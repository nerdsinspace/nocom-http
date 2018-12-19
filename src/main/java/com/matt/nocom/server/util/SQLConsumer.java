package com.matt.nocom.server.util;

import java.sql.SQLException;

public interface SQLConsumer<T> {
  void accept(T t) throws SQLException;
}
