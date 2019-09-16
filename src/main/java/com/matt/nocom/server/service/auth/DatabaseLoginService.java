package com.matt.nocom.server.service.auth;

import static com.matt.nocom.server.sqlite.Tables.AUTH_TOKENS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_USERS;

import com.matt.nocom.server.exception.InvalidPasswordException;
import com.matt.nocom.server.exception.InvalidUsernameException;
import com.matt.nocom.server.model.sql.auth.AccessToken;
import com.matt.nocom.server.model.sql.auth.User;
import com.matt.nocom.server.service.ApplicationSettings;
import com.matt.nocom.server.util.StaticUtils;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
public class DatabaseLoginService implements LoginService {
  
  private final DSLContext dsl;
  protected final PasswordEncoder passwordEncoder;
  protected final ApplicationSettings settings;
  
  @Autowired
  public DatabaseLoginService(DSLContext dsl,
      PasswordEncoder passwordEncoder, ApplicationSettings settings) {
    this.dsl = dsl;
    this.passwordEncoder = passwordEncoder;
    this.settings = settings;
  }
  
  @Override
  public int addUser(String username, String plaintextPassword, int level, boolean enabled)
      throws InvalidUsernameException, InvalidPasswordException {
    return dsl.insertInto(AUTH_USERS,
        AUTH_USERS.USERNAME,
        AUTH_USERS.PASSWORD,
        AUTH_USERS.LEVEL,
        AUTH_USERS.ENABLED)
        .values(username, passwordEncoder.encode(plaintextPassword), level, enabled ? 1 : 0)
        .execute();
  }
  
