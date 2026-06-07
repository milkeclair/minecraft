package com.milkeclair.glacage;

import com.milkeclair.glacage.config.Feature;
import com.milkeclair.glacage.config.feature.SyncPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
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
  public Client(ModContainer container) {
    Config.setSyncer(Client::syncFeature);
    NeoForge.EVENT_BUS.addListener(Client::syncFeaturesOnLogin);

    container.registerExtensionPoint(
        IConfigScreenFactory.class,
        // parentは設定から戻る先のスクリーン。
        (selectedContainer, parent) -> new ConfigurationScreen(selectedContainer, parent));
  }

  private static void syncFeaturesOnLogin(ClientPlayerNetworkEvent.LoggingIn event) {
    Config.syncAll();
  }

  private static void syncFeature(Feature feature) {
    var minecraft = Minecraft.getInstance();
    if (minecraft == null || minecraft.getConnection() == null) {
      return;
    }

    ClientPacketDistributor.sendToServer(new SyncPayload(feature, Config.enabled(feature)));
  }
}
