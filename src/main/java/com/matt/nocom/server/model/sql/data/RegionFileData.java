package com.matt.nocom.server.model.sql.data;

import com.matt.nocom.server.minecraft.world.MinecraftWorld;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegionFileData implements Serializable {

  private String server;

  private MinecraftWorld.Type type;

  private int dimension;

  private int x;

  private int z;

  private String fileName; // unused

  private String base64Data;
}
