package com.matt.nocom.server.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.MoreObjects;
import com.matt.nocom.server.util.Region;
import com.matt.nocom.server.util.WorldEntry;
import com.matt.nocom.server.util.VectorXZ;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.matt.nocom.server.data.worlds.MinecraftWorld;
import com.matt.nocom.server.data.worlds.MinecraftWorld.Type;
import com.matt.nocom.server.model.game.LocationGroup.Serializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
public class LocationGroup implements Serializable, VectorXZ, WorldEntry {
  private String server;
  private int dimension;

  @Default
  private Region region = new Region();

  @Singular
  private List<Position> positions;

  public LocationGroup setup() {
    final Region region = getRegion();
    if(getPositions().isEmpty()) {
      region.setMaxX(Integer.MAX_VALUE);
      region.setMinX(Integer.MAX_VALUE);
      region.setMaxZ(Integer.MAX_VALUE);
      region.setMinZ(Integer.MAX_VALUE);
      return this;
    }

    for(Position position : getPositions()) {
      if(position.getX() > region.getMaxX() || region.getMaxX() == Integer.MAX_VALUE)
        region.setMaxX(position.getX());

      if(position.getX() < region.getMinX() || region.getMinX() == Integer.MAX_VALUE)
        region.setMinX(position.getX());

      if(position.getZ() > region.getMaxZ() || region.getMaxZ() == Integer.MAX_VALUE)
        region.setMaxZ(position.getZ());

      if(position.getZ() < region.getMinZ() || region.getMinZ() == Integer.MAX_VALUE)
        region.setMinZ(position.getZ());
    }

    return this;
  }

  @JsonInclude
  public int getX() {
    return getRegion().getMinX() + ((getRegion().getMaxX() - getRegion().getMinX()) / 2);
  }

  @JsonInclude
  public int getZ() {
    return getRegion().getMinZ() + ((getRegion().getMaxZ() - getRegion().getMinZ()) / 2);
  }

  private int distanceSqTo(int otherX, int otherZ) {
    return (int) Math.ceil(Math.pow(otherX - getX(), 2) + Math.pow(otherZ - getZ(), 2));
  }
  private int distanceSqTo(LocationGroup other) {
    return distanceSqTo(other.getX(), other.getZ());
  }

  public boolean isInRegion(LocationGroup other) {
    return getRegion().intersects(other.getRegion());
  }

  public boolean isInGroup(LocationGroup other, int distance) {
    return distanceSqTo(other) <= distance;
  }
  public static class LocationGroupBuilder {
    public LocationGroup build() {
      return new LocationGroup(server, dimension, MoreObjects.firstNonNull(region, new Region()), positions).setup();
    }
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

        gen.writeBooleanField("downloaded_exists", renderExists(value, Type.DOWNLOADED));
        gen.writeBooleanField("generated_exists", renderExists(value, Type.GENERATED));

        gen.writeArrayFieldStart("positions");
        for (Position pos : value.getPositions()) gen.writeObject(pos);
        gen.writeEndArray();

      gen.writeEndObject();
    }

    private boolean renderExists(LocationGroup group, Type type) {
      return new MinecraftWorld(group.getServer())
          .ofType(type)
          .dimension(group.getDimension())
          .hasRegionAt(group.getX(), group.getZ());
    }
  }
}
