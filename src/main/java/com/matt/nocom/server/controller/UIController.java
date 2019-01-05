package com.matt.nocom.server.controller;

import com.matt.nocom.server.service.APIService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UIController {
  private final APIService api;

  public UIController(APIService api) {
    this.api = api;
  }

  @GetMapping({"/", "/login"})
  public String index() {
    return "/login";
  }

  @GetMapping("/overview")
  public String overview(Model model,
      @RequestParam("server") Optional<String> server,
      @RequestParam("dimension") Optional<Integer> dimension,
      @RequestParam("delta") Optional<Integer> delta,
      @RequestParam("minHits") Optional<Integer> hits,
      @RequestParam("range") Optional<Integer> range,
      @RequestParam("startTime") Optional<Long> startTime,
      @RequestParam("endTime") Optional<Long> endTime) {
    model.addAttribute("servers", api.getServers());
    model.addAttribute("dimensions", api.getDimensions());
    return "/secret/overview";
  }

  @GetMapping("/listview")
  public String listView(Model model) {
    model.addAttribute("servers", api.getServers());
    model.addAttribute("dimensions", api.getDimensions());
    return "/secret/listview";
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "/error/access-denied";
  }
}
