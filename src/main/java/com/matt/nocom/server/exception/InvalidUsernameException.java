package com.matt.nocom.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InvalidUsernameException extends RuntimeException {
  
  public InvalidUsernameException(String message) {
    super(message);
  }
}
