package com.matt.nocom.server.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.matt.nocom.server.model.game.LocationGroup.Serializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(using = Serializer.class)
public class LocationGroup implements Serializable {
  private int x;
  private int z;
  private String server;
  private int dimension;

  @Singular
  private List<Position> positions;

  public LocationGroup setup() {
    setX(getPositions().stream().collect(Collectors.averagingInt(Position::getX)).intValue());
    setZ(getPositions().stream().collect(Collectors.averagingInt(Position::getZ)).intValue());
    return this;
  }


  private int distanceSqTo(int otherX, int otherZ) {
    return (int) Math.ceil(Math.pow(otherX - getX(), 2) + Math.pow(otherZ - getZ(), 2));
  }
  private int distanceSqTo(LocationGroup other) {
    return distanceSqTo(other.getX(), other.getZ());
  }

  @JsonIgnore
  public boolean isInGroup(LocationGroup other, int distance) {
    return distanceSqTo(other) <= distance;
  }

  @JsonIgnore
  public boolean isInSameWorld(LocationGroup other) {
    return getServer().equalsIgnoreCase(other.getServer()) && getDimension() == other.getDimension();
  }

  public static class Serializer extends JsonSerializer<LocationGroup> {

    @Override
    public void serialize(LocationGroup value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeStartObject();
        gen.writeNumberField("x", value.getX());
        gen.writeNumberField("z", value.getZ());
        gen.writeStringField("server", value.getServer());
        gen.writeNumberField("dimension", value.getDimension());

        gen.writeStringField("downloaded", "2b2t.org/DOWNLOADED/0/0,0");
        //gen.writeNullField("generated");
        gen.writeStringField("generated", "2b2t.org/DOWNLOADED/0/0,0");

        gen.writeArrayFieldStart("positions");
        for (Position pos : value.getPositions()) gen.writeObject(pos);
        gen.writeEndArray();

      gen.writeEndObject();
    }
  }
}
