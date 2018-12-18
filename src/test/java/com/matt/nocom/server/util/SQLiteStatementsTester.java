package com.matt.nocom.server.util;

import com.matt.nocom.server.service.SQLiteStatements;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Test;

public class SQLiteStatementsTester {

  @Test
  public void initialize_Test_ExpectedBehavior() throws URISyntaxException, IOException {
    SQLiteStatements reader = new SQLiteStatements("nocom.test.sql");

    reader.find("Create*").forEach(System.out::println);
  }
}
