package com.farm.dialog;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.farm.bean.GameConfig;
import com.farm.config.Constants;

/**
 * 游戏结束弹窗（使用 TextureRegion）
 */
public class GameOverDialog {

    private final TextureRegion panelRegion;
    private final TextureRegion buttonRegion;
    private final GameConfig config;

    public GameOverDialog(TextureRegion panelRegion, TextureRegion buttonRegion, GameConfig config) {
        this.panelRegion = panelRegion;
        this.buttonRegion = buttonRegion;
        this.config = config;
    }

    private Rectangle getButtonRect() {
        float bw = config.worldWidth * Constants.BUTTON_WIDTH_FACTOR;
        float bh = config.worldHeight * Constants.BUTTON_HEIGHT_FACTOR;
        float bx = (config.worldWidth - bw) / 2f;
        float by = config.worldHeight * Constants.BUTTON_Y_FACTOR;
        return new Rectangle(bx, by, bw, bh);
    }

    public boolean containsButton(float worldX, float worldY) {
        return getButtonRect().contains(worldX, worldY);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(panelRegion,
            config.worldWidth * 0.12f, config.worldHeight * 0.24f,
            config.worldWidth * 0.76f, config.worldHeight * 0.58f);
        Rectangle button = getButtonRect();
        batch.draw(buttonRegion, button.x, button.y, button.width, button.height);
    }
}
