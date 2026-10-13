package com.farm;

import com.badlogic.gdx.ApplicationAdapter;
import com.farm.screen.GameScreen;

/**
 * 游戏入口类
 * 负责初始化GameScreen并委托渲染循环
 */
public class FlybirdGame extends ApplicationAdapter {

    private GameScreen gameScreen;

    @Override
    public void create() {
        gameScreen = new GameScreen();
    }

    @Override
    public void render() {
        gameScreen.render();
    }

    @Override
    public void resize(int width, int height) {
        if (gameScreen != null) {
            gameScreen.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }
}
