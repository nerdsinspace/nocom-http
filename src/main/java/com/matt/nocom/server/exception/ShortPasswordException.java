package com.matt.nocom.server.exception;

import com.matt.nocom.server.Properties;

public class ShortPasswordException extends RuntimeException {

  public ShortPasswordException() {
    super("Password must be at least " + Properties.MIN_PASSWORD_LEN + " characters long.");
  }
}
