package com.farm.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.farm.FlybirdGame;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static final float WINDOW_SCALE = 0.3f; // 窗口相对背景图的缩放系数，开发者可修改

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new FlybirdGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("FlyBird");

        // 读取背景图尺寸
        int[] bgSize = getBackgroundImageSize("background.png");
        int bgWidth = bgSize[0];
        int bgHeight = bgSize[1];
        int windowWidth = Math.round(bgWidth * WINDOW_SCALE);
        int windowHeight = Math.round(bgHeight * WINDOW_SCALE);

        // 限制窗口不超过屏幕的 90%，防止霸屏
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float maxWidth = (float) (screenSize.width * 0.9);
        float maxHeight = (float) (screenSize.height * 0.9);
        float scale = Math.min(maxWidth / windowWidth, maxHeight / windowHeight);
        if (scale < 1f) {
            windowWidth = Math.round(windowWidth * scale);
            windowHeight = Math.round(windowHeight * scale);
        }

        configuration.setWindowedMode(windowWidth, windowHeight);
        configuration.setResizable(false); // 禁止拉伸

        // 其他配置
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }

    /**
     * 从 classpath 中读取指定图片的宽度和高度。
     * 如果读取失败，返回默认 800x600。
     */
    private static int[] getBackgroundImageSize(String resourcePath) {
        // 注意：资源路径前加 "/" 表示从 classpath 根目录查找
        try (InputStream is = Lwjgl3Launcher.class.getResourceAsStream("/" + resourcePath)) {
            if (is != null) {
                BufferedImage image = ImageIO.read(is);
                if (image != null) {
                    return new int[]{image.getWidth(), image.getHeight()};
                }
            }
        } catch (IOException e) {
            System.err.println("无法读取背景图 " + resourcePath + "，使用默认尺寸 800x600");
        }
        return new int[]{800, 600}; // fallback
    }
}
