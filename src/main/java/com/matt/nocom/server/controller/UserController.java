package com.matt.nocom.server.controller;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.exception.IllegalUsernameException;
import com.matt.nocom.server.exception.ShortPasswordException;
import com.matt.nocom.server.auth.UserGroup;
import com.matt.nocom.server.model.auth.UserRegistration;
import com.matt.nocom.server.model.auth.UsernamePassword;
import com.matt.nocom.server.model.auth.UsernameToken;
import com.matt.nocom.server.auth.AccessToken;
import com.matt.nocom.server.auth.User;
import com.matt.nocom.server.service.LoginManagerService;
import com.matt.nocom.server.util.CredentialsChecker;
import com.matt.nocom.server.util.Util;
import com.matt.nocom.server.util.factory.AccessTokenFactory;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final AuthenticationManager auth;
  private final LoginManagerService login;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserController(AuthenticationManager auth,
      LoginManagerService login, PasswordEncoder passwordEncoder) {
    this.auth = auth;
    this.login = login;
    this.passwordEncoder = passwordEncoder;
  }

  @RequestMapping(value = "/login",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity login(
      @RequestBody UsernamePassword details,
      HttpServletRequest request) {
    User user = login.getUser(details.getUsername())
        .filter(User::isNotDebugUser)
        .map(u -> User.builder()
            .username(u.getUsername())
            .password(details.getPassword())
            .enabled(u.isEnabled())
            .groups(u.getGroups())
            .build())
        .orElseGet(() -> User.builder()
            .username(details.getUsername())
            .password(details.getPassword())
            .build());

    Authentication authentication = auth.authenticate(user.toAuthenticationToken());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    AccessToken token = AccessTokenFactory.generate(Util.stringToAddress(request.getRemoteAddr()));
    login.addUserToken(user.getUsername(), token);

    return ResponseEntity.ok(UsernameToken.builder()
        .username(user.getUsername())
        .token(token.getToken())
        .build());
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
  public void register(@RequestBody UserRegistration details) {
    if(login.usernameExists(details.getUsername()))
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Username already exists.");

    try {
      CredentialsChecker.checkUsername(details.getUsername());
      CredentialsChecker.checkPasswordLength(details.getPassword());

      User user = User.builder()
          .username(details.getUsername())
          .password(passwordEncoder.encode(details.getPassword()))
          .groups(details.getGroups().stream()
              .filter(UserGroup::isActive)
              .collect(Collectors.toSet()))
          .build();

      // add the user to the database
      login.addUser(user);

      // add the user to any groups provided
      login.addUserToGroups(user.getUsername(), user.getGroups().toArray(new UserGroup[0]));
    } catch (IllegalUsernameException | ShortPasswordException e) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getLocalizedMessage(), e);
    }
  }

  @RequestMapping(value = "/unregister/{username}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void unregister(@PathVariable("username") String username) {
    login.removeUser(username);
  }

  @RequestMapping(value = "/tokens",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<AccessToken[]> getAccessTokens() {
    return ResponseEntity.ok(login.getTokens().stream()
        .sorted(Comparator.comparingLong(AccessToken::getExpiresOn))
        .toArray(AccessToken[]::new));
  }

  @RequestMapping(value = "/tokens/user/{username}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<AccessToken[]> getUserAccessTokens(@PathVariable("username") String username) {
    return ResponseEntity.ok(login.getUserTokens(username).stream()
        .sorted(Comparator.comparingLong(AccessToken::getExpiresOn))
        .toArray(AccessToken[]::new));
  }

  @RequestMapping(value = "/tokens/user/{username}/expire",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void expireUserTokens(@PathVariable("username") String username) {
    login.expireUserTokens(username);
  }

  @RequestMapping(value = "/tokens/expire/{uuid}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void expireUuid(@PathVariable("uuid") String uuid) {
    login.expireToken(UUID.fromString(uuid));
  }

  @RequestMapping(value = "/set/enabled/{username}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void setUserEnabled(@PathVariable("username") String username,
      @RequestParam("enabled") boolean enabled) {
    login.setUserEnabled(username, enabled);
  }
}
