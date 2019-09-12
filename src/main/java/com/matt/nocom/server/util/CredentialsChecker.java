package com.matt.nocom.server.util;

import com.matt.nocom.server.Properties;
import com.matt.nocom.server.exception.InvalidPasswordException;
import com.matt.nocom.server.exception.InvalidUsernameException;
import javax.annotation.Nullable;

public class CredentialsChecker {
  
  public static boolean validUsernameCharacters(String username) {
    return Properties.USERNAME_ALLOWED_CHARACTERS.matcher(username).matches();
  }
  
  public static boolean validUsernameLength(String username) {
    return username.length() >= Properties.USERNAME_MIN_CHARACTERS;
  }
  
  public static boolean usernameNotReserved(String username) {
    for (String un : Properties.USERNAME_RESERVED_NAMES) {
      if (username.equalsIgnoreCase(un)) {
        return false;
      }
    }
    return true;
  }

  public static boolean validPassword(String passwordUnencrypted) {
    return passwordUnencrypted.length() >= Properties.PASSWORD_MIN_CHARACTERS;
  }
  
  public static void checkUsername(String username) throws InvalidUsernameException {
    if (username == null) {
      throw new InvalidUsernameException("Username is null.");
    } else if (!validUsernameLength(username)) {
      throw new InvalidUsernameException(
          "Username must be at least " + Properties.USERNAME_MIN_CHARACTERS + " characters long.");
    } else if (!validUsernameCharacters(username)) {
      throw new InvalidUsernameException("Username contains illegal characters.");
    } else if (!usernameNotReserved(username)) {
      throw new InvalidUsernameException("Username is reserved");
    }
  }
  
  public static void checkPassword(String passwordUnencrypted) throws InvalidPasswordException {
    if (passwordUnencrypted == null) {
      throw new InvalidPasswordException("Password is null.");
    } else if (!validPassword(passwordUnencrypted)) {
      throw new InvalidPasswordException(
          "Password must be at least " + Properties.PASSWORD_MIN_CHARACTERS
              + " characters long.");
    }
  }
  
  public static void checkIfNullUsername(@Nullable String username)
      throws InvalidUsernameException {
    if (username == null || username.isEmpty()) {
      throw new InvalidUsernameException("Username is null.");
    }
  }
}
