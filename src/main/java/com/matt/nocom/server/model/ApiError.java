package com.matt.nocom.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ApiError {
  private HttpStatus status;
  private String message;

  @Singular
  private List<String> errors;

  public ResponseEntity toResponseEntity() {
    return ResponseEntity.status(getStatus()).body(this);
  }

  public static class ApiErrorBuilder {
    public ResponseEntity asResponseEntity() {
      return build().toResponseEntity();
    }
  }
}
