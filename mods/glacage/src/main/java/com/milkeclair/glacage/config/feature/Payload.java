package com.milkeclair.glacage.config.feature;

import com.milkeclair.glacage.Glacage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/* Flagの同期用payload。 */
public record Payload(Flag flag, boolean enabled) implements CustomPacketPayload {
  /* namespace:path(glacage:sync_feature_config) */
  public static final Type<Payload> TYPE =
      new CustomPacketPayload.Type<>(
          Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "sync_feature_config"));

  /* codec */
  public static final StreamCodec<RegistryFriendlyByteBuf, Payload> CODEC =
      StreamCodec.of(Payload::encode, Payload::decode);

  /* このpayloadの型。 */
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  /* サーバーがpayloadを受け取ったときの処理。 */
  public static void handle(Payload payload, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      PlayerPreference.setEnabled(payload.flag(), player, payload.enabled());
    }
  }

  private static void encode(RegistryFriendlyByteBuf buffer, Payload payload) {
    Flag.CODEC.encode(buffer, payload.flag());
    buffer.writeBoolean(payload.enabled());
  }

  private static Payload decode(RegistryFriendlyByteBuf buffer) {
    return new Payload(Flag.CODEC.decode(buffer), buffer.readBoolean());
  }
}
