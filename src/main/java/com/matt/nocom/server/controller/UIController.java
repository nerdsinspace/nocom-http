package com.matt.nocom.server.controller;

import com.matt.nocom.server.auth.UserGroup;
import com.matt.nocom.server.service.APIService;
import com.matt.nocom.server.service.LoginManagerService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UIController {
  private final APIService api;
  private final LoginManagerService login;

  public UIController(APIService api, LoginManagerService login) {
    this.api = api;
    this.login = login;
  }

  @GetMapping({"/", "/login"})
  public String index() {
    return "login";
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
    return "secret/overview";
  }

  @GetMapping("/manager")
  public String manager(Model model) {
    model.addAttribute("usersData", login.getUserData());
    model.addAttribute("authGroups", login.getGroups().stream()
        .filter(UserGroup::isAllowed)
        .collect(Collectors.toList()));
    return "secret/manager";
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "error/access-denied";
  }
}
