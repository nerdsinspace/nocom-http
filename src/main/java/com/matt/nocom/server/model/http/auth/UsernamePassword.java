package com.matt.nocom.server.model.http.auth;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePassword implements Serializable {
  private String username;
  private String password;
}
