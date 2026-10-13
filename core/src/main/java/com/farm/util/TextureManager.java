package com.farm.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * 纹理资源管理器（回退版：不使用图集）
 * 直接加载独立纹理并包装为 TextureRegion，后续可替换为图集优化
 */
public class TextureManager implements Disposable {

    // 背景纹理（独立，因为尺寸可能较大）
    private final Texture backgroundTexture;
    // 其他纹理包装为 TextureRegion，便于统一绘制接口
    private final Texture birdTexture;
    private final Texture obstacleTexture;
    private final Texture buttonTexture;
    private final Texture panelTexture;
    private final TextureRegion birdRegion;
    private final TextureRegion obstacleRegion;
    private final TextureRegion buttonRegion;
    private final TextureRegion panelRegion;

    public TextureManager() {
        // 加载背景
        backgroundTexture = new Texture("sky_clean.png");

        // 加载其他纹理
        birdTexture = new Texture("bird_clean.png");
        obstacleTexture = new Texture("obstacle.png");
        buttonTexture = new Texture("ui_button.png");
        panelTexture = new Texture("ui_panel.png");

        // 包装为 TextureRegion
        birdRegion = new TextureRegion(birdTexture);
        obstacleRegion = new TextureRegion(obstacleTexture);
        buttonRegion = new TextureRegion(buttonTexture);
        panelRegion = new TextureRegion(panelTexture);

        // 设置纹理过滤（可选）
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        birdTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        panelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public TextureRegion getBirdRegion() {
        return birdRegion;
    }

    public TextureRegion getObstacleRegion() {
        return obstacleRegion;
    }

    public TextureRegion getButtonRegion() {
        return buttonRegion;
    }

    public TextureRegion getPanelRegion() {
        return panelRegion;
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        birdTexture.dispose();
        obstacleTexture.dispose();
        buttonTexture.dispose();
        panelTexture.dispose();
    }
}
