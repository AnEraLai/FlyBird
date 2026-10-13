package com.farm.listener;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * 游戏输入监听器
 * 负责捕获触摸和键盘事件，维护帧级输入状态
 */
public class GameInputListener extends InputAdapter {

    /** 本帧是否发生触摸 */
    private boolean justTouched = false;

    /** 本帧是否按下空格 */
    private boolean justPressedSpace = false;

    /** 最近一次触摸的屏幕坐标 */
    private float touchScreenX = 0f;
    private float touchScreenY = 0f;

    /**
     * 每帧开始时调用，重置帧级状态
     */
    public void resetFrameState() {
        justTouched = false;
        justPressedSpace = false;
    }

    public boolean isJustTouched() {
        return justTouched;
    }

    public boolean isJustPressedSpace() {
        return justPressedSpace;
    }

    public float getTouchScreenX() {
        return touchScreenX;
    }

    public float getTouchScreenY() {
        return touchScreenY;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        justTouched = true;
        touchScreenX = screenX;
        touchScreenY = screenY;
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            justPressedSpace = true;
            return true;
        }
        return false;
    }
}
