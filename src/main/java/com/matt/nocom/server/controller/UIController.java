package com.matt.nocom.server.controller;

import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UIController {
  @GetMapping("/")
  public String home(Model model,
      @RequestParam("server") Optional<String> server,
      @RequestParam("dimension") Optional<Integer> dimension,
      @RequestParam("startTime") Optional<Long> startTime,
      @RequestParam("endTime") Optional<Long> endTime) {
    return "home";
  }
}
