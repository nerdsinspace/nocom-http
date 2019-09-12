package com.matt.nocom.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InvalidPasswordException extends RuntimeException {
  
  public InvalidPasswordException(String message) {
    super(message);
  }
}
