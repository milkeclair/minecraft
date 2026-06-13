package com.milkeclair.glacage.usecases.parkour.doubleJump;

import com.milkeclair.glacage.Glacage;
import com.milkeclair.glacage.config.feature.Feature;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/* 二段ジャンプ用パケット。 */
public record Request() implements CustomPacketPayload {
  /* 識別子はglacage:double_jump。 */
  public static final Type<Request> TYPE =
      new CustomPacketPayload.Type<>(
          Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "double_jump"));

  /* 符号化。 */
  public static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC =
      StreamCodec.of(Request::encode, Request::decode);

  /* このパケットの型。 */
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  /* サーバーがパケットを受け取ったときの処理。 */
  public static void handle(Request request, IPayloadContext context) {
    if (!(context.player() instanceof ServerPlayer player)) {
      return;
    }

    if (!Feature.enabled(Feature.PARKOUR.DOUBLE_JUMP, player)) {
      return;
    }

    Server.call(player);
  }

  private static void encode(RegistryFriendlyByteBuf buffer, Request request) {}

  private static Request decode(RegistryFriendlyByteBuf buffer) {
    return new Request();
  }
}
