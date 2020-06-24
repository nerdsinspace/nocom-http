package com.matt.nocom.server.config.auth;

import com.matt.nocom.server.model.auth.UserGroup;
import com.matt.nocom.server.properties.AuthenticationProperties;
import com.matt.nocom.server.service.auth.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.matt.nocom.server.Logging.getLogger;

@Profile("!dev")
@Configuration
public class CreateProductionUsers {
  public void createDefaultUsers(AuthenticationProperties properties, UserRepository users) {
    getLogger().debug("Creating default root users");

    for (String userInfo : properties.getRootUsers()) {
      int d = userInfo.indexOf(':');

      if (d == -1) {
        getLogger().warn("Invalid username:password provided \"{}\"", userInfo);
        continue;
      }

      String username = userInfo.substring(0, d);
      String password = userInfo.substring(d + 1);

      // only add new user if the old one no longer exists
      if (!users.usernameExists(username)) {
        users.addUser(username, password, UserGroup.ROOT.getLevel(), true);
      }
    }
  }
}
