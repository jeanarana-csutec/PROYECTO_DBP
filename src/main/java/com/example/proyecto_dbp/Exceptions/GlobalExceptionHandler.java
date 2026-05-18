package com.example.proyecto_dbp.Exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExists.class)
    public ProblemDetail notfound(UserAlreadyExists e, HttpServletRequest r){
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Operacion con usuario no existente");
        pd.setDetail(e.getMessage());
        pd.setInstance(URI.create(r.getRequestURI()));
        return pd;
    }
}
