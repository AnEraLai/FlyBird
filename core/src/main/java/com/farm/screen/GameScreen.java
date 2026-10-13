package com.farm.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.farm.actor.Bird;
import com.farm.actor.Pillar;
import com.farm.bean.GameConfig;
import com.farm.bean.GameState;
import com.farm.config.Constants;
import com.farm.dialog.GameOverDialog;
import com.farm.listener.GameInputListener;
import com.farm.util.DrawHelper;
import com.farm.util.TextureManager;

import java.util.Random;

/**
 * 主游戏场景（优化版：对象池、预创建字体）
 */
public class GameScreen {

    // ===== 渲染相关 =====
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    // ===== 预创建字体（避免每帧缩放） =====
    private final BitmapFont fontBig;
    private final BitmapFont fontMed;

    // ===== 资源管理 =====
    private final TextureManager textureManager;
    private final DrawHelper drawHelper;

    // ===== 数据模型 =====
    private final GameConfig config;
    private final GameState state;

    // ===== 游戏实体 =====
    private Bird bird;
    private final Array<Pillar> pillars = new Array<>();

    private int highScore;

    // ===== 柱子对象池 =====
    private final Pool<Pillar> pillarPool = new Pool<Pillar>() {
        @Override
        protected Pillar newObject() {
            return new Pillar();
        }
    };

    // ===== UI组件 =====
    private GameOverDialog gameOverDialog;

    // ===== 输入监听 =====
    private final GameInputListener inputListener;

    // ===== 随机数 =====
    private final Random random = new Random();

    // ===== 临时变量（避免GC） =====
    private final Vector2 tempWorldPos = new Vector2();

    public GameScreen() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        textureManager = new TextureManager();

        config = new GameConfig();
        state = new GameState();
        highScore = Gdx.app.getPreferences("flybird").getInteger("highscore", 0);
        inputListener = new GameInputListener();

        // 初始化配置
        config.initFromWorldSize(
            textureManager.getBackgroundTexture().getWidth(),
            textureManager.getBackgroundTexture().getHeight()
        );

        // 初始化视口
        viewport = new FitViewport(config.worldWidth, config.worldHeight, camera);
        viewport.apply();
        camera.position.set(config.worldWidth / 2f, config.worldHeight / 2f, 0);
        camera.update();

        fontBig = new BitmapFont(Gdx.files.internal("font_title.fnt"), false);
        fontMed = new BitmapFont(Gdx.files.internal("font_label.fnt"), false);
        fontBig.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fontMed.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // 初始化绘制辅助
        drawHelper = new DrawHelper(fontBig, fontMed, config.worldHeight);

        // 初始化实体
        bird = new Bird(config, textureManager.getBirdRegion());
        gameOverDialog = new GameOverDialog(
            textureManager.getPanelRegion(),
            textureManager.getButtonRegion(),
            config
        );

        // 注册输入监听
        Gdx.input.setInputProcessor(inputListener);

