package com.matt.nocom.server.exception;

import com.matt.nocom.server.model.ApiError;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    ApiError error = ApiError.builder()
        .status(HttpStatus.BAD_REQUEST)
        .message(ex.getLocalizedMessage())
        .errors(ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.toList())
        )
        .build();
    return handleExceptionInternal(
        ex,
        error,
        headers,
        error.getStatus(),
        request
    );
  }
}
