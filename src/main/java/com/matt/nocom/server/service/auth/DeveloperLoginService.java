package com.matt.nocom.server.service.auth;

import com.google.common.collect.Lists;
import com.matt.nocom.server.exception.InvalidPasswordException;
import com.matt.nocom.server.exception.InvalidUsernameException;
import com.matt.nocom.server.model.shared.auth.UserGroup;
import com.matt.nocom.server.model.sql.auth.AccessToken;
import com.matt.nocom.server.model.sql.auth.User;
import com.matt.nocom.server.service.ApplicationSettings;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DeveloperLoginService extends DatabaseLoginService {
  
  private final User root = User.builder()
      .id(-1)
      .username("")
      .passwordPlaintext("")
      .group(UserGroup.ROOT)
      .enabled(true)
      .debugUser(true)
      .build();
  private final List<AccessToken> tokens = Lists.newArrayList();
  
  @Autowired
  public DeveloperLoginService(DSLContext dsl, PasswordEncoder passwordEncoder,
      ApplicationSettings settings) {
    super(dsl, passwordEncoder, settings);
  
    root.setUsername(settings.getDevUsername());
    root.setPassword(passwordEncoder.encode(settings.getDevPassword()));
  }
  
  @Override
  public int addUser(String username, String plaintextPassword, int level, boolean enabled)
      throws InvalidUsernameException, InvalidPasswordException {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return 0;
    } else {
      return super.addUser(username, plaintextPassword, level, enabled);
    }
  }
  
  @Override
  public int removeUser(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return 0;
    } else {
      return super.removeUser(username);
    }
  }
  
  @Override
  public int setUserPassword(String username, String plaintextPassword) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      settings.checkPassword(plaintextPassword);
      root.setPassword(passwordEncoder.encode(plaintextPassword));
      return 1;
    } else {
      return super.setUserPassword(username, plaintextPassword);
    }
  }
  
  @Override
  public Optional<String> getUserPassword(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return Optional.of(root.getPassword());
    } else {
      return super.getUserPassword(username);
    }
  }
  
  @Override
  public int setUserEnabled(String username, boolean enabled) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      root.setEnabled(enabled);
      return 1;
    } else {
      return super.setUserEnabled(username, enabled);
    }
  }
  
  @Override
  public Optional<Boolean> getUserEnabled(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return Optional.of(root.isEnabled());
    } else {
      return super.getUserEnabled(username);
    }
  }
  
  @Override
  public int setUserLevel(String username, int level) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      root.setLevel(level);
      return 1;
    } else {
      return super.setUserLevel(username, level);
    }
  }
  
  @Override
  public Optional<Integer> getUserLevel(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return Optional.of(root.getLevel());
    } else {
      return super.getUserLevel(username);
    }
  }
  
  @Override
  public int setUserLastLogin(String username, long time) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      root.setLastLogin(time);
      return 1;
    } else {
      return super.setUserLastLogin(username, time);
    }
  }
  
  @Override
  public Optional<Long> getUserLastLogin(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return Optional.of(root.getLastLogin());
    } else {
      return super.getUserLastLogin(username);
    }
  }
  
  @Override
  public List<User> getUsers() {
    List<User> users = super.getUsers();
    users.removeIf(user -> root.getUsername().equalsIgnoreCase(user.getUsername()));
    users.add(0, root);
    return users;
  }
  
  @Override
  public Optional<User> getUser(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return Optional.of(root);
    } else {
      return super.getUser(username);
    }
  }
  
  @Override
  public Optional<User> getUserById(int id) {
    if (root.getId() == id) {
      return Optional.of(root);
    } else {
      return super.getUserById(id);
    }
  }
  
  @Override
  public Optional<Integer> getIdByUsername(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return Optional.of(root.getId());
    } else {
      return super.getIdByUsername(username);
    }
  }
  
  @Override
  public List<String> getUsernames() {
    List<String> usernames = super.getUsernames();
    usernames.removeIf(user -> root.getUsername().equalsIgnoreCase(user));
    usernames.add(0, root.getUsername());
    return usernames;
  }
  
  @Override
  public boolean usernameExists(String username) {
    return root.getUsername().equalsIgnoreCase(username) || super.usernameExists(username);
  }
  
  @Override
  public List<AccessToken> getTokens() {
    List<AccessToken> tokens = super.getTokens();
    tokens.addAll(this.tokens);
    return tokens;
  }
  
  @Override
  public List<AccessToken> getUserTokens(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      return tokens;
    } else {
      return super.getUserTokens(username);
    }
  }
  
  @Override
  public Optional<String> getUsernameByToken(UUID token, InetAddress address) {
    if (tokens.stream()
        .anyMatch(at -> at.getToken().equals(token) && at.getAddress().equals(address))) {
      return Optional.of(root.getUsername());
    } else {
      return super.getUsernameByToken(token, address);
    }
  }
  
  @Override
  public int addUserToken(String username, AccessToken token) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      tokens.add(token);
      return 1;
    } else {
      return super.addUserToken(username, token);
    }
  }
  
  @Override
  public int revokeToken(UUID token) {
    if (tokens.removeIf(at -> at.getToken().equals(token))) {
      return 1;
    } else {
      return super.revokeToken(token);
    }
  }
  
  @Override
  public int revokeUserTokens(String username) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      int n = tokens.size();
      tokens.clear();
      return n;
    } else {
      return super.revokeUserTokens(username);
    }
  }
  
  @Override
  public int revokeUserTokens(String username, Collection<UUID> uuids) {
    if (root.getUsername().equalsIgnoreCase(username)) {
      int n = tokens.size();
      tokens.removeIf(at -> uuids.contains(at.getToken()));
      return n - tokens.size();
    } else {
      return super.revokeUserTokens(username, uuids);
    }
  }
  
  @Override
  public Optional<Long> getNextTokenExpirationTime() {
    Optional<Long> o1 = super.getNextTokenExpirationTime();
    Optional<Long> o2 = tokens.stream()
        .map(AccessToken::getExpiresOn)
        .min(Comparator.comparingLong(Long::longValue));
    if (o1.isPresent() && o2.isPresent()) {
      return o1.get() < o2.get() ? o1 : o2;
    } else {
      return o1.isPresent() ? o1 : o2;
    }
  }
  
  @Override
  public int clearExpiredTokens() {
    int n = tokens.size();
    tokens.removeIf(at -> at.getExpiresOn() >= System.currentTimeMillis());
    return super.clearExpiredTokens() + (n - tokens.size());
  }
}
