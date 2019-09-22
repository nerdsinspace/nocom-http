package com.matt.nocom.server.service;

import static com.matt.nocom.server.sqlite.Tables.AUTH_USERS;
import static com.matt.nocom.server.sqlite.Tables.EVENT_TYPES;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.shared.auth.UserGroup;
import com.matt.nocom.server.model.sql.event.EventType;
import com.matt.nocom.server.util.EventTypeRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements Logging, DatabasePopulator {
  private final ApplicationSettings settings;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public DatabaseInitializer(ApplicationSettings settings,
      PasswordEncoder passwordEncoder) {
    this.settings = settings;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void populate(Connection connection) throws SQLException, ScriptException {
    DSLContext dsl = new DefaultDSLContext(connection, settings.getDialect());
    
    loadUsers(dsl);
    addAllEventTypes(dsl);
  }
  
  private void loadUsers(DSLContext dsl) {
    // remove any debug users in the database or reversed usernames
    int n = dsl.deleteFrom(AUTH_USERS)
        .where(AUTH_USERS.IS_DEBUG.ne(0))
        .execute();
    if (n > 0) {
      LOGGER.warn("{} debug user(s) removed from the database", n);
    }

    // obtain accounts from an accounts file
    Path admins = settings.getAdmins();

    if(!Files.exists(admins)) {
      LOGGER.info("No admins file named '{}' found (this is fine). path={}",
          settings.getAdmins().toString(), admins.toString());
      return;
    }

    Scanner scanner;
    try {
      scanner = new Scanner(new String(Files.readAllBytes(admins)));
    } catch (IOException e) {
      LOGGER.error("Failed to read admins file", e);
      return;
    }

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();

      if(line.trim().isEmpty())
        continue;

      int i = line.indexOf(':');

      if (i == -1) {
        LOGGER.error("Error parsing admins file: expected user:pass arguments, got: " + line);
        continue;
      }

      String username = line.substring(0, i);
      String password = line.substring(i + 1);
      
      if(dsl.fetchExists(AUTH_USERS, AUTH_USERS.USERNAME.equalIgnoreCase(username))) {
        if(dsl.update(AUTH_USERS)
            .set(AUTH_USERS.PASSWORD, passwordEncoder.encode(password))
            .set(AUTH_USERS.ENABLED, 1)
            .set(AUTH_USERS.LEVEL, UserGroup.ROOT.getLevel())
            .where(AUTH_USERS.USERNAME.equalIgnoreCase(username))
            .execute() > 0) {
          LOGGER.info("Updated user {}", username);
        }
      } else if (dsl.insertInto(AUTH_USERS, AUTH_USERS.USERNAME, AUTH_USERS.PASSWORD,
          AUTH_USERS.LEVEL, AUTH_USERS.ENABLED)
          .values(username, passwordEncoder.encode(password), UserGroup.ROOT.getLevel(), 1)
          .execute() > 0) {
        LOGGER.trace("Added user {} as root", username);
      }
    }

    LOGGER.trace("Admins file successfully parsed");
  }

  private void addAllEventTypes(DSLContext dsl) {
    for(EventType type : EventTypeRegistry.all()) {
      if (!dsl.fetchExists(EVENT_TYPES, EVENT_TYPES.HASH.eq(type.getHash()))) {
        // event type is missing from database, add it now
        dsl.insertInto(EVENT_TYPES, EVENT_TYPES.NAME, EVENT_TYPES.HASH)
            .values(type.getType(), type.getHash())
            .execute();
      }

      // update existing event types id with the one that is in the database
      type.__set_id(dsl.select(EVENT_TYPES.ID)
          .from(EVENT_TYPES)
          .where(EVENT_TYPES.HASH.eq(type.getHash()))
          .limit(1)
          .fetchOne(EVENT_TYPES.ID));
    }

    LOGGER.trace("EventTypes initialized");
  }
}
