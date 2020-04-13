package com.matt.nocom.server.properties;

import com.matt.nocom.server.exception.InvalidPasswordException;
import com.matt.nocom.server.exception.InvalidUsernameException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
@ConfigurationProperties("nocom.authentication")
public class AuthenticationProperties {
  private int usernameMinLength = 2;
  private int usernameMaxLength = 16;
  private int passwordMinLength = 8;
  private int passwordMaxLength = 64;
  private Pattern usernameAllowedChars = Pattern.compile("[A-Za-z0-9_]+");
  private Duration tokenLifetime = Duration.ofDays(1);
  private int userMaxLevel = 100;
  private int userMinLevel = 0;
  private List<String> reservedUsernames = Arrays.asList("root", "admin");
  private List<String> debugUsers = Arrays.asList("root:pass", "admin:pass");

  public boolean validUsernameCharacters(String username) {
    return getUsernameAllowedChars().matcher(username).matches();
  }

  public boolean validUsernameLength(String username) {
    return username.length() >= getUsernameMinLength()
        && username.length() <= getUsernameMaxLength();
  }

  public boolean usernameNotReserved(String username) {
    return getReservedUsernames().stream().anyMatch(username::equalsIgnoreCase);
  }

  public boolean validPassword(String passwordUnencrypted) {
    return passwordUnencrypted.length() >= getPasswordMinLength()
        && passwordUnencrypted.length() <= getPasswordMaxLength();
  }

  public void checkUsername(String username) throws InvalidUsernameException {
    checkIfNullUsername(username);
    if (!validUsernameLength(username)) {
      throw new InvalidUsernameException(
          "Username must be at least " + getUsernameMinLength() + " characters long.");
    } else if (!validUsernameCharacters(username)) {
      throw new InvalidUsernameException("Username contains illegal characters.");
    } else if (!usernameNotReserved(username)) {
      throw new InvalidUsernameException("Username is reserved");
    }
  }

  public void checkPassword(String passwordUnencrypted) throws InvalidPasswordException {
    if (passwordUnencrypted == null) {
      throw new InvalidPasswordException("Password is null.");
    } else if (!validPassword(passwordUnencrypted)) {
      throw new InvalidPasswordException(
          "Password must be at least " + getPasswordMinLength()
              + " characters long.");
    }
  }

  public void checkIfNullUsername(@Nullable String username)
      throws InvalidUsernameException {
    if (username == null || username.isEmpty()) {
      throw new InvalidUsernameException("Username is null.");
    }
  }
}
