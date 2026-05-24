$pipeName = "docker_engine"
$tcpPort = 12375
$listener = New-Object System.Net.Sockets.TcpListener([System.Net.IPAddress]::Loopback, $tcpPort)
$listener.Start()
Write-Host "Docker TCP proxy listening on 127.0.0.1:$tcpPort -> npipe://$pipeName"

while ($true) {
    $tcpClient = $listener.AcceptTcpClient()
    Write-Host "Accepted connection"
    $stream = $tcpClient.GetStream()
    
    $pipeClient = New-Object System.IO.Pipes.NamedPipeClientStream(".", $pipeName, [System.IO.Pipes.PipeDirection]::InOut)
    $pipeClient.Connect(5000)
    Write-Host "Connected to pipe"
    
    $pipeWriter = New-Object System.IO.BinaryWriter($pipeClient)
    $pipeReader = New-Object System.IO.BinaryReader($pipeClient)
    
    # Read from TCP and forward to pipe
    $buffer = New-Object byte[] 65536
    
    # First, read the HTTP request from TCP
    do {
        $read = $stream.Read($buffer, 0, $buffer.Length)
        if ($read -gt 0) {
            $pipeWriter.Write($buffer, 0, $read)
            $pipeWriter.Flush()
            Write-Host "Forwarded $read bytes to pipe"
            
            # Check if we've received the complete request (end with \r\n\r\n)
            $requestText = [System.Text.Encoding]::ASCII.GetString($buffer, 0, $read)
            if ($requestText.Contains("`r`n`r`n")) {
                break
            }
        }
    } while ($read > 0)
    
    Start-Sleep -Milliseconds 200
    
    # Read response from pipe and forward to TCP
    $totalRead = 0
    do {
        try {
            $read = $pipeReader.Read($buffer, 0, $buffer.Length)
            if ($read -gt 0) {
                $stream.Write($buffer, 0, $read)
                $stream.Flush()
                $totalRead += $read
                Write-Host "Forwarded $read bytes to TCP (total: $totalRead)"
            }
        } catch {
            break
        }
        if ($totalRead -gt 0 -and $read -eq 0) { break }
    } while ($true)
    
    Write-Host "Done ($totalRead bytes total)"
    $pipeClient.Dispose()
    $stream.Dispose()
    $tcpClient.Dispose()
}
