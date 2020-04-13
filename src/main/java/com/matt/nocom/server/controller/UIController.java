package com.matt.nocom.server.controller;

import com.matt.nocom.server.model.shared.auth.UserGroup;
import com.matt.nocom.server.model.sql.auth.User;
import com.matt.nocom.server.service.EventRepository;
import com.matt.nocom.server.service.auth.UserRepository;
import com.matt.nocom.server.service.data.NocomRepository;
import com.matt.nocom.server.util.StaticUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UIController {
  private final NocomRepository nocom;
  private final UserRepository login;
  private final EventRepository events;

  @GetMapping({"/", "/login"})
  public String index() {
    return "login";
  }

  @GetMapping("/overview")
  public String overview(Model model) {
    model.addAttribute("servers", nocom.getServers());
    model.addAttribute("dimensions", nocom.getDimensions());
    return "secret/overview";
  }

  @GetMapping("/accounts")
  public String accounts(Model model) {
    List<User> users = login.getUsers();
    users.sort(Comparator.comparing(User::getLevel).reversed());
    model.addAttribute("usersData", users);
    model.addAttribute("authGroups", UserGroup.all());
    return "secret/accounts";
  }
  
  @GetMapping("/account/{username}")
  public String account(@PathVariable("username") String username, Model model) {
    final User accessUser = StaticUtils.getCurrentUserContext()
        .map(User::getUsername)
        .flatMap(login::getUser) // get the current user data
        .orElseThrow(NullPointerException::new);
    model.addAttribute("accessUser", accessUser);
    model.addAttribute("user",
        login.getUser(username).orElseThrow(NullPointerException::new));
    model.addAttribute("authGroups",
        UserGroup.all().stream()
            .filter(v -> v.getLevel() <= accessUser.getLevel())
            .sorted(Comparator.comparing(UserGroup::getLevel).reversed())
            .collect(Collectors.toList()));
    model.addAttribute("activeTokens", login.getUserTokens(username));
    return "secret/account";
  }

  @GetMapping("/events/{page}")
  public String events(Model model,
      @PathVariable("page") Integer page,
      @RequestParam("view") Optional<Integer> view,
      @RequestParam("type") Optional<String> type,
      @RequestParam("beginTime") Optional<Long> beginTime,
      @RequestParam("endTime") Optional<Long> endTime) {
    final int DEFAULT_VIEW = 20;
    model.addAttribute("events", events.getEvents(view.orElse(DEFAULT_VIEW), page,
        type.orElse(null),
        beginTime.map(Instant::ofEpochMilli).orElse(null),
        endTime.map(Instant::ofEpochMilli).orElse(null)));
    model.addAttribute("types", events.getEventTypes());
    model.addAttribute("default_view", DEFAULT_VIEW);
    model.addAttribute("current_view", view.orElse(DEFAULT_VIEW));
    model.addAttribute("current_type", type.orElse(null));
    model.addAttribute("current_page", page);
    model.addAttribute("current_beginTime", beginTime.orElse(-1L));
    model.addAttribute("current_endTime", endTime.orElse(-1L));
    model.addAttribute("max_pages",
        Math.ceil((float)(events.getEventCount()) / (float)(view.orElse(DEFAULT_VIEW))));

    StringBuilder builder = new StringBuilder("?");
    view.filter(v -> v != DEFAULT_VIEW).ifPresent(s -> builder.append("&view=").append(s));
    type.ifPresent(s -> builder.append("&type=").append(s));
    beginTime.ifPresent(s -> builder.append("&beginTime=").append(s));
    endTime.ifPresent(s -> builder.append("&endTime=").append(s));
    model.addAttribute("url_params", builder.toString()
        .replaceFirst("\\?&", "?")
        .replaceFirst("\\?$", ""));

    return "secret/events";
  }

  @GetMapping("/events")
  public String events(Model model,
      @RequestParam("view") Optional<Integer> view,
      @RequestParam("type") Optional<String> type,
      @RequestParam("beginTime") Optional<Long> beginTime,
      @RequestParam("endTime") Optional<Long> endTime) {
    return events(model, 1, view, type, beginTime, endTime);
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "error/access-denied";
  }
}
