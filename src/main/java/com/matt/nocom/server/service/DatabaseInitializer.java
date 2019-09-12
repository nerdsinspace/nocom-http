package com.matt.nocom.server.service;

import static com.matt.nocom.server.sqlite.Tables.AUTH_USERS;
import static com.matt.nocom.server.sqlite.Tables.DIMENSIONS;
import static com.matt.nocom.server.sqlite.Tables.EVENT_TYPES;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.Properties;
import com.matt.nocom.server.model.shared.auth.UserGroup;
import com.matt.nocom.server.model.sql.data.Dimension;
import com.matt.nocom.server.model.sql.event.EventType;
import com.matt.nocom.server.util.EventTypeRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements Logging, DatabasePopulator {
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public DatabaseInitializer(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void populate(Connection connection) throws SQLException, ScriptException {
    // do not apply schema - use flyway instead
    //ResourceDatabasePopulator pop = new ResourceDatabasePopulator();
    //pop.addScript(schemaSql);
    //pop.populate(connection);

    DSLContext dsl = new DefaultDSLContext(connection, Properties.SQL_DIALECT);
  
    loadDimensions(dsl);
  
    loadUsers(dsl);

    addAllEventTypes(dsl);
  }

  private void loadDimensions(DSLContext dsl) {
    final EnumSet<Dimension> all = Dimension.all();

    // find existing dimensions
    List<Dimension> existing = dsl.select(DIMENSIONS.ORDINAL)
        .from(DIMENSIONS)
        .fetch(record -> Dimension.from(record.getValue(DIMENSIONS.ORDINAL)));

    // add dimensions that do not exist in the current database
    // doing it this way will prevent the table increment from incrementing the id
    dsl.batch(
        all.stream()
            .filter(existing::contains)
            .sorted()
            .map(d -> dsl.insertInto(DIMENSIONS, DIMENSIONS.ORDINAL, DIMENSIONS.NAME)
                .values(d.getOrdinal(), d.getName()))
            .collect(Collectors.toList())
    ).execute();

    LOGGER.trace("Dimensions initialized");
  }
  
  private void loadUsers(DSLContext dsl) {
    if (!Properties.DEBUG_AUTH) {
      // remove any debug users in the database or reversed usernames
      int n = dsl.deleteFrom(AUTH_USERS)
          .where(AUTH_USERS.IS_DEBUG.ge(0))
          .execute();
      if (n > 0) {
        LOGGER.warn("{} debug user(s) removed from the database", n);
      }

      // obtain accounts from an accounts file
      Path admins = Paths.get("").resolve(Properties.ADMINS_FILE);

      if(!Files.exists(admins)) {
        LOGGER.trace("No admins file named '{}' found (this is fine). path={}",
            Properties.ADMINS_FILE, admins.toString());
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
  
        if (dsl.insertInto(AUTH_USERS, AUTH_USERS.USERNAME, AUTH_USERS.PASSWORD,
            AUTH_USERS.LEVEL, AUTH_USERS.ENABLED)
            .values(username, passwordEncoder.encode(password), UserGroup.ROOT.getLevel(), 1)
            .execute() > 0) {
          LOGGER.trace("Added user {} as root", username);
        }
      }

      LOGGER.trace("Admins file successfully parsed");
    }
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
