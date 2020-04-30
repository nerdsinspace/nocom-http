package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.exception.InvalidPasswordException;
import com.matt.nocom.server.exception.InvalidUsernameException;
import com.matt.nocom.server.model.auth.AccessToken;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.properties.AuthenticationProperties;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.matt.nocom.server.h2.codegen.Tables.AUTH_TOKEN;
import static com.matt.nocom.server.h2.codegen.Tables.AUTH_USERS;

@Repository
@AllArgsConstructor
public class UserRepository {
  private final DSLContext dsl;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationProperties properties;

  @Transactional
  public int addUser(String username, String plaintextPassword, int level, boolean enabled)
      throws InvalidUsernameException, InvalidPasswordException {
    properties.checkUsername(username);
    properties.checkPassword(plaintextPassword);
    return dsl.insertInto(AUTH_USERS,
        AUTH_USERS.USERNAME,
        AUTH_USERS.PASSWORD,
        AUTH_USERS.LEVEL,
        AUTH_USERS.ENABLED)
        .values(username, passwordEncoder.encode(plaintextPassword).getBytes(StandardCharsets.US_ASCII), level, enabled)
        .execute();
  }

  @Transactional
  public int removeUser(String username) {
    return dsl.deleteFrom(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional
  public int setUserPassword(String username, String plaintextPassword) {
    properties.checkPassword(plaintextPassword);
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.PASSWORD, passwordEncoder.encode(plaintextPassword).getBytes(StandardCharsets.US_ASCII))
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional
  public int setUserEncodedPassword(String username, String password) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.PASSWORD, password.getBytes(StandardCharsets.US_ASCII))
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional(readOnly = true)
  public Optional<String> getUserPassword(String username) {
    return dsl.select(AUTH_USERS.PASSWORD)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.eq(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.PASSWORD)
        .map(pw -> new String(pw, StandardCharsets.US_ASCII));
  }

  @Transactional
  public int setUserEnabled(String username, boolean enabled) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.ENABLED, enabled)
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional(readOnly = true)
  public Optional<Boolean> getUserEnabled(String username) {
    return dsl.select(AUTH_USERS.ENABLED)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.eq(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.ENABLED);
  }

