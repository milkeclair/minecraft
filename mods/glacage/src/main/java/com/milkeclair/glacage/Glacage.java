package com.milkeclair.glacage;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Glacage.MOD_ID)
public class Glacage {
  public static final String MOD_ID = "glacage";

  private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public Glacage() {
    NeoForge.EVENT_BUS.register(new Lumberjack());
    LOGGER.info("Glacage initialized");
  }
}
