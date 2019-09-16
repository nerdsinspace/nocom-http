package com.matt.nocom.server.controller;

import com.google.common.base.Strings;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.exception.InvalidUsernameException;
import com.matt.nocom.server.model.http.auth.HttpCredentials;
import com.matt.nocom.server.model.http.auth.HttpUserRegistration;
import com.matt.nocom.server.model.http.auth.HttpUsernameToken;
import com.matt.nocom.server.model.sql.auth.AccessToken;
import com.matt.nocom.server.model.sql.auth.User;
import com.matt.nocom.server.service.ApplicationSettings;
import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.service.auth.LoginService;
import com.matt.nocom.server.service.auth.UserAuthenticationProvider;
import com.matt.nocom.server.util.EventTypeRegistry;
import com.matt.nocom.server.util.StaticUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("user")
public class UserController implements Logging {
  
  private final ApplicationSettings settings;
  private final AuthenticationManager auth;
  private final LoginService login;
  private final UserAuthenticationProvider authProvider;
  private final EventService events;

  @Autowired
  public UserController(ApplicationSettings settings,
      AuthenticationManager auth, LoginService login,
      UserAuthenticationProvider authProvider, EventService events) {
    this.settings = settings;
    this.auth = auth;
    this.login = login;
    this.authProvider = authProvider;
    this.events = events;
  }

  @RequestMapping(value = "/login",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity login(
      @RequestBody HttpCredentials details,
      HttpServletRequest request) {
    Authentication authentication = auth.authenticate(
        new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword()));
  
    // if we get here then we have successfully authenticated
    User user = (User) authProvider.loadUserByUsername(details.getUsername());
    SecurityContextHolder.getContext().setAuthentication(user.toAuthenticationToken());
  
    AccessToken token = AccessToken.builder()
        .token(UUID.randomUUID())
        .expiresOn(System.currentTimeMillis() + settings.getTokenExpiration())
        .address(settings.getRemoteAddr(request))
        .build();
  
    events.publishInfo(authentication, EventTypeRegistry.USER__LOGIN, "Logged in via API");
    login.setUserLastLogin(user.getUsername(), System.currentTimeMillis());
  
    if(login.addUserToken(user.getUsername(), token) > 0) {
      events.publishInfo(EventTypeRegistry.USER__LOGIN__CREATE_TOKEN,
          "Created new access token");
    
      return ResponseEntity.ok(HttpUsernameToken.builder()
          .username(user.getUsername())
          .token(token.getToken())
          .build());
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Access token could not be created");
    }
  }

