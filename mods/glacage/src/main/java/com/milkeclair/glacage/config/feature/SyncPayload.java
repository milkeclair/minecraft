package com.milkeclair.glacage.config.feature;

import com.milkeclair.glacage.Glacage;
import com.milkeclair.glacage.config.Feature;
import com.milkeclair.glacage.config.PlayerSettings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/* Featureの同期用payload。 */
public record SyncPayload(Feature feature, boolean enabled) implements CustomPacketPayload {
  /* namespace:path(glacage:sync_feature_config) */
  public static final Type<SyncPayload> TYPE =
      new CustomPacketPayload.Type<>(
          Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "sync_feature_config"));

  /* codec */
  public static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> CODEC =
      StreamCodec.of(SyncPayload::encode, SyncPayload::decode);

  @Override
  /* このpayloadの型。 */
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  /* サーバーがpayloadを受け取ったときの処理。 */
  public static void handle(SyncPayload payload, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      PlayerSettings.setEnabled(payload.feature(), player, payload.enabled());
    }
  }

  private static void encode(RegistryFriendlyByteBuf buffer, SyncPayload payload) {
    Feature.CODEC.encode(buffer, payload.feature());
    buffer.writeBoolean(payload.enabled());
  }

  private static SyncPayload decode(RegistryFriendlyByteBuf buffer) {
    return new SyncPayload(Feature.CODEC.decode(buffer), buffer.readBoolean());
  }
}
