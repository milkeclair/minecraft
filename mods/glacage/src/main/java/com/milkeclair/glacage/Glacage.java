package com.milkeclair.glacage;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.Network;
import com.milkeclair.glacage.config.ServerConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Glacage.MOD_ID)
public class Glacage {
  /** ModのID。 */
  public static final String MOD_ID = "glacage";

  private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public Glacage(IEventBus modEventBus, ModContainer container) {
    ClientConfig.register(container, modEventBus);
    ServerConfig.register(container);
    modEventBus.addListener(RegisterPayloadHandlersEvent.class, Network::register);
    new EventRegistration().call();

    LOGGER.info("Glacage initialized");
  }
}
