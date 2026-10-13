package com.farm.bean;

/**
 * 游戏运行时配置数据
 * 在游戏初始化时从纹理尺寸计算得出，之后保持不变
 */
public class GameConfig {

    // ===== 世界尺寸 =====
    public int worldWidth;
    public int worldHeight;

    // ===== 物理参数 =====
    public float gravity;
    public float flapVelocity;
    public float scrollSpeed;

    // ===== 柱子参数 =====
    public float gapSize;
    public float spawnInterval;
    public float pillarWidth;

    // ===== 鸟的显示尺寸 =====
    public float birdDisplayHeight;

    /**
     * 根据背景纹理尺寸初始化所有配置参数
     */
    public void initFromWorldSize(int bgWidth, int bgHeight) {
        this.worldWidth = bgWidth;
        this.worldHeight = bgHeight;

        this.gravity = bgHeight * com.farm.config.Constants.GRAVITY_FACTOR;
        this.flapVelocity = bgHeight * com.farm.config.Constants.FLAP_FACTOR;
        this.scrollSpeed = bgWidth * com.farm.config.Constants.SCROLL_FACTOR;
        this.gapSize = bgHeight * com.farm.config.Constants.GAP_FACTOR;
        this.spawnInterval = bgWidth * com.farm.config.Constants.SPAWN_FACTOR;
        this.pillarWidth = bgWidth * com.farm.config.Constants.PILLAR_W_FACTOR;

        this.birdDisplayHeight = bgHeight * com.farm.config.Constants.BIRD_HEIGHT_FACTOR;
    }
}
