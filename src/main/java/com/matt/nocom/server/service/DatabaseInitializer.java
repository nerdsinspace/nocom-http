package com.matt.nocom.server.service;

import static com.matt.nocom.server.sqlite.Tables.AUTH_GROUPS;
import static com.matt.nocom.server.sqlite.Tables.DIMENSIONS;

import com.matt.nocom.server.Logging;
import com.matt.nocom.server.Properties;
import com.matt.nocom.server.auth.User;
import com.matt.nocom.server.auth.UserGroup;
import com.matt.nocom.server.model.game.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements Logging, DatabasePopulator {
  private final PasswordEncoder passwordEncoder;

  @Value("classpath:database.init.sql")
  private Resource schemaSql;

  @Autowired
  public DatabaseInitializer(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void populate(Connection connection) throws SQLException, ScriptException {
    ResourceDatabasePopulator pop = new ResourceDatabasePopulator();
    pop.addScript(schemaSql);
    pop.populate(connection);

    DSLContext dsl = new DefaultDSLContext(connection, Properties.SQL_DIALECT);
    LoginManagerService login = new LoginManagerService(dsl);

    loadDimensions(dsl);
    loadGroups(dsl);

    initializeAccounts(dsl, login);
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

  private void loadGroups(DSLContext dsl) {
    final EnumSet<UserGroup> active = UserGroup.active();

    // find existing groups
    List<UserGroup> existing = dsl.select(AUTH_GROUPS.NAME)
        .from(AUTH_GROUPS)
        .fetch(record -> UserGroup.valueOf(record.getValue(AUTH_GROUPS.NAME)));

    // add groups that do not exist in the current database
    // doing it this way will prevent the table increment from incrementing the id
    dsl.batch(
        active.stream()
            .filter(g -> !existing.contains(g))
            .sorted()
            .map(ug -> dsl.insertInto(AUTH_GROUPS, AUTH_GROUPS.NAME, AUTH_GROUPS.LEVEL)
                .values(ug.getName(), ug.getLevel()))
            .collect(Collectors.toList())
    ).execute();

    LOGGER.trace("UserGroups initialized");
  }

  private void addOrUpdateUser(LoginManagerService login, String username, String password, UserGroup... groups) {
    User user = login.getUser(username).orElse(null);
    if(user != null) {
      // update existing user with provided password
      login.setUserPassword(username, passwordEncoder.encode(password));
      // enable just in case
      login.setUserEnabled(username, true);
    } else {
      login.addUser(User.builder()
          .username(username)
          .password(passwordEncoder.encode(password))
          .enabled(true)
          .build());
    }

    // add debug user back to groups just in case they have been removed from them
    if(user == null || !user.getGroups().containsAll(Arrays.asList(groups))) {
      login.addUserToGroups(username, groups);
    }
  }

  private void deleteDebugUsers(LoginManagerService login) {
    login.getUsers().stream()
        .filter(user -> user.getGroups().contains(UserGroup.DEBUG))
        .forEach(user -> {
          login.removeUser(user.getUsername());
          LOGGER.trace("Removed debug user " + user.getUsername());
        });

    LOGGER.trace("Debug users removed from database");
  }

  private void initializeAccounts(DSLContext dsl, LoginManagerService login) {
    if(Properties.DEBUG_AUTH) {
      // debug authentication
      addOrUpdateUser(login, Properties.DEBUG_USERNAME, Properties.DEBUG_PASSWORD, UserGroup.DEBUG);
      LOGGER.trace("Debug user initialized");
    } else {
      // remove any debug users in the database
      deleteDebugUsers(login);

      // obtain accounts from an accounts file
      Path admins = Paths.get("").resolve(Properties.ADMINS_FILE);

      if(!Files.exists(admins)) {
        LOGGER.trace("No admins file named '" + Properties.ADMINS_FILE + "' found, skipping...");
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
        String[] ss = scanner.nextLine().split(" ");

        if(ss.length < 2)
          throw new IllegalArgumentException("Error parsing admins file: expected 2 arguments, got " + ss.length);

        String username = ss[0];
        String password = ss[1];

        addOrUpdateUser(login, username, password, UserGroup.ADMIN);

        LOGGER.trace("Added admin " + username);
      }

      LOGGER.trace("Admins file successfully parsed");
    }
  }
}
