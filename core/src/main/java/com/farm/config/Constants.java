package com.farm.config;

import com.badlogic.gdx.graphics.Color;

/**
 * 游戏所有静态常量配置
 * 集中管理便于调参和维护
 */
public final class Constants {

    // ===== 比例参数（基于世界尺寸） =====
    public static final float GRAVITY_FACTOR      = 1.8f;
    public static final float FLAP_FACTOR         = 0.45f;
    public static final float SCROLL_FACTOR       = 0.45f;
    public static final float GAP_FACTOR          = 0.32f;
    public static final float SPAWN_FACTOR        = 0.85f;
    public static final float PILLAR_W_FACTOR     = 0.11f;
    public static final float BIRD_HEIGHT_FACTOR  = 0.09f;

    // ===== UI 缩放因子 =====
    public static final float SHADOW_OFFSET_FACTOR = 0.002f;

    // ===== UI 颜色 =====
    public static final Color TXT_MAIN   = new Color(1f, 1f, 1f, 1f);
    public static final Color TXT_SHADOW = new Color(0f, 0f, 0f, 0.85f);
    public static final Color TXT_DARK   = new Color(0.09f, 0.32f, 0.39f, 1f);

    // ===== UI 布局比例 =====
    public static final float BUTTON_WIDTH_FACTOR       = 0.53f;
    public static final float BUTTON_HEIGHT_FACTOR      = 0.084f;
    public static final float BUTTON_Y_FACTOR           = 0.32f;

    // ===== 柱子生成参数 =====
    public static final float PILLAR_MIN_EDGE_FACTOR    = 0.12f;

    // ===== 游戏限制 =====
    public static final float MAX_DELTA_TIME             = 0.05f;
    public static final float MAX_BIRD_ANGLE             = 25f;
    public static final float BIRD_X_POSITION_FACTOR     = 0.25f;

    private Constants() {
        // 禁止实例化
    }
}
