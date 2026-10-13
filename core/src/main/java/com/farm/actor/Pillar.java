package com.farm.actor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import com.farm.base.BaseActor;

/**
 * 柱子实体（实现 Pool.Poolable 以便对象池重用）
 */
public class Pillar extends BaseActor implements Pool.Poolable {

    private TextureRegion region;   // 共享，来自 TextureManager
    private float scrollSpeed;
    private boolean isTop;
    private boolean scored;

    /** 由 Pool 调用的默认构造器（不要在这里初始化成员） */
    public Pillar() {
    }

    /**
     * 初始化方法，替代构造器完成设置（供对象池获取时调用）
     */
    public void init(TextureRegion region, float scrollSpeed,
                     float x, float y, float width, float height,
                     boolean isTop) {
        this.region = region;
        this.scrollSpeed = scrollSpeed;
        this.isTop = isTop;
        this.scored = isTop; // 顶部柱子不参与计分，底部柱子通过后计一次分
        bounds.set(x, y, width, height);
    }

    public boolean isTop() {
        return isTop;
    }

    public boolean isScored() {
        return scored;
    }

    public void markScored() {
        this.scored = true;
    }

    public boolean isOffScreen() {
        return bounds.x + bounds.width < 0f;
    }

    @Override
    public void update(float dt) {
        bounds.x -= scrollSpeed * dt;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isTop) {
            // 上下翻转：高度设为负值，同时调整y坐标
            batch.draw(region, bounds.x, bounds.y + bounds.height, bounds.width, -bounds.height);
        } else {
            batch.draw(region, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    /** Pool.Poolable 接口方法，对象回收时自动调用 */
    @Override
    public void reset() {
        // 重置为安全状态，等待下一次 init
        region = null;
        scrollSpeed = 0f;
        isTop = false;
        scored = false;
        bounds.set(0, 0, 0, 0);
    }
}
