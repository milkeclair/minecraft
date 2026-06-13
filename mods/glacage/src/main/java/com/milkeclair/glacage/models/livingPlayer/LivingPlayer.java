package com.milkeclair.glacage.models.livingPlayer;

import com.milkeclair.glacage.models.livingPlayer.climb.Climb;
import com.milkeclair.glacage.models.livingPlayer.ground.Contact;
import com.milkeclair.glacage.models.livingPlayer.jump.Jump;
import com.milkeclair.glacage.models.livingPlayer.satiety.Satiety;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/* 生きているプレイヤー。 */
public class LivingPlayer {
  private final Player player;

  public LivingPlayer(Player player) {
    this.player = player;
  }

  /* 地面との接触状態。 */
  public Contact contact() {
    return new Contact(player);
  }

  /* 登れる状態。 */
  public Climb climb() {
    return new Climb(player);
  }

  /* ジャンプ状態。 */
  public Jump jump() {
    return new Jump(player);
  }

  /* 満腹度。 */
  public Satiety satiety() {
    var foodData = player.getFoodData();

    return new Satiety(
        foodData.getFoodLevel(),
        foodData.getSaturationLevel(),
        player.hasEffect(MobEffects.HUNGER));
  }

  /* 移動量をクライアントへ同期する。 */
  public void markMotionChanged() {
    // hurtMarkedはダメージを与えるマークではなく、クライアントへモーションを同期するもの。
    // 本来はノックバックモーションの同期に使うものだったと思われる。
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.hurtMarked = true;
    }
  }
}
