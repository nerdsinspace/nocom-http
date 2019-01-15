package com.matt.nocom.server.util;

import com.matt.nocom.server.Properties;
import com.matt.nocom.server.exception.IllegalUsernameException;
import com.matt.nocom.server.exception.ShortPasswordException;

public class CredentialsChecker {
  public static boolean validUsername(String username) {
    return username.matches("[A-Za-z0-9_]+");
  }

  public static boolean validPassword(String passwordUnencrypted) {
    return passwordUnencrypted.length() >= Properties.MIN_PASSWORD_LEN;
  }

  public static void checkUsername(String username) {
    if(!validUsername(username)) throw new IllegalUsernameException();
  }

  public static void checkPasswordLength(String passwordUnencrypted) {
    if(!validPassword(passwordUnencrypted)) throw new ShortPasswordException();
  }
}
