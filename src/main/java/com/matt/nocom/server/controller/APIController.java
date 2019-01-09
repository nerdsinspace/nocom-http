package com.matt.nocom.server.controller;

import com.google.common.base.MoreObjects;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.Dimension;
import com.matt.nocom.server.model.Location;
import com.matt.nocom.server.model.LocationGroup;
import com.matt.nocom.server.model.LoginRequest;
import com.matt.nocom.server.model.LoginResponse;
import com.matt.nocom.server.model.SearchFilter;
import com.matt.nocom.server.model.auth.AccessToken;
import com.matt.nocom.server.model.auth.User;
import com.matt.nocom.server.service.APIService;
import com.matt.nocom.server.service.LoginManagerService;
import com.matt.nocom.server.util.Util;
import com.matt.nocom.server.util.factory.AccessTokenFactory;
import com.matt.nocom.server.util.factory.LocationGroupFactory;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("api")
public class APIController implements Logging {
  private final AuthenticationManager auth;
  private final LoginManagerService login;
  private final APIService api;

  public APIController(AuthenticationManager auth,
      LoginManagerService login, APIService api) {
    this.auth = auth;
    this.login = login;
    this.api = api;
  }

  @RequestMapping(value = "authenticate",
      method = RequestMethod.POST,
      consumes = "application/json")
  @ResponseBody
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest details,
      HttpServletRequest request) {
    try {
      String username = details.getUsername();
      Authentication authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(username, details.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      User user = login.getUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
      AccessToken token = AccessTokenFactory.create();
      token.setAddress(Util.stringToAddress(request.getRemoteAddr()));

      login.addUserToken(user, token);

      return ResponseEntity.ok(LoginResponse.builder()
          .username(username)
          .token(token.getToken())
          .build());
    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
    }
  }

  @RequestMapping(value = "/upload/locations",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<Object> addLocations(@RequestBody Location[] locations) {
    if(locations.length < 1)
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Must have at least one location object.");

    api.addServers(Arrays.stream(locations)
        .map(Location::getServer)
        .map(String::toLowerCase)
        .collect(Collectors.toSet()));

    api.addPositions(Arrays.stream(locations)
        .map(Location::getPosition)
        .collect(Collectors.toSet()));

    api.addLocations(Arrays.asList(locations));

    return ResponseEntity.ok().body(null);
  }

  @RequestMapping(value = "/search/locations",
      method = RequestMethod.POST ,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<Location[]> getLocations(@RequestBody SearchFilter filter) {
    return ResponseEntity.ok(api.getLocations(filter).toArray(new Location[0]));
  }

  @RequestMapping(value = "/search/group/locations",
      method = RequestMethod.POST ,
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<LocationGroup[]> getLocationGroups(@RequestBody SearchFilter filter) {
    filter.forceGrouping();
    return ResponseEntity.ok(
        LocationGroupFactory.translate(api.getLocations(filter), filter.getGroupingRange()).stream()
            .filter(g -> g.getPositions().size() >= MoreObjects.firstNonNull(filter.getMinHits(), 0))
            .toArray(LocationGroup[]::new)
    );
  }

  @RequestMapping(value = "/servers", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<String[]> getServers() {
    return ResponseEntity.ok(api.getServers().toArray(new String[0]));
  }

  @RequestMapping(value = "/dimensions", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<Dimension[]> getDimensions() {
    return ResponseEntity.ok(api.getDimensions().toArray(new Dimension[0]));
  }
}
