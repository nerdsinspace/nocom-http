package com.matt.nocom.server.model.auth;

import com.matt.nocom.server.auth.AccessToken;
import com.matt.nocom.server.auth.UserGroup;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData implements Serializable {
  private String username;
  private boolean enabled;

  @Singular
  private Set<UserGroup> groups;

  @Singular
  private List<AccessToken> tokens;
}
