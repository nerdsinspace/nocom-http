package com.matt.nocom.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;

@JsonSerialize
public class EmptyModel implements Serializable {
  private static final EmptyModel INSTANCE = new EmptyModel();

  public static EmptyModel getInstance() {
    return INSTANCE;
  }

  private EmptyModel() {}
}
