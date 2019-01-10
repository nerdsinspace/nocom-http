package com.matt.nocom.server.exception;

public class IllegalUsernameException extends RuntimeException {

  public IllegalUsernameException() {
    super("Username contains illegal characters");
  }
}
