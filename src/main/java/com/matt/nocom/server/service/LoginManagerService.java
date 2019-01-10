package com.matt.nocom.server.service;

import static com.matt.nocom.server.sqlite.Tables.AUTH_GROUPS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_TOKENS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_USERS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_USER_GROUPS;

import com.google.common.base.MoreObjects;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.exception.IllegalPasswordException;
import com.matt.nocom.server.exception.IllegalUsernameException;
import com.matt.nocom.server.model.auth.AccessToken;
import com.matt.nocom.server.model.auth.UserData;
import com.matt.nocom.server.model.auth.UserGroup;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.util.Util;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LoginManagerService implements UserDetailsService, Logging {
  private final DSLContext dsl;

  @Autowired
  public LoginManagerService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public int addUser(User user) throws IllegalUsernameException, IllegalPasswordException {
    if(!user.getUsername().matches("[A-Za-z0-9_]+"))
      throw new IllegalUsernameException();

    return dsl.insertInto(AUTH_USERS,
        AUTH_USERS.USERNAME,
        AUTH_USERS.PASSWORD,
        AUTH_USERS.ENABLED)
        .values(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled() ? 1 : 0
        )
        .execute();
  }

  public int updateUser(User user) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.PASSWORD, user.getPassword())
        .set(AUTH_USERS.ENABLED, user.isEnabled() ? 1 : 0)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(user.getUsername()))
        .execute();
  }

  public int removeUser(User user) {
    return dsl.deleteFrom(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(user.getUsername()))
        .execute();
  }

  private List<User> getUsers(Condition conditions) {
    return dsl.select(
        AUTH_USERS.ID,
        AUTH_USERS.USERNAME,
        AUTH_USERS.PASSWORD,
        AUTH_USERS.ENABLED)
        .from(AUTH_USERS)
        .where(conditions)
        .fetch()
        .map(record -> User.builder()
            .username(record.getValue(AUTH_USERS.USERNAME))
            .password(record.getValue(AUTH_USERS.PASSWORD))
            .enabled(MoreObjects.firstNonNull(record.getValue(AUTH_USERS.ENABLED), 1) > 0)
            .groups(dsl.select(AUTH_GROUPS.NAME)
                .from(AUTH_USER_GROUPS)
                .innerJoin(AUTH_GROUPS).on(AUTH_GROUPS.ID.eq(AUTH_USER_GROUPS.GROUP_ID))
                .where(AUTH_USER_GROUPS.USER_ID.eq(record.getValue(AUTH_USERS.ID)))
                .fetch()
                .map(rec -> UserGroup.valueOf(rec.getValue(AUTH_GROUPS.NAME))))
            .build());
  }
  public List<User> getUsers() {
    return getUsers(DSL.noCondition());
  }

  public List<String> getUsernames() {
    return dsl.select(AUTH_USERS.USERNAME)
        .from(AUTH_USERS)
        .fetch()
        .map(record -> record.getValue(AUTH_USERS.USERNAME));
  }

  public Optional<User> getUser(String name) {
    return getUsers(DSL.and(AUTH_USERS.USERNAME.equalIgnoreCase(name))).stream()
        .findFirst();
  }

  public boolean usernameExists(String name) {
    return dsl.select()
        .from(AUTH_USERS)
        .where(DSL.and(AUTH_USERS.USERNAME.equalIgnoreCase(name)))
        .fetch()
        .isNotEmpty();
  }

  private List<UserGroup> getGroups(Condition conditions) {
    return dsl.select(AUTH_GROUPS.NAME)
        .from(AUTH_GROUPS)
        .where(conditions)
        .fetch()
        .map(record -> UserGroup.valueOf(record.getValue(AUTH_GROUPS.NAME)));
  }
  public List<UserGroup> getGroups() {
    return getGroups(DSL.noCondition());
  }

  public int addUserToGroup(User user, UserGroup group) {
    return dsl.insertInto(AUTH_USER_GROUPS, AUTH_USER_GROUPS.USER_ID, AUTH_USER_GROUPS.GROUP_ID)
        .values(
            DSL.field(dsl.select(AUTH_USERS.ID)
                .from(AUTH_USERS)
                .where(AUTH_USERS.USERNAME.equalIgnoreCase(user.getUsername()))
                .limit(1)),
            DSL.field(dsl.select(AUTH_GROUPS.ID)
                .from(AUTH_GROUPS)
                .where(AUTH_GROUPS.NAME.equalIgnoreCase(group.getName()))
                .limit(1))
        )
        .execute();
  }

  public int removeUserFromGroup(User user, UserGroup group) {
    return dsl.deleteFrom(AUTH_USER_GROUPS)
        .where(AUTH_USER_GROUPS.GROUP_ID.eq(dsl.select(AUTH_GROUPS.ID)
            .from(AUTH_GROUPS)
            .where(AUTH_GROUPS.NAME.eq(group.getName()))
            .limit(1)))
        .and(AUTH_USER_GROUPS.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.equalIgnoreCase(user.getUsername()))
            .limit(1)))
        .execute();
  }

  private List<AccessToken> getTokens(Condition condition) {
    return dsl.select(AUTH_TOKENS.TOKEN, AUTH_TOKENS.ADDRESS, AUTH_TOKENS.EXPIRES_ON)
        .from(AUTH_TOKENS)
        .where(condition)
        .fetch()
        .map(record -> AccessToken.builder()
            .token(UUID.fromString(record.getValue(AUTH_TOKENS.TOKEN)))
            .address(Util.stringToAddress(record.getValue(AUTH_TOKENS.ADDRESS)))
            .expiresOn(record.getValue(AUTH_TOKENS.EXPIRES_ON))
            .build());
  }
  public List<AccessToken> getTokens() {
    return getTokens(DSL.noCondition());
  }

  public List<AccessToken> getUserTokens(User user) {
    return dsl.select(AUTH_TOKENS.TOKEN, AUTH_TOKENS.ADDRESS, AUTH_TOKENS.EXPIRES_ON)
        .from(AUTH_TOKENS)
        .innerJoin(AUTH_USERS).on(AUTH_TOKENS.USER_ID.eq(AUTH_USERS.ID))
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(user.getUsername()))
        .fetch()
        .map(record -> AccessToken.builder()
            .token(UUID.fromString(record.getValue(AUTH_TOKENS.TOKEN)))
            .address(Util.stringToAddress(record.getValue(AUTH_TOKENS.ADDRESS)))
            .expiresOn(record.getValue(AUTH_TOKENS.EXPIRES_ON))
            .build());
  }

  public Optional<User> getUserByToken(UUID token, InetAddress address) {
    return dsl.select(
        AUTH_USERS.ID,
        AUTH_USERS.USERNAME,
        AUTH_USERS.PASSWORD,
        AUTH_USERS.ENABLED)
        .from(AUTH_TOKENS)
        .innerJoin(AUTH_USERS).on(AUTH_TOKENS.USER_ID.eq(AUTH_USERS.ID))
        .where(AUTH_TOKENS.TOKEN.eq(token.toString()))
        .and(AUTH_TOKENS.ADDRESS.eq(address.getHostAddress()))
        .and(AUTH_TOKENS.EXPIRES_ON.ge(System.currentTimeMillis()))
        .limit(1)
        .fetch()
        .map(record -> User.builder()
            .username(record.getValue(AUTH_USERS.USERNAME))
            .password(record.getValue(AUTH_USERS.PASSWORD))
            .enabled(MoreObjects.firstNonNull(record.getValue(AUTH_USERS.ENABLED), 1) > 0)
            .groups(dsl.select(AUTH_GROUPS.NAME)
                .from(AUTH_USER_GROUPS)
                .innerJoin(AUTH_GROUPS).on(AUTH_USER_GROUPS.GROUP_ID.eq(AUTH_GROUPS.ID))
                .where(AUTH_USER_GROUPS.USER_ID.eq(record.getValue(AUTH_USERS.ID)))
                .fetch()
                .map(rec -> UserGroup.valueOf(rec.getValue(AUTH_GROUPS.NAME))))
            .build()
        ).stream().findFirst();
  }

  public int addUserToken(User user, AccessToken token) {
    return dsl.insertInto(AUTH_TOKENS,
        AUTH_TOKENS.TOKEN,
        AUTH_TOKENS.ADDRESS,
        AUTH_TOKENS.EXPIRES_ON,
        AUTH_TOKENS.USER_ID)
        .values(
            DSL.val(token.getToken().toString()),
            DSL.val(token.getAddress().getHostAddress()),
            DSL.val(token.getExpiresOn()),
            DSL.field(dsl.select(AUTH_USERS.ID)
                .from(AUTH_USERS)
                .where(AUTH_USERS.USERNAME.equalIgnoreCase(user.getUsername()))
                .limit(1)
            )
        )
        .execute();
  }

  public int expireUserTokens(User user) {
    return dsl.deleteFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.equalIgnoreCase(user.getUsername()))
            .limit(1)
        ))
        .execute();
  }

  public Optional<Long> getNextExpirationTime() {
    return dsl.select(AUTH_TOKENS.EXPIRES_ON)
        .from(AUTH_TOKENS)
        .fetch()
        .map(record -> record.getValue(AUTH_TOKENS.EXPIRES_ON))
        .stream()
        .min(Long::compareTo);
  }

  public int clearExpiredTokens() {
    LOGGER.trace("Expired tokens cleared");
    return dsl.deleteFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.EXPIRES_ON.ge(System.currentTimeMillis()))
        .execute();
  }

  public List<UserData> getUserData() {
    return dsl.select(AUTH_USERS.ID, AUTH_USERS.USERNAME, AUTH_USERS.ENABLED)
        .from(AUTH_USERS)
        .fetch()
        .map(record -> UserData.builder()
            .username(record.getValue(AUTH_USERS.USERNAME))
            .enabled(record.getValue(AUTH_USERS.ENABLED) > 0)
            .groups(dsl.select(AUTH_GROUPS.NAME)
                .from(AUTH_USER_GROUPS)
                .innerJoin(AUTH_GROUPS).on(AUTH_USER_GROUPS.GROUP_ID.eq(AUTH_GROUPS.ID))
                .where(AUTH_USER_GROUPS.USER_ID.eq(record.getValue(AUTH_USERS.ID)))
                .fetch()
                .map(rec -> UserGroup.valueOf(rec.getValue(AUTH_GROUPS.NAME))))
            .tokens(dsl.select(AUTH_TOKENS.TOKEN, AUTH_TOKENS.ADDRESS, AUTH_TOKENS.EXPIRES_ON)
                .from(AUTH_TOKENS)
                .where(AUTH_TOKENS.USER_ID.eq(record.getValue(AUTH_USERS.ID)))
                .fetch()
                .map(rec -> AccessToken.builder()
                    .token(UUID.fromString(rec.getValue(AUTH_TOKENS.TOKEN)))
                    .address(Util.stringToAddress(rec.getValue(AUTH_TOKENS.ADDRESS)))
                    .expiresOn(rec.getValue(AUTH_TOKENS.EXPIRES_ON))
                    .build()))
            .build());
  }
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return getUser(username)
        .filter(User::isNotDebugUser)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
