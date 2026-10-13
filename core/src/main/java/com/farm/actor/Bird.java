package com.farm.actor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.farm.base.BaseActor;
import com.farm.bean.GameConfig;
import com.farm.config.Constants;

/**
 * 小鸟实体（使用 TextureRegion 绘制）
 */
public class Bird extends BaseActor {

    private final GameConfig config;
    private final TextureRegion region;
    private float velocity;

    public Bird(GameConfig config, TextureRegion region) {
        this.config = config;
        this.region = region;
        reset();
    }

    @Override
    public void reset() {
        float displayH = config.birdDisplayHeight;
        float scale = displayH / region.getRegionHeight();
        bounds.width = region.getRegionWidth() * scale;
        bounds.height = displayH;
        bounds.x = config.worldWidth * Constants.BIRD_X_POSITION_FACTOR - bounds.width / 2f;
        bounds.y = config.worldHeight * 0.5f - bounds.height / 2f;
        velocity = 0f;
    }

    public void flap() {
        velocity = config.flapVelocity;
    }

    @Override
    public void update(float dt) {
        velocity -= config.gravity * dt;
        bounds.y += velocity * dt;

        if (bounds.y <= 0f) {
            bounds.y = 0f;
        }
        if (bounds.y + bounds.height > config.worldHeight) {
            bounds.y = config.worldHeight - bounds.height;
            velocity = 0f;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        float angle = clamp(velocity / config.flapVelocity * Constants.MAX_BIRD_ANGLE,
            -Constants.MAX_BIRD_ANGLE, Constants.MAX_BIRD_ANGLE);

        batch.draw(region,
            bounds.x, bounds.y,
            bounds.width / 2f, bounds.height / 2f,
            bounds.width, bounds.height,
            1f, 1f, angle);
    }

    public boolean isOnGround() {
        return bounds.y <= 0f;
    }

    private static float clamp(float v, float min, float max) {
        return v < min ? min : (v > max ? max : v);
    }
}
