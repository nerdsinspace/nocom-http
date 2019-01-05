package com.matt.nocom.server.minecraft;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.Block;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

// TODO: make this compile and work without dependency
public class BiomeSupplier {
  private static final Cache<Long, BiomeProvider> CACHE = CacheBuilder.newBuilder()
      .expireAfterWrite(5, TimeUnit.MINUTES)
      //.expireAfterAccess(5, TimeUnit.MINUTES)
      .build();

  public static Biome getBiome(long seed, int x, int z) {
    try {
      BiomeProvider provider = CACHE.get(seed, () -> newBiomeProvider(seed));
      return provider.getBiome(new BlockPos(x, 0, z));
    } catch (ExecutionException ex){
      throw new RuntimeException(ex);
    }
  }

  private static BiomeProvider newBiomeProvider(long seed) {
    return new BiomeProvider(seed, WorldType.DEFAULT, "");
  }

  static {
    SoundEvent.registerSounds();
    Block.registerBlocks();
    Biome.registerBiomes();
  }

}
