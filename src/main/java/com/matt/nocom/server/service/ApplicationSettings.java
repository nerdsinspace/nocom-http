package com.matt.nocom.server.service;

import static com.matt.nocom.server.util.StaticUtils.stringToAddress;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.exception.InvalidPasswordException;
import com.matt.nocom.server.exception.InvalidUsernameException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import org.jooq.SQLDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySources({
    @PropertySource("classpath:settings.properties"),
    @PropertySource(value = "${nocom.settings}", ignoreResourceNotFound = true)
})
@Getter
public class ApplicationSettings implements Logging {
  
  @Getter(AccessLevel.NONE)
	private final Environment env;
	
	private SQLDialect dialect;
	private String devUsername;
	private String devPassword;
	private Path admins;
	private long tokenExpiration;
	private Pattern usernameAllowedChars;
	private int usernameMinLength;
	private List<String> usernameReserved;
	private int passwordMinLength;
	private int userLevelMin;
	private int userLevelMax;
	private Path worlds;
	private Path spigot;
	private List<String> httpHeaders;
	
	@Autowired
  public ApplicationSettings(Environment env) {
    this.env = env;
    
    dialect = SQLDialect.valueOf(property("app.sql.dialect"));
    devUsername = property("app.dev.username");
    devPassword = property("app.dev.password");
    admins = Paths.get(property("app.admins"));
    tokenExpiration = Long.parseLong(property("app.token.expiration"));
    usernameAllowedChars = Pattern.compile(property("app.username.allowed.chars"));
    usernameMinLength = Integer.parseInt(property("app.username.length.min"));
    usernameReserved = Arrays.asList(property("app.username.reserved").split(","));
    passwordMinLength = Integer.parseInt(property("app.password.length.min"));
    userLevelMin = Integer.parseInt(property("app.user.level.min"));
    userLevelMax = Integer.parseInt(property("app.user.level.max"));
    worlds = Paths.get(property("app.worlds"));
    spigot = Paths.get(property("app.spigot"));
    httpHeaders = Arrays.asList(property("app.http.headers.real.ip").split(","));
  }
  
  private String property(String prop) {
	  String o = env.getProperty(prop);
	  Objects.requireNonNull(o, prop);
	  return o;
  }
  
  public InetAddress getRemoteAddr(ServletRequest request) {
    return Optional.of(request)
        .filter(HttpServletRequest.class::isInstance)
        .map(HttpServletRequest.class::cast)
        .map(this::getRemoteAddr)
        .orElseThrow(() -> new Error("request is not of a HttpServletRequest type"));
  }
  
  public InetAddress getRemoteAddr(HttpServletRequest request) {
    return Optional.of(request)
        .map(req -> {
          // for reverse proxy compatibility
          for(String header : getHttpHeaders()) {
            String content = req.getHeader(header);
            if(content != null) {
              try {
                return stringToAddress(content); // return first valid header
              } catch (Throwable t) {
                LOGGER.warn("{} is not a valid ip header", content);
              }
            }
          }
          return null;
        })
        .orElse(stringToAddress(request.getRemoteAddr()));
  }
  
  public boolean validUsernameCharacters(String username) {
    return getUsernameAllowedChars().matcher(username).matches();
  }
  
  public boolean validUsernameLength(String username) {
    return username.length() >= getUsernameMinLength();
  }
  
  public boolean usernameNotReserved(String username) {
    for (String un : getUsernameReserved()) {
      if (username.equalsIgnoreCase(un)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean validPassword(String passwordUnencrypted) {
    return passwordUnencrypted.length() >= getPasswordMinLength();
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
