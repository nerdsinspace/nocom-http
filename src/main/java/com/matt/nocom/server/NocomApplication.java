package com.matt.nocom.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NocomApplication {
  public static void main(String[] args) throws IOException {
    if("dev".equals(System.getProperty("spring.profiles.active")))
      createPropertiesFile();

    SpringApplication.run(NocomApplication.class, args);
  }

  private static void createPropertiesFile() throws IOException {
    Path gradleProperties = Paths.get("").resolve("gradle.properties");

    if(!Files.exists(gradleProperties)) {
      System.err.println("Could not locate gradle.properties");
      return;
    }

    Path configProperties = Paths.get("").resolve("out/production/resources/database.properties");

    if(!Files.exists(configProperties)) {
      System.err.println("Database properties file missing, should exist");
      return;
    }

    List<Pair> list = new ArrayList<>();

    Scanner scanner = new Scanner(new String(Files.readAllBytes(gradleProperties)));
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      int d = line.indexOf('=');
      if(d == -1)
        continue; // has no delimiter
      list.add(new Pair(line.substring(0, d), line.substring(d + 1)));
    }

    StringBuilder builder = new StringBuilder(new String(Files.readAllBytes(configProperties)));
    for(Pair pair : list) {
      final String token = '@' + pair.key + '@';
      int start = builder.indexOf(token);
      if(start != -1) {
        builder.replace(start, start + token.length(), pair.value);
      }
    }

    Files.write(configProperties, builder.toString().getBytes(), StandardOpenOption.WRITE);
  }

  @AllArgsConstructor
  static class Pair {
    String key, value;
  }
}
