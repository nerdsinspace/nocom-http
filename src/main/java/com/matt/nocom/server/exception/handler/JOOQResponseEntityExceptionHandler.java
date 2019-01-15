package com.matt.nocom.server.exception.handler;

import com.matt.nocom.server.model.ApiError;
import org.jooq.exception.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class JOOQResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(DataAccessException.class)
  protected ResponseEntity<Object> handleConflict(
      RuntimeException ex, WebRequest request) {
    ApiError error = ApiError.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .message("Problem accessing the database")
        .error(ex.getLocalizedMessage())
        .build();
    return handleExceptionInternal(ex,
        error,
        new HttpHeaders(),
        error.getStatus(),
        request
    );
  }
}
