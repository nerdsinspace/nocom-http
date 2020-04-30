package com.matt.nocom.server.model.auth;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredentials implements Serializable {
  private String username;
  private String passwordPlaintext;
}