  @RequestMapping(value = "/registered",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<String[]> getRegistered() {
    return ResponseEntity.ok(login.getUsernames().toArray(new String[0]));
  }

  @RequestMapping(value = "/register",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void register(@RequestBody HttpUserRegistration details) {
    settings.checkIfNullUsername(details.getUsername());
    checkAllowedLevel(details.getLevel());
    
    if(login.usernameExists(details.getUsername()))
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Username already exists.");
  
    // add the user to the database
    settings.checkUsername(details.getUsername());
    settings.checkPassword(details.getPassword());
    if (login.addUser(details.getUsername(), details.getPassword(),
        details.getLevel(), true) > 0) {
      events.publishInfo(EventTypeRegistry.USER__REGISTER,
          "Registered a new user '%s'", details.getUsername());
    }
  }

  @RequestMapping(value = "/unregister/{username}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void unregister(
      @PathVariable("username") String username,
      @RequestParam("verificationPassword") Optional<String> verificationPassword) {
    settings.checkIfNullUsername(username);
    checkAccessPrivileges(username, verificationPassword);
  
    if (login.removeUser(username) > 0) {
      events.publishInfo(EventTypeRegistry.USER__UNREGISTER,
          "Unregistered user '%s'", username);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user");
    }
  }
  
  @RequestMapping(value = "/tokens/revoke/{username}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void revokeUserTokens(
      @PathVariable("username") String username,
      @RequestParam("uuids[]") List<UUID> uuids,
      @RequestParam("verificationPassword") Optional<String> verificationPassword) {
    settings.checkIfNullUsername(username);
    checkAccessPrivileges(username, verificationPassword);
    
    int n;
    if ((n = login.revokeUserTokens(username, uuids)) > 0) {
      events.publishInfo(EventTypeRegistry.USER__EXPIRE_TOKENS,
          "Revoked %d/%d of %s's access token(s)", n, uuids.size(), username);
    }
    
    // it's fine if nothing is removed
  }
  
  @RequestMapping(value = "/set/password/{username}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void setUserPassword(
      @PathVariable("username") String username,
      @RequestParam("password") String plaintextPassword,
      @RequestParam("verificationPassword") Optional<String> verificationPassword) {
    settings.checkIfNullUsername(username);
    checkAccessPrivileges(username, verificationPassword);
    
    settings.checkPassword(plaintextPassword);
    if (login.setUserPassword(username, plaintextPassword) > 0) {
      events.publishInfo(EventTypeRegistry.USER__SET_PASSWORD,
          "Changed password for %s", username);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to set password");
    }
  }
  
  @RequestMapping(value = "/set/enabled/{username}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public int setUserEnabled(
      @PathVariable("username") String username,
      @RequestParam("enabled") boolean enabled,
      @RequestParam("verificationPassword") Optional<String> verificationPassword) {
    settings.checkIfNullUsername(username);
    checkAccessPrivileges(username);
    
    if (login.setUserEnabled(username, enabled) > 0) {
      events.publishInfo(EventTypeRegistry.USER__SET_ENABLE,
          "%s user %s", enabled ? "Enabled" : "Disabled", username);
      return login.getUserEnabled(username)
          .map(b -> b ? 1 : 0)
          .orElseThrow(() -> new InvalidUsernameException("Unknown username"));
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to enable/disable user");
    }
  }
  
  @RequestMapping(value = "/set/level/{username}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void setUserLevel(
      @PathVariable("username") String username,
      @RequestParam("level") int level,
      @RequestParam("currentPassword") Optional<String> verificationPassword) {
    settings.checkIfNullUsername(username);
    checkAccessPrivileges(username, verificationPassword);
    checkAllowedLevel(level);
    
    if (login.setUserLevel(username, level) > 0) {
      events.publishInfo(EventTypeRegistry.USER__SET_LEVEL,
          "Set %s level to %d", username, level);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to set users level");
    }
  }
  
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Bad credentials")
  public void handleException(AuthenticationException e) {
  }
  
  private void checkAccessPrivileges(String username, Optional<String> plaintextPassword) {
    // the user invoking this api
    String accessor = StaticUtils.getCurrentUserContext()
        .map(User::getUsername)
        .filter(login::usernameExists)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    
    if (!login.usernameExists(username)) {
      throw new InvalidUsernameException("Unknown username.");
    }
    
    if (!accessor.equalsIgnoreCase(username)) {
      // if not editing self, we must verify this user has a level higher than he is
      // trying to edit
      int accessorLevel = login.getUserLevel(accessor)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
      int subjectLevel = login.getUserLevel(username)
          .orElseThrow(() -> new InvalidUsernameException("Unknown username."));
      
      if (subjectLevel >= accessorLevel) {
        // invoker must have a level that is greater than their subject
        // otherwise they must provide a confirmation password
        String pw = plaintextPassword
            .map(Strings::emptyToNull)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Must provide verification password"
            ));
        
        // will throw @AuthenticationException if it fails
        auth.authenticate(new UsernamePasswordAuthenticationToken(accessor, pw));
      }
    }
    
    // if we reach here then there are no issues
  }
  
  private void checkAccessPrivileges(String username) {
    checkAccessPrivileges(username, Optional.empty());
  }
  
  private void checkAllowedLevel(int level) {
    // cap the level to be between 0 and 100
    if (level < 0 || level > 100) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
          "Level must be in range of 0-100");
    }
    
    // get current user accessing  this api
    int maxLevel = StaticUtils.getCurrentUserContext()
        .map(User::getUsername)
        .flatMap(login::getUserLevel)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    
    // only allow user to set level at or below their own
    if (level > maxLevel) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
          "Cannot set level above your own");
    }
  }
}