  @Override
  public int removeUser(String username) {
    return dsl.deleteFrom(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .execute();
  }
  
  @Override
  public int setUserPassword(String username, String plaintextPassword) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.PASSWORD, passwordEncoder.encode(plaintextPassword))
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .execute();
  }
  
  @Override
  public Optional<String> getUserPassword(String username) {
    return dsl.select(AUTH_USERS.PASSWORD)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.PASSWORD);
  }
  
  @Override
  public int setUserEnabled(String username, boolean enabled) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.ENABLED, enabled ? 1 : 0)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .execute();
  }
  
  @Override
  public Optional<Boolean> getUserEnabled(String username) {
    return dsl.select(AUTH_USERS.ENABLED)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.ENABLED)
        .map(Integer.valueOf(1)::equals);
  }
  
  @Override
  public int setUserLevel(String username, int level) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.LEVEL, level)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .execute();
  }
  
  @Override
  public Optional<Integer> getUserLevel(String username) {
    return dsl.select(AUTH_USERS.LEVEL)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.LEVEL);
  }
  
  @Override
  public int setUserLastLogin(String username, long time) {
    return dsl.update(AUTH_USERS)
        .set(AUTH_USERS.LAST_LOGIN, time)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .execute();
  }
  
  @Override
  public Optional<Long> getUserLastLogin(String username) {
    return dsl.select(AUTH_USERS.LAST_LOGIN)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.LAST_LOGIN);
  }
  
  @Override
  public List<User> getUsers() {
    return dsl.selectFrom(AUTH_USERS).fetch(this::createUserObject);
  }
  
  @Override
  public Optional<User> getUser(String username) {
    return dsl.selectFrom(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .limit(1)
        .fetchOptional(this::createUserObject);
  }
  
  @Override
  public Optional<User> getUserById(int id) {
    return dsl.select(AUTH_USERS.ID, AUTH_USERS.USERNAME, AUTH_USERS.PASSWORD,
        AUTH_USERS.ENABLED, AUTH_USERS.LEVEL, AUTH_USERS.IS_DEBUG)
        .from(AUTH_USERS)
        .where(AUTH_USERS.ID.eq(id))
        .limit(1)
        .fetchOptional(this::createUserObject);
  }
  
  @Override
  public Optional<Integer> getIdByUsername(String username) {
    return dsl.select(AUTH_USERS.ID)
        .from(AUTH_USERS)
        .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
        .limit(1)
        .fetchOptional(AUTH_USERS.ID);
  }
  
  @Override
  public List<String> getUsernames() {
    return dsl.select(AUTH_USERS.USERNAME)
        .from(AUTH_USERS)
        .fetch(AUTH_USERS.USERNAME);
  }
  
  @Override
  public boolean usernameExists(String username) {
    return dsl.fetchExists(AUTH_USERS, AUTH_USERS.USERNAME.equalIgnoreCase(username));
  }
  
  @Override
  public List<AccessToken> getTokens() {
    return dsl.selectFrom(AUTH_TOKENS).fetch(this::createAccessTokenObject);
  }
  
  @Override
  public List<AccessToken> getUserTokens(String username) {
    return dsl.selectFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
            .limit(1)))
        .fetch(this::createAccessTokenObject);
  }
  
  @Override
  public Optional<String> getUsernameByToken(UUID token, InetAddress address) {
    return dsl.select(AUTH_USERS.USERNAME)
        .from(AUTH_TOKENS)
        .innerJoin(AUTH_USERS).on(AUTH_TOKENS.USER_ID.eq(AUTH_USERS.ID))
        .where(AUTH_TOKENS.TOKEN.eq(token.toString()))
        .and(AUTH_TOKENS.ADDRESS.eq(address.getHostAddress()))
        .and(AUTH_TOKENS.EXPIRES_ON.ge(System.currentTimeMillis()))
        .limit(1)
        .fetchOptional(AUTH_USERS.USERNAME);
  }
  
  @Override
  public int addUserToken(String username, AccessToken token) {
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
                .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
                .limit(1)
            )
        )
        .execute();
  }
  
  @Override
  public int revokeToken(UUID token) {
    return dsl.deleteFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.TOKEN.eq(token.toString()))
        .execute();
  }
  
  @Override
  public int revokeUserTokens(String username) {
    return dsl.deleteFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
            .limit(1)))
        .execute();
  }
  
  @Override
  public int revokeUserTokens(String username, Collection<UUID> uuids) {
    Condition condition = DSL.falseCondition();
    for (UUID uuid : uuids) {
      condition = condition.or(AUTH_TOKENS.TOKEN.eq(uuid.toString()));
    }
    
    return dsl.deleteFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.USER_ID.eq(dsl.select(AUTH_USERS.ID)
            .from(AUTH_USERS)
            .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
            .limit(1)
        ))
        .and(condition)
        .execute();
  }
  
  @Override
  public Optional<Long> getNextTokenExpirationTime() {
    return dsl.select(AUTH_TOKENS.EXPIRES_ON)
        .from(AUTH_TOKENS)
        .orderBy(AUTH_TOKENS.EXPIRES_ON.asc())
        .limit(1)
        .fetchOptional(AUTH_TOKENS.EXPIRES_ON);
  }
  
  @Override
  public int clearExpiredTokens() {
    return dsl.deleteFrom(AUTH_TOKENS)
        .where(AUTH_TOKENS.EXPIRES_ON.le(System.currentTimeMillis()))
        .execute();
  }
  
  private User createUserObject(Record record) {
    return User.builder()
        .id(record.getValue(AUTH_USERS.ID))
        .username(record.getValue(AUTH_USERS.USERNAME))
        .password(record.getValue(AUTH_USERS.PASSWORD))
        .enabled(record.getValue(AUTH_USERS.ENABLED) != 0)
        .level(record.getValue(AUTH_USERS.LEVEL))
        .debugUser(record.getValue(AUTH_USERS.IS_DEBUG) != 0)
        .lastLogin(record.getValue(AUTH_USERS.LAST_LOGIN))
        .build();
  }
  
  private AccessToken createAccessTokenObject(Record record) {
    return AccessToken.builder()
        .token(UUID.fromString(record.getValue(AUTH_TOKENS.TOKEN)))
        .address(StaticUtils.stringToAddress(record.getValue(AUTH_TOKENS.ADDRESS)))
        .expiresOn(record.getValue(AUTH_TOKENS.EXPIRES_ON))
        .build();
  }
}
