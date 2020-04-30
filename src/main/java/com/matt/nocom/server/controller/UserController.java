package com.matt.nocom.server.controller;

import com.google.common.base.Strings;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.exception.InvalidUsernameException;
import com.matt.nocom.server.model.auth.AuthenticatedUser;
import com.matt.nocom.server.model.auth.HttpUserRegistration;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.properties.AuthenticationProperties;
import com.matt.nocom.server.service.EventRepository;
import com.matt.nocom.server.service.auth.UserRepository;
import com.matt.nocom.server.util.StaticUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements Logging {

  private final AuthenticationProperties properties;
  private final AuthenticationManager auth;
  private final UserRepository users;
  private final EventRepository events;

  @GetMapping("/registered")
  @ResponseBody
  public ResponseEntity<String[]> getRegistered() {
    return ResponseEntity.ok(users.getUsernames().toArray(new String[0]));
  }

  @PostMapping("/register")
  public void register(@RequestBody HttpUserRegistration details) {
    properties.checkIfNullUsername(details.getUsername());
    checkAllowedLevel(details.getLevel());

    if (users.usernameExists(details.getUsername())) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Username already exists.");
    }

    // add the user to the database
    if (users.addUser(details.getUsername(), details.getPassword(),
        details.getLevel(), true) > 0) {
      events.publishUser("/user/register", "Registered a new user '%s'", details.getUsername());
    }
  }

  @GetMapping("/unregister/{username}")
  public void unregister(
      @PathVariable("username") String username) {
    properties.checkIfNullUsername(username);
    checkAccessPrivileges(username, Optional.empty());

    if (users.removeUser(username) > 0) {
      events.publishUser("/user/unregister", "Unregistered user '%s'", username);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user");
    }
  }

  @PostMapping("/set/password/{username}")
  public void setUserPassword(
      @PathVariable("username") String username,
      @RequestParam("password") String plaintextPassword) {
    properties.checkIfNullUsername(username);
    checkAccessPrivileges(username, Optional.empty());

    if (users.setUserPassword(username, plaintextPassword) > 0) {
      events.publishUser("/user/set/password/{username}", "Changed password for %s", username);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to set password");
    }
  }

  @PostMapping("/set/enabled/{username}")
  @ResponseBody
  public boolean setUserEnabled(
      @PathVariable("username") String username,
      @RequestParam("enabled") boolean enabled) {
    properties.checkIfNullUsername(username);
    checkAccessPrivileges(username);

    if (users.setUserEnabled(username, enabled) > 0) {
      events.publishUser("/user/set/enabled/{username}",
          "%s user %s", enabled ? "Enabled" : "Disabled", username);

      return users.getUserEnabled(username)
          .orElseThrow(() -> new InvalidUsernameException("Unknown username"));
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to enable/disable user");
    }
  }

  @PostMapping("/set/level/{username}")
  public void setUserLevel(
      @PathVariable("username") String username,
      @RequestParam("level") int level) {
    properties.checkIfNullUsername(username);
    checkAccessPrivileges(username);
    checkAllowedLevel(level);

    if (users.setUserLevel(username, level) > 0) {
      events.publishUser("/user/set/level/{username}", "Set %s level to %d", username, level);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to set users level");
    }
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad credentials")
  public void handleException(AuthenticationException e) {
  }

  private void checkAccessPrivileges(String username, Optional<String> plaintextPassword) {
    // the user invoking this api
    String accessor = StaticUtils.getCurrentUserContext()
        .map(User::getUsername)
        .filter(users::usernameExists)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    if (!users.usernameExists(username)) {
      throw new InvalidUsernameException("Unknown username.");
    }

    if (!accessor.equalsIgnoreCase(username)) {
      // if not editing self, we must verify this user has a level higher than he is
      // trying to edit
      int accessorLevel = users.getUserLevel(accessor)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
      int subjectLevel = users.getUserLevel(username)
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
        .flatMap(users::getUserLevel)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    // only allow user to set level at or below their own
    if (level > maxLevel) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
          "Cannot set level above your own");
    }
  }
}