        // 初始化状态
        state.distanceSinceSpawn = config.spawnInterval;
    }

    public void render() {
        float dt = Math.min(Gdx.graphics.getDeltaTime(), Constants.MAX_DELTA_TIME);

        handleInput();
        if (state.started && !state.gameOver) {
            updateWorld(dt);
        }
        draw();

        // 帧末重置输入状态，确保下一帧开始时代志已清空
        inputListener.resetFrameState();
    }

    private void handleInput() {
        boolean touched = inputListener.isJustTouched();
        boolean space = inputListener.isJustPressedSpace();
        if (!touched && !space) return;

        if (state.gameOver) {
            if (space) {
                restart();
                return;
            }
            if (touched && touchInButton()) {
                restart();
            }
            return;
        }

        if (!state.started) {
            if (space || (touched && touchInButton())) {
                state.started = true;
                bird.flap();
            }
            return;
        }

        bird.flap();
    }

    private boolean touchInButton() {
        tempWorldPos.set(inputListener.getTouchScreenX(), inputListener.getTouchScreenY());
        Vector2 worldPos = viewport.unproject(tempWorldPos);
        return gameOverDialog.containsButton(worldPos.x, worldPos.y);
    }

    private void updateWorld(float dt) {
        bird.update(dt);

        state.bgScrollX -= config.scrollSpeed * dt;
        if (state.bgScrollX <= -config.worldWidth) {
            state.bgScrollX += config.worldWidth;
        }

        // 更新柱子
        for (Pillar pillar : pillars) {
            pillar.update(dt);
        }

        // 移除超出屏幕的柱子，并回收到对象池
        for (int i = pillars.size - 1; i >= 0; i--) {
            Pillar p = pillars.get(i);
            if (p.isOffScreen()) {
                pillars.removeIndex(i);
                pillarPool.free(p);
            }
        }

        // 计分检测
        Rectangle birdBounds = bird.getBounds();
        for (Pillar pillar : pillars) {
            if (pillar.isTop() || pillar.isScored()) continue;
            if (pillar.getBounds().x + pillar.getBounds().width < birdBounds.x) {
                pillar.markScored();
                state.score++;
            }
        }

        // 生成新柱子
        state.distanceSinceSpawn += config.scrollSpeed * dt;
        if (state.distanceSinceSpawn >= config.spawnInterval) {
            state.distanceSinceSpawn -= config.spawnInterval;
            spawnPillarPair();
        }

        // 碰撞检测
        for (Pillar pillar : pillars) {
            if (birdBounds.overlaps(pillar.getBounds())) {
                finishGame();
                return;
            }
        }

        if (bird.isOnGround()) {
            finishGame();
        }
    }

    private void finishGame() {
        state.gameOver = true;
        if (state.score > highScore) {
            highScore = state.score;
            Gdx.app.getPreferences("flybird").putInteger("highscore", highScore).flush();
        }
    }

    private void spawnPillarPair() {
        float minCenter = config.gapSize / 2f + config.worldHeight * Constants.PILLAR_MIN_EDGE_FACTOR;
        float maxCenter = config.worldHeight - config.gapSize / 2f
            - config.worldHeight * Constants.PILLAR_MIN_EDGE_FACTOR;
        float center = minCenter + random.nextFloat() * (maxCenter - minCenter);
        float gapBottom = center - config.gapSize / 2f;
        float gapTop = center + config.gapSize / 2f;

        // 从池中获取底部柱子
        Pillar bottom = pillarPool.obtain();
        bottom.init(textureManager.getObstacleRegion(), config.scrollSpeed,
            config.worldWidth, 0f, config.pillarWidth, gapBottom, false);
        pillars.add(bottom);

        // 从池中获取顶部柱子
        Pillar top = pillarPool.obtain();
        top.init(textureManager.getObstacleRegion(), config.scrollSpeed,
            config.worldWidth, gapTop, config.pillarWidth, config.worldHeight - gapTop, true);
        pillars.add(top);
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.setColor(Color.WHITE);

        // 背景
        batch.draw(textureManager.getBackgroundTexture(), state.bgScrollX, 0);
        batch.draw(textureManager.getBackgroundTexture(),
            state.bgScrollX + config.worldWidth, 0);

        // 柱子
        for (Pillar pillar : pillars) {
            pillar.draw(batch);
        }

        if (state.started) {
            bird.draw(batch);
        }

        // UI
        drawUI();

        batch.end();
    }

    private void drawUI() {
        batch.setColor(Color.WHITE);

        float centerX = config.worldWidth / 2f;
        if (!state.started) {
            gameOverDialog.draw(batch);
            drawHelper.drawDarkBig(batch, "FLY BIRD", centerX, config.worldHeight * 0.71f);
            float birdWidth = config.worldWidth * 0.31f;
            float birdHeight = birdWidth * textureManager.getBirdRegion().getRegionHeight()
                / textureManager.getBirdRegion().getRegionWidth();
            batch.draw(textureManager.getBirdRegion(),
                centerX - birdWidth / 2f, config.worldHeight * 0.46f,
                birdWidth, birdHeight);
            drawHelper.drawMed(batch, "START", centerX, config.worldHeight * 0.362f, true);
        } else if (!state.gameOver) {
            drawHelper.drawBig(batch, String.valueOf(state.score),
                centerX, config.worldHeight * 0.86f, true);
        } else {
            gameOverDialog.draw(batch);
            drawHelper.drawDarkBig(batch, "GAME OVER", centerX, config.worldHeight * 0.71f);
            drawHelper.drawDarkMed(batch, "SCORE", centerX, config.worldHeight * 0.59f);
            drawHelper.drawDarkBig(batch, String.valueOf(state.score),
                centerX, config.worldHeight * 0.51f);
            drawHelper.drawMed(batch, "RESTART", centerX, config.worldHeight * 0.362f, true);
        }
        drawHelper.drawMed(batch, "BEST  " + highScore,
            centerX, config.worldHeight * 0.085f, true);
    }

    private void restart() {
        // 回收所有柱子
        for (Pillar p : pillars) {
            pillarPool.free(p);
        }
        pillars.clear();

        state.reset();
        state.distanceSinceSpawn = config.spawnInterval;
        bird.reset();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        camera.update();
    }

    public void dispose() {
        batch.dispose();
        fontBig.dispose();
        fontMed.dispose();
        textureManager.dispose();
    }
}
