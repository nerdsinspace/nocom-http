package com.matt.nocom.server.controller;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.matt.nocom.server.Logging;
import com.matt.nocom.server.model.http.data.SearchFilter;
import com.matt.nocom.server.model.shared.data.Dimension;
import com.matt.nocom.server.model.shared.data.LocationGroup;
import com.matt.nocom.server.model.sql.data.Location;
import com.matt.nocom.server.model.sql.data.Position;
import com.matt.nocom.server.service.EventService;
import com.matt.nocom.server.service.data.APIService;
import com.matt.nocom.server.util.EventTypeRegistry;
import com.matt.nocom.server.util.kdtree.KdNode;
import com.matt.nocom.server.util.kdtree.KdTree;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@PropertySource("database.properties")
public class APIController implements Logging {
  private final Environment env;
  private final APIService api;
  private final EventService events;

  public APIController(Environment env, APIService api,
      EventService events) {
    this.env = env;
    this.api = api;
    this.events = events;
  }

  @RequestMapping(value = "/upload/locations",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void addLocations(@RequestBody Location[] locations) {
    if (locations.length < 1)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Must contain at least one location object.");

    int n; // count of inserts

    n = Arrays.stream(
        api.addServers(Arrays.stream(locations)
            .map(Location::getServer)
            .map(String::toLowerCase)
            .collect(Collectors.toSet()))
    ).sum();

    if(n > 0)
      events.publishInfo(EventTypeRegistry.API__ADD_LOCATION__SERVERS, "Added %d new servers", n);

    n = Arrays.stream(
        api.addPositions(Arrays.stream(locations)
            .map(Location::toPosition)
            .collect(Collectors.toSet()))
    ).sum();

    if(n > 0)
      events.publishInfo(EventTypeRegistry.API__ADD_LOCATION__POSITIONS, "Added %d new unique coordinates", n);

    n = Arrays.stream(
        api.addLocations(Arrays.asList(locations))
    ).sum();

    if(n > 0)
      events.publishInfo(EventTypeRegistry.API__ADD_LOCATION, "Added %d total locations", n);
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
        cluster(api.getLocations(filter), filter.getGroupingRange()).stream()
            .filter(g -> g.getPositions().size() >= MoreObjects.firstNonNull(filter.getMinHits(), 0))
            .toArray(LocationGroup[]::new)
    );
  }


  @RequestMapping(value = "/search/locations/list",
      method = RequestMethod.POST ,
      consumes = "application/json",
      produces = MediaType.TEXT_PLAIN_VALUE)
  @ResponseBody
  public ResponseEntity<String> listLocations(@RequestBody SearchFilter filter) {
    return ResponseEntity.ok(api.getLocations(filter).stream()
        .map(Location::toPosition)
        .map(Position::toString)
        .collect(Collectors.joining("\n")));
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
  

  @RequestMapping(value = "/database/download",
      method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Resource> databaseDownload() throws IOException {
    events.publishInfo(EventTypeRegistry.API__DOWNLOAD_DATABASE, "Downloaded the database");

    Path path = Paths.get("")
        .resolve(env.getRequiredProperty("db.file"));

    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=" + path.getFileName().toString())
        .contentLength(Files.size(path))
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(resource);
  }
  
  private List<LocationGroup> cluster(List<Location> inputs, int range) {
    if(inputs.isEmpty())
      return Collections.emptyList();
    
    final int rangeSq = range*range;
    
    KdTree<Location> tree = new KdTree<>(inputs);
    
    List<LocationGroup> cluster = Lists.newArrayListWithCapacity(inputs.size());
    final Location base = tree.getRoot().getReference();
    
    KdNode<Location> next;
    while((next = tree.getRightMost()) != null) {
      List<KdNode<Location>> nodes;
      if(rangeSq <= 0) {
        nodes = Collections.singletonList(next);
      } else {
        nodes = tree.radiusSq(next, rangeSq);
        if(nodes.isEmpty()) {
          tree.remove(next);
          continue;
        }
      }
      
      LocationGroup loc = new LocationGroup();
      loc.setServer(base.getServer());
      loc.setDimension(base.getDimension());
      loc.setPositions(nodes.stream()
          .map(KdNode::getReferences)
          .flatMap(List::stream)
          .map(Location::toPosition)
          .sorted(Comparator.comparingLong(Position::getTime))
          .collect(Collectors.toList()));
      loc.setup();
      
      nodes.forEach(tree::removeNode);
      
      cluster.add(loc);
    }
    
    return cluster;
  }
}
