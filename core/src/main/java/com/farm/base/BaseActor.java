package com.farm.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * 所有游戏实体的基类
 * 提供基础的位置/尺寸信息和生命周期方法
 */
public abstract class BaseActor {

    /** 实体的碰撞盒/位置信息 */
    protected final Rectangle bounds = new Rectangle();

    /**
     * 获取实体的边界矩形
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * 更新实体逻辑
     * @param dt 帧间隔时间（秒）
     */
    public abstract void update(float dt);

    /**
     * 绘制实体
     * @param batch SpriteBatch实例
     */
    public abstract void draw(SpriteBatch batch);

    /**
     * 重置实体到初始状态
     */
    public abstract void reset();
}
