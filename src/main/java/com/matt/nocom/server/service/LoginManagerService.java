package com.matt.nocom.server.service;

import static com.matt.nocom.server.sqlite.Tables.AUTH_GROUPS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_TOKENS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_USERS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_USER_GROUPS;

import com.google.common.base.MoreObjects;
import com.matt.nocom.server.model.auth.AccessToken;
import com.matt.nocom.server.model.auth.UserGroup;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.util.Util;
import com.matt.nocom.server.util.factory.AccessTokenFactory;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LoginManagerService implements UserDetailsService {
  private final DSLContext dsl;

  @Autowired
  public LoginManagerService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public int addUser(User user) {
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

  public int addUserGroup(User user, UserGroup group) {
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

  public int removeUserGroup(User user, UserGroup group) {
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

  private List<AccessToken> getAccessToken(Condition condition) {
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

  public Optional<Long> getNextExpirationTime() {
    return dsl.select(AUTH_TOKENS.EXPIRES_ON)
        .from(AUTH_TOKENS)
        .fetch()
        .map(record -> record.getValue(AUTH_TOKENS.EXPIRES_ON))
        .stream()
        .min(Long::compareTo);
  }

  public int clearExpiredTokens() {
    return dsl.deleteFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.EXPIRES_ON.ge(System.currentTimeMillis()))
        .execute();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return getUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
