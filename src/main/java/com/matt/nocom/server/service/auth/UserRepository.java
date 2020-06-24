package com.matt.nocom.server.service.auth;

import com.matt.nocom.server.exception.InvalidPasswordException;
import com.matt.nocom.server.exception.InvalidUsernameException;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.properties.AuthenticationProperties;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        .values(username, encodedPasswordToBytes(passwordEncoder.encode(plaintextPassword)), level, enabled)
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
        .set(AUTH_USERS.PASSWORD, encodedPasswordToBytes(passwordEncoder.encode(plaintextPassword)))
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional
  public int setUserEncodedPassword(String username, String password) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.PASSWORD, encodedPasswordToBytes(password))
        .where(AUTH_USERS.USERNAME.eq(username))
        .execute();
  }

  @Transactional(readOnly = true)
  public Optional<String> getUserEncodedPassword(String username) {
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

  private Instant timestampToInstant(@Nullable Timestamp timestamp) {
    return timestamp == null ? null : timestamp.toInstant();
  }

  private byte[] encodedPasswordToBytes(String encoded) {
    return encoded.getBytes(StandardCharsets.US_ASCII);
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
}
