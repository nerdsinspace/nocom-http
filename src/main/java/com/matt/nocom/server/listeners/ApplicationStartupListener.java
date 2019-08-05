package com.matt.nocom.server.listeners;

import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.util.EventTypeRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {
  private final EventService events;

  public ApplicationStartupListener(EventService events) {
    this.events = events;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    events.publishSystemInfo(EventTypeRegistry.READY, "Application started");
  }
}
