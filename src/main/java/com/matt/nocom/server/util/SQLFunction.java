package com.matt.nocom.server.util;

import java.sql.SQLException;

public interface SQLFunction<T, R> {
  R apply(T o) throws SQLException;
}
