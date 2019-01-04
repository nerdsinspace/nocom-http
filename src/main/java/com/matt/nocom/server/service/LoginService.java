package com.matt.nocom.server.service;

import static com.matt.nocom.server.sqlite.Tables.AUTH_GROUPS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_USERS;
import static com.matt.nocom.server.sqlite.Tables.AUTH_USER_GROUPS;

import com.matt.nocom.server.model.auth.UserGroup;
import com.matt.nocom.server.model.auth.User;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public class LoginService implements UserDetailsService {
  private final DSLContext dsl;

  @Autowired
  public LoginService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<User> getUsers(Condition conditions) {
    return dsl.select(
        AUTH_USERS.USERNAME,
        AUTH_USERS.PASSWORD,
        AUTH_USERS.ENABLED,
        DSL.groupConcat(AUTH_GROUPS.NAME, ":").as("groupNames"))
        .from(AUTH_USERS)
        .innerJoin(AUTH_USER_GROUPS).on(AUTH_USERS.ID.eq(AUTH_USER_GROUPS.USER_ID))
        .innerJoin(AUTH_GROUPS).on(AUTH_USER_GROUPS.GROUP_ID.eq(AUTH_GROUPS.ID))
        .where(conditions)
        .fetch()
        .map(record -> User.builder()
            .username(record.getValue(AUTH_USERS.USERNAME))
            .password(record.getValue(AUTH_USERS.PASSWORD))
            .enabled(record.getValue(AUTH_USERS.ENABLED) > 0)
            .groups(Arrays.stream(record.component4().split(":"))
                .map(UserGroup::valueOf)
                .collect(Collectors.toList()))
            .build());
  }
  public List<User> getUsers() {
    return getUsers(DSL.noCondition());
  }

  public Optional<User> getUser(String name) {
    return getUsers(DSL.and(AUTH_USERS.USERNAME.equalIgnoreCase(name))).stream()
        .findFirst();
  }

  public List<UserGroup> getGroups(Condition conditions) {
    return dsl.select(AUTH_GROUPS.NAME)
        .from(AUTH_GROUPS)
        .where(conditions)
        .fetch()
        .map(record -> UserGroup.valueOf(record.getValue(AUTH_GROUPS.NAME)));
  }
  public List<UserGroup> getGroups() {
    return getGroups(DSL.noCondition());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return getUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
