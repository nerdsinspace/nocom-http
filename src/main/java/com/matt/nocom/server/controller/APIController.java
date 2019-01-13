package com.matt.nocom.server.controller;

import com.google.common.base.MoreObjects;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.game.Dimension;
import com.matt.nocom.server.model.game.Location;
import com.matt.nocom.server.model.game.LocationGroup;
import com.matt.nocom.server.model.game.SearchFilter;
import com.matt.nocom.server.service.APIService;
import com.matt.nocom.server.util.factory.LocationGroupFactory;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api")
public class APIController implements Logging {
  private final APIService api;

  public APIController(APIService api) {
    this.api = api;
  }

  @RequestMapping(value = "/upload/locations",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void addLocations(@RequestBody Location[] locations) {
    if(locations.length < 1)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must contain at least one location object.");

    api.addServers(Arrays.stream(locations)
        .map(Location::getServer)
        .map(String::toLowerCase)
        .collect(Collectors.toSet()));

    api.addPositions(Arrays.stream(locations)
        .map(Location::getPosition)
        .collect(Collectors.toSet()));

    api.addLocations(Arrays.asList(locations));
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
