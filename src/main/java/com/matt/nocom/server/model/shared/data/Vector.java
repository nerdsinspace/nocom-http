package com.matt.nocom.server.model.shared.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Vector implements VectorXZ {
  private int x;
  private int z;
  
  public Vector copy() {
    return new Vector(x, z);
  }
}
