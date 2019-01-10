package com.matt.nocom.server.exception;

public class IllegalPasswordException extends RuntimeException {
  public IllegalPasswordException() {
    super("password too short");
  }
}
