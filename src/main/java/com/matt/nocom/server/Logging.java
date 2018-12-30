package com.matt.nocom.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Logging {
  Logger LOGGER = LogManager.getLogger("NoCom");

  static Logger getLogger() {
    return LOGGER;
  }
}
