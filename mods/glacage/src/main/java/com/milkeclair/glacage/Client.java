package com.milkeclair.glacage;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.Payload;
import com.milkeclair.glacage.usecases.foodie.Foodie;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;

/* クライアント側。 */
@Mod(value = Glacage.MOD_ID, dist = Dist.CLIENT)
public class Client {
  public Client(IEventBus modEventBus, ModContainer container) {
    ClientConfig.setSyncer(Client::syncFlag);
    modEventBus.register(new Foodie());
    NeoForge.EVENT_BUS.addListener(Client::syncFlagsOnLogin);

    container.registerExtensionPoint(
        IConfigScreenFactory.class,
        // parentは設定から戻る先のスクリーン。
        (selectedContainer, parent) -> new ConfigurationScreen(selectedContainer, parent));
  }

  private static void syncFlagsOnLogin(ClientPlayerNetworkEvent.LoggingIn event) {
    ClientConfig.syncAll();
  }

  private static void syncFlag(Flag flag) {
    var minecraft = Minecraft.getInstance();
    if (minecraft == null || minecraft.getConnection() == null) {
      return;
    }

    ClientPacketDistributor.sendToServer(new Payload(flag, ClientConfig.enabled(flag)));
  }
}
