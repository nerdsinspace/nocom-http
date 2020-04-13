package com.matt.nocom.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {
  private final EventRepository events;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    events.publishSystem("Application.Ready", "Application started");
  }
}
