package com.example.Demotion.Common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorDomain.class)
    public ResponseEntity<Object> handleErrorDomain(ErrorDomain ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(
                        ex.getCode(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse(
                        1500,
                        "서버 오류가 발생했습니다.",
                        null
                ));
    }
}
