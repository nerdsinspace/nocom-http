package com.matt.nocom.server.model.http.auth;

import com.matt.nocom.server.model.sql.auth.UserGroup;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistration {
  private String username;
  private String password;

  @Singular
  private Set<UserGroup> groups;
}
