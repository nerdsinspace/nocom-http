package com.matt.nocom.server.controller;

import com.matt.nocom.server.auth.UserGroup;
import com.matt.nocom.server.service.APIService;
import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.service.LoginManagerService;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpServerErrorException;

@Controller
public class UIController {
  private final APIService api;
  private final LoginManagerService login;
  private final EventService events;

  public UIController(APIService api, LoginManagerService login,
      EventService events) {
    this.api = api;
    this.login = login;
    this.events = events;
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

  @GetMapping("/listview")
  public String listView(Model model) {
    model.addAttribute("servers", api.getServers());
    model.addAttribute("dimensions", api.getDimensions());
    return "secret/listview";
  }

  @GetMapping("/accounts")
  public String accounts(Model model) {
    model.addAttribute("usersData", login.getUserData());
    model.addAttribute("authGroups", UserGroup.production());
    return "secret/accounts";
  }

  @GetMapping("/events/{page}")
  public String events(Model model,
      @PathVariable("page") Integer page,
      @RequestParam("view") Optional<Integer> view,
      @RequestParam("level") Optional<Integer> level,
      @RequestParam("type") Optional<Integer> type,
      @RequestParam("beginTime") Optional<Long> beginTime,
      @RequestParam("endTime") Optional<Long> endTime) {
    final int DEFAULT_VIEW = 20;
    int _view = view.orElse(DEFAULT_VIEW);
    int _level = level.orElse(-1);
    int _type = type.orElse(-1);
    long _beginTime = beginTime.orElse(-1L);
    long _endTime = endTime.orElse(-1L);

    model.addAttribute("events", events.getEvents(_view, page, _level, _type, _beginTime, _endTime));
    model.addAttribute("levels", events.getEventLevels());
    model.addAttribute("types", events.getEventTypes().stream()
        .sorted(Comparator.comparing(e -> e.getType().toLowerCase()))
        .collect(Collectors.toList()));
    model.addAttribute("default_view", DEFAULT_VIEW);
    model.addAttribute("current_view", _view);
    model.addAttribute("current_level", _level);
    model.addAttribute("current_type", _type);
    model.addAttribute("current_page", page);
    model.addAttribute("current_beginTime", _beginTime);
    model.addAttribute("current_endTime", _endTime);
    model.addAttribute("max_pages", Math.ceil((float)(events.getEventCount()) / (float)(_view)));

    StringBuilder builder = new StringBuilder("?");
    if(_view != DEFAULT_VIEW) builder.append("&view=").append(_view);
    if(_level != -1) builder.append("&level=").append(_level);
    if(_type != -1) builder.append("&type=").append(_type);
    if(_beginTime != -1) builder.append("&beginTime=").append(_beginTime);
    if(_endTime != -1) builder.append("&endTime=").append(_endTime);
    model.addAttribute("url_params", builder.toString()
        .replaceFirst("\\?&", "?")
        .replaceFirst("\\?$", ""));

    return "secret/events";
  }

  @GetMapping("/events")
  public String events(Model model,
      @RequestParam("view") Optional<Integer> view,
      @RequestParam("level") Optional<Integer> level,
      @RequestParam("type") Optional<Integer> type,
      @RequestParam("beginTime") Optional<Long> beginTime,
      @RequestParam("endTime") Optional<Long> endTime) {
    return events(model, 1, view, level, type, beginTime, endTime);
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "error/access-denied";
  }
}
