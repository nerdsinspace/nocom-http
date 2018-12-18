package com.matt.nocom.server.service;

import com.google.common.collect.Maps;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.Properties;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SQLiteStatements implements Logging {
  private static final SQLiteStatements INSTANCE;

  public static final String INSERT_PLAYER;
  public static final String INSERT_SERVER;
  public static final String INSERT_POSITION;
  public static final String INSERT_LOCATION;
  public static final String SELECT_ALL_LOCATIONS;
  public static final String SELECT_LOCATIONS_WITH_FILTER;

  private static final Pattern FILE_FORMAT = Pattern.compile("\\#(.+?\\r?\\n)(.+?)\\;", Pattern.DOTALL);

  static {
    try {
      INSTANCE = new SQLiteStatements();
    } catch (IOException | URISyntaxException e) {
      throw new Error(e);
    }

    INSERT_PLAYER = getInstance().get("InsertPlayer");
    INSERT_SERVER = getInstance().get("InsertServer");
    INSERT_POSITION = getInstance().get("InsertPosition");
    INSERT_LOCATION = getInstance().get("InsertLocation");
    SELECT_ALL_LOCATIONS = getInstance().get("SelectAllLocations");
    SELECT_LOCATIONS_WITH_FILTER = getInstance().get("SelectLocationsWithFilter");
  }

  public static SQLiteStatements getInstance() {
    return INSTANCE;
  }

  private final Map<String, String> statements = Maps.newLinkedHashMap();

  private SQLiteStatements(Path file) throws IOException {
    Objects.requireNonNull(file);

    String contents = new String(Files.readAllBytes(file));
    Matcher matcher = FILE_FORMAT.matcher(contents);
    for(int i = 0; matcher.find(i); i = matcher.end()) {
      String name = matcher.group(1).trim();
      String sql = filterStatement(matcher.group(2));

      if(statements.put(name, sql) != null)
        throw new Error("Entry '" + name + "' already exists!");
    }
  }

  private SQLiteStatements() throws URISyntaxException, IOException {
    this(Paths.get(SQLiteStatements.class.getClassLoader().getResource(Properties.RESOURCE_SQL).toURI()));
  }

  public Collection<String> all() {
    return Collections.unmodifiableCollection(statements.values());
  }

  public Collection<String> find(String filter) {
    final String regex = ("\\Q" + filter + "\\E").replace("*", "\\E.*\\Q");
    return statements.entrySet().stream()
        .filter(e -> e.getKey().matches(regex))
        .map(Map.Entry::getValue)
        .collect(Collectors.toList());
  }

  public String get(String key) {
    return statements.get(key);
  }

  private static String filterStatement(String input) {
    return input
        .replaceAll("\\s{2,}", " ")
        .trim();
  }
}
