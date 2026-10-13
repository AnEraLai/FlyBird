package com.farm.bean;

/**
 * 游戏运行时可变状态
 * 保存游戏进行中的动态数据
 */
public class GameState {

    /** 是否已经开始（第一次输入后为true） */
    public boolean started = false;

    /** 是否游戏结束 */
    public boolean gameOver = false;

    /** 当前得分 */
    public int score = 0;

    /** 背景滚动偏移 */
    public float bgScrollX = 0f;

    /** 距离上次生成柱子的累积距离 */
    public float distanceSinceSpawn = 0f;

    /**
     * 重置为初始状态
     */
    public void reset() {
        started = false;
        gameOver = false;
        score = 0;
        bgScrollX = 0f;
        distanceSinceSpawn = 0f;
    }
}
