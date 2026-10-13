package com.farm.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.farm.config.Constants;

/**
 * 文字绘制辅助工具（优化版）
 * 使用预缩放字体实例，避免每帧 setScale 和重复布局
 */
public class DrawHelper {

    private final BitmapFont fontBig;
    private final BitmapFont fontMed;
    private final GlyphLayout layout = new GlyphLayout();
    private final float shadowOffset;

    public DrawHelper(BitmapFont fontBig, BitmapFont fontMed, float worldHeight) {
        this.fontBig = fontBig;
        this.fontMed = fontMed;
        this.shadowOffset = worldHeight * Constants.SHADOW_OFFSET_FACTOR;
    }

    /**
     * 使用指定字体居中绘制文字（带阴影）
     */
    private void drawCentered(SpriteBatch batch, BitmapFont font, String text,
                              float cx, float cy, boolean shadow, Color color) {
        if (shadow) {
            font.setColor(Constants.TXT_SHADOW);
            layout.setText(font, text);
            font.draw(batch, layout,
                cx - layout.width / 2f + shadowOffset,
                cy + layout.height / 2f - shadowOffset);
        }
        font.setColor(color);
        layout.setText(font, text);
        font.draw(batch, layout,
            cx - layout.width / 2f,
            cy + layout.height / 2f);
    }

    /** 使用大号字体绘制 */
    public void drawBig(SpriteBatch batch, String text, float cx, float cy, boolean shadow) {
        drawCentered(batch, fontBig, text, cx, cy, shadow, Constants.TXT_MAIN);
    }

    /** 使用中号字体绘制 */
    public void drawMed(SpriteBatch batch, String text, float cx, float cy, boolean shadow) {
        drawCentered(batch, fontMed, text, cx, cy, shadow, Constants.TXT_MAIN);
    }

    public void drawDarkBig(SpriteBatch batch, String text, float cx, float cy) {
        drawCentered(batch, fontBig, text, cx, cy, false, Constants.TXT_DARK);
    }

    public void drawDarkMed(SpriteBatch batch, String text, float cx, float cy) {
        drawCentered(batch, fontMed, text, cx, cy, false, Constants.TXT_DARK);
    }
}
