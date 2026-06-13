package com.milkeclair.glacage.models.livingPlayer.satiety;

/*
 * 満腹度のバー。
 * rightHeightは満腹度、酸素などホットバー右に詰まれるHUDの高さ。
 */
public record Bar(int guiWidth, int guiHeight, int rightHeight) {
  private static final int FOOD_BAR_RIGHT_OFFSET = 91;
  private static final int FOOD_LEVEL_HEIGHT = 10;
  private static final int ICON_SIZE = 9;
  private static final int ICON_STEP = 8;

  /*
   * x座標。
   */
  public int x(int index) {
    var guiCenter = guiWidth / 2;
    var foodBarRight = guiCenter + FOOD_BAR_RIGHT_OFFSET;
    // 現在アイコンの左端を見たいので、ICON_SIZEを引く。
    return foodBarRight - index * ICON_STEP - ICON_SIZE;
  }

  /* y座標。 */
  public int y() {
    // rightHeightにfood levelが含まれているので、その分戻る。
    return guiHeight - rightHeight + FOOD_LEVEL_HEIGHT;
  }
}
