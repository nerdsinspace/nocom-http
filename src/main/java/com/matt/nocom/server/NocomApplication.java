package com.matt.nocom.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NocomApplication {
  public static void main(String[] args) throws IOException {
    /*InputStream is = NocomApplication.class.getResourceAsStream("/static/secret/chunkviewer/samples/r.5.13.mca");
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[16384];

    while ((nRead = is.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    Files.write(Paths.get("meme"), buffer.toByteArray());*/
    /*Path p = Paths.get("src/main/resources/static/secret/chunkviewer/samples/r.5.13.mca");
    System.out.println(p.toAbsolutePath());
    Files.write(Paths.get("meme"), Files.readAllBytes(Paths.get("src/main/resources/static/secret/chunkviewer/samples/r.5.13.mca")));*/

    SpringApplication.run(NocomApplication.class, args);
  }
}
