package com.matt.nocom.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.matt.nocom.server.properties")
public class NocomApplication {
  public static void main(String[] args) {
    SpringApplication.run(NocomApplication.class, args);
  }
}
