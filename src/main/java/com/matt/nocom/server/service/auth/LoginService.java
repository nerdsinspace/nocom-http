package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.sql.auth.AccessToken;
import com.matt.nocom.server.model.sql.auth.User;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoginService extends Logging {
  
  int addUser(String username, String plaintextPassword, int level, boolean enabled);
  
  int removeUser(String username);
  
  int setUserPassword(String username, String plaintextPassword);
  
  Optional<String> getUserPassword(String username);
  
  int setUserEnabled(String username, boolean enabled);
  
  Optional<Boolean> getUserEnabled(String username);
  
  int setUserLevel(String username, int level);
  
  Optional<Integer> getUserLevel(String username);
  
  int setUserLastLogin(String username, long time);
  
  Optional<Long> getUserLastLogin(String username);
  
  List<User> getUsers();
  
  Optional<User> getUser(String username);
  
  Optional<User> getUserById(int id);
  
  Optional<Integer> getIdByUsername(String username);
  
  List<String> getUsernames();
  
  boolean usernameExists(String username);
  
  List<AccessToken> getTokens();
  
  List<AccessToken> getUserTokens(String username);
  
  Optional<String> getUsernameByToken(UUID token, InetAddress address);
  
  int addUserToken(String username, AccessToken token);
  
  int revokeToken(UUID token);
  
  int revokeUserTokens(String username);
  
  int revokeUserTokens(String username, Collection<UUID> uuids);
  
  Optional<Long> getNextTokenExpirationTime();
  
  int clearExpiredTokens();
}
