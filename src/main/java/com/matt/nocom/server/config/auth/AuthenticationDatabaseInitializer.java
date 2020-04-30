package com.matt.nocom.server.config.auth;

import com.matt.nocom.server.properties.AuthenticationProperties;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;

import static com.matt.nocom.server.Logging.getLogger;
import static com.matt.nocom.server.h2.codegen.Tables.AUTH_USERS;

@Configuration
public class AuthenticationDatabaseInitializer {
  @Profile("dev")
  @Autowired
  public void initializeDevUsers(AuthenticationProperties properties, DSLContext dsl, PasswordEncoder encoder) {
    getLogger().debug("Adding debug mode users to database");

    dsl.transaction(config -> {
      var ctx = DSL.using(config);
      for (String userInfo : properties.getDebugUsers()) {
        int d = userInfo.indexOf(':');

        if (d == -1) {
          getLogger().warn("Invalid username:password provided \"{}\"", userInfo);
          continue;
        }

        String username = userInfo.substring(0, d);
        String password = userInfo.substring(d + 1);

        ctx.insertInto(AUTH_USERS,
            AUTH_USERS.USERNAME,
            AUTH_USERS.PASSWORD,
            AUTH_USERS.LEVEL,
            AUTH_USERS.ENABLED,
            AUTH_USERS.DEBUG)
            .values(username, encoder.encode(password).getBytes(StandardCharsets.US_ASCII), 100, true, true)
            .onConflictDoNothing()
            .execute();
      }
    });
  }
}