  @Transactional
  public int setUserLevel(String username, int level) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.LEVEL, level)
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional(readOnly = true)
  public Optional<Integer> getUserLevel(String username) {
    return dsl.select(AUTH_USERS.LEVEL)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.eq(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.LEVEL);
  }

  @Transactional
  public int setUserLastLogin(String username, Instant time) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.LOGIN_TIMESTAMP, Timestamp.from(time))
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional(readOnly = true)
  public Optional<Instant> getUserLastLogin(String username) {
    return dsl.select(AUTH_USERS.LOGIN_TIMESTAMP)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.eq(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.LOGIN_TIMESTAMP)
        .map(Timestamp::toInstant);
  }

  @Transactional(readOnly = true)
  public List<User> getUsers() {
    return dsl.selectFrom(AUTH_USERS).fetch(this::createUserObject);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUser(String username) {
    return dsl.selectFrom(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.eq(username))
        .limit(1)
        .fetchOptional(this::createUserObject);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserById(int id) {
    return dsl.select(AUTH_USERS.ID, AUTH_USERS.USERNAME, AUTH_USERS.PASSWORD,
        AUTH_USERS.ENABLED, AUTH_USERS.LEVEL, AUTH_USERS.DEBUG)
        .from(AUTH_USERS)
        .where(AUTH_USERS.ID.eq(id))
        .limit(1)
        .fetchOptional(this::createUserObject);
  }

  @Transactional(readOnly = true)
  public Optional<Integer> getIdByUsername(String username) {
    return dsl.select(AUTH_USERS.ID)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.eq(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.ID);
  }

  @Transactional(readOnly = true)
  public List<String> getUsernames() {
    return dsl.select(AUTH_USERS.USERNAME)
        .from(AUTH_USERS)
        .fetch(AUTH_USERS.USERNAME);
  }

  @Transactional(readOnly = true)
  public boolean usernameExists(String username) {
    return dsl.fetchExists(AUTH_USERS, AUTH_USERS.USERNAME.eq(username));
  }

  @Transactional(readOnly = true)
  public List<AccessToken> getTokens() {
    return dsl.selectFrom(AUTH_TOKEN).fetch(this::createAccessTokenObject);
  }

  @Transactional(readOnly = true)
  public List<AccessToken> getUserTokens(String username) {
    return dsl.selectFrom(AUTH_TOKEN)
        .where(AUTH_TOKEN.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.eq(username))
            .limit(1)))
        .fetch(this::createAccessTokenObject);
  }

  @Transactional(readOnly = true)
  public Optional<String> getUsernameByToken(UUID token, InetAddress address) {
    return dsl.select(AUTH_USERS.USERNAME)
        .from(AUTH_TOKEN)
        .innerJoin(AUTH_USERS).on(AUTH_TOKEN.USER_ID.eq(AUTH_USERS.ID))
        .where(AUTH_TOKEN.TOKEN.eq(token))
        .and(AUTH_TOKEN.ADDRESS.eq(address.getAddress()))
        .and(AUTH_TOKEN.CREATED_TIME.ge(Timestamp.from(oldestPossibleToken())))
        .limit(1)
        .fetchOptional(AUTH_USERS.USERNAME);
  }

  @Transactional
  public int addUserToken(String username, AccessToken token) {
    return dsl.insertInto(AUTH_TOKEN,
        AUTH_TOKEN.TOKEN,
        AUTH_TOKEN.ADDRESS,
        AUTH_TOKEN.USER_ID)
        .values(
            DSL.val(token.getToken()),
            DSL.val(token.getAddress().getAddress()),
            DSL.field(dsl.select(AUTH_USERS.ID)
                .from(AUTH_USERS)
                .where(AUTH_USERS.USERNAME.eq(username))
                .limit(1)))
        .execute();
  }

  @Transactional
  public int revokeToken(UUID token) {
    return dsl.deleteFrom(AUTH_TOKEN)
        .where(AUTH_TOKEN.TOKEN.eq(token))
        .execute();
  }

  @Transactional
  public int revokeUserTokens(String username) {
    return dsl.deleteFrom(AUTH_TOKEN)
        .where(AUTH_TOKEN.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.eq(username))
            .limit(1)))
        .execute();
  }

  @Transactional
  public int revokeUserTokens(String username, Collection<UUID> uuids) {
    Condition condition = DSL.falseCondition();
    for (UUID uuid : uuids) {
      condition = condition.or(AUTH_TOKEN.TOKEN.eq(uuid));
    }

    return dsl.deleteFrom(AUTH_TOKEN)
        .where(AUTH_TOKEN.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.eq(username))
            .limit(1)
        ))
        .and(condition)
        .execute();
  }

  @Transactional(readOnly = true)
  public Optional<Instant> getOldestToken() {
    return dsl.select(AUTH_TOKEN.CREATED_TIME)
        .from(AUTH_TOKEN)
        .orderBy(AUTH_TOKEN.CREATED_TIME)
        .limit(1)
        .fetchOptional(AUTH_TOKEN.CREATED_TIME)
        .map(Timestamp::toInstant);
  }

  @Transactional
  public int clearExpiredTokens() {
    return dsl.deleteFrom(AUTH_TOKEN)
        .where(DSL.currentTimestamp().ge(AUTH_TOKEN.CREATED_TIME.add(AUTH_TOKEN.LIFESPAN)))
        .execute();
  }

  private Instant oldestPossibleToken() {
    return Instant.now().minus(properties.getTokenLifetime());
  }

  private Instant timestampToInstant(@Nullable Timestamp timestamp) {
    return timestamp == null ? null : timestamp.toInstant();
  }

  private User createUserObject(Record record) {
    return User.builder()
        .id(record.getValue(AUTH_USERS.ID))
        .username(record.getValue(AUTH_USERS.USERNAME))
        .password(new String(record.getValue(AUTH_USERS.PASSWORD), StandardCharsets.US_ASCII))
        .enabled(record.getValue(AUTH_USERS.ENABLED))
        .level(record.getValue(AUTH_USERS.LEVEL))
        .debugUser(record.getValue(AUTH_USERS.DEBUG))
        .lastLogin(timestampToInstant(record.getValue(AUTH_USERS.LOGIN_TIMESTAMP)))
        .build();
  }

  @SneakyThrows(UnknownHostException.class)
  private AccessToken createAccessTokenObject(Record record) {
    return AccessToken.builder()
        .token(record.getValue(AUTH_TOKEN.TOKEN))
        .address(InetAddress.getByAddress(record.getValue(AUTH_TOKEN.ADDRESS)))
        .createdOn(timestampToInstant(record.getValue(AUTH_TOKEN.CREATED_TIME)))
        .lifespan(Duration.ofMillis(record.getValue(AUTH_TOKEN.LIFESPAN).getTime()))
        .build();
  }
}
