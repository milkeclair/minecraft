package com.milkeclair.glacage;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Glacage.MOD_ID)
public class Glacage {
  /** ModのID。 */
  public static final String MOD_ID = "glacage";

  private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public Glacage() {
    new EventRegistration().call();
    LOGGER.info("Glacage initialized");
  }
}
