param(
    [int]$Port = 12375,
    [string]$PipeName = "docker_engine"
)

$listener = New-Object System.Net.Sockets.TcpListener([System.Net.IPAddress]::Loopback, $Port)
$listener.Start()
Write-Host "Docker TCP proxy listening on 127.0.0.1:$Port -> npipe://$PipeName"
Write-Host "Set DOCKER_HOST=tcp://127.0.0.1:$Port"

while ($true) {
    $tcpClient = $listener.AcceptTcpClient()
    Write-Host "`nAccepted TCP connection"
    $tcpStream = $tcpClient.GetStream()
    
    try {
        $pipeClient = New-Object System.IO.Pipes.NamedPipeClientStream(".", $PipeName, [System.IO.Pipes.PipeDirection]::InOut)
        $pipeClient.Connect(5000)
        Write-Host "Connected to pipe $PipeName"
        
        # Read HTTP request from TCP (up to double CRLF)
        $requestBytes = New-Object System.Collections.Generic.List[byte]
        $buffer = New-Object byte[] 1
        $foundEnd = $false
        $consecutiveNewlines = 0
        
        while ($tcpClient.Connected) {
            $read = $tcpStream.Read($buffer, 0, 1)
            if ($read -le 0) { break }
            $requestBytes.Add($buffer[0])
            
            if ($buffer[0] -eq 10) {  # \n
                $consecutiveNewlines++
                if ($consecutiveNewlines -eq 2) { $foundEnd = $true; break }
            } elseif ($buffer[0] -ne 13) {  # not \r
                $consecutiveNewlines = 0
            }
        }
        
        if ($requestBytes.Count -gt 0) {
            Write-Host "Forwarding $($requestBytes.Count) byte request to pipe"
            $pipeClient.Write($requestBytes.ToArray(), 0, $requestBytes.Count)
            $pipeClient.Flush()
            Write-Host "Sent to pipe, reading response..."
            
            # Read response from pipe and forward to TCP
            $responseBuffer = New-Object byte[] 65536
            $totalRead = 0
            $stallCount = 0
            
            do {
                if ($pipeClient.IsConnected) {
                    try {
                        $read = $pipeClient.Read($responseBuffer, 0, $responseBuffer.Length)
                        if ($read -gt 0) {
                            $tcpStream.Write($responseBuffer, 0, $read)
                            $tcpStream.Flush()
                            $totalRead += $read
                            Write-Host "Forwarded $read bytes to TCP (total: $totalRead)"
                            $stallCount = 0
                        } else {
                            $stallCount++
                            Start-Sleep -Milliseconds 50
                        }
                    } catch {
                        Write-Host "Pipe read error: $_"
                        break
                    }
                } else {
                    break
                }
            } while ($totalRead -eq 0 -or $stallCount -lt 200)  # ~10 second timeout after last data
            
            Write-Host "Response complete ($totalRead bytes)"
        }
        
        $pipeClient.Dispose()
    } catch {
        Write-Host "Error: $_"
    } finally {
        $tcpStream.Dispose()
        $tcpClient.Dispose()
    }
}
