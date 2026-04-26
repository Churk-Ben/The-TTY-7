package com.tty7.gl.ui.app;

import com.tty7.core.engine.GameCoreService;
import com.tty7.core.engine.SubmissionResult;
import com.tty7.core.save.SaveStateRepository;
import com.tty7.gl.ui.tea.CmdHandler;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import javazoom.jl.player.Player;

public class AppCmdHandler implements CmdHandler<AppCmd, AppMsg> {
    public static final String TITLE_BGM_FIRST_MEET = "/assets/audio/bgm/joelfazhari-dark-mysterious-true-crime-music-loopable-235870.mp3";
    public static final String TITLE_BGM_BACK_AGAIN = "/assets/audio/bgm/nickpanekaiassets-stealth-breacher-tense-title-screen-218087.mp3";
    private final GameCoreService service;
    private final SaveStateRepository saveStateRepository;
    private final ExecutorService commandExecutor;
    private final ExecutorService audioExecutor;
    private final ExecutorService musicExecutor;
    private final AtomicLong musicSession = new AtomicLong();
    private final Object musicLock = new Object();

    private volatile String loopingMusicResourcePath;
    private volatile Player currentMusicPlayer;
    private volatile Future<?> loopingMusicTask;

    public AppCmdHandler(GameCoreService service, SaveStateRepository saveStateRepository) {
        this.service = service;
        this.saveStateRepository = saveStateRepository;
        this.commandExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "ui-command-executor");
            t.setDaemon(true);
            return t;
        });
        this.audioExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "ui-audio-executor");
            t.setDaemon(true);
            return t;
        });
        this.musicExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "ui-music-executor");
            t.setDaemon(true);
            return t;
        });

        // 预热音频系统，防止首次播放音频时因懒加载(如枚举音频设备)导致的卡顿
        this.audioExecutor.submit(() -> {
            try {
                javax.sound.sampled.AudioSystem.getMixerInfo();
                Class.forName("javazoom.jl.player.Player");
                // 尝试加载一个短音效并实例化 Player 以触发内部的类加载和 JavaSound 初始化
                try (InputStream is = getClass().getResourceAsStream("/assets/audio/sfx/interact.mp3")) {
                    if (is != null) {
                        Player dummy = new Player(is);
                        dummy.close();
                    }
                }
            } catch (Throwable t) {
                // ignore
            }
        });

        startLoopingMusic(TITLE_BGM_FIRST_MEET);
    }

    @Override
    public void handle(AppCmd cmd, Consumer<AppMsg> dispatch) {
        if (cmd instanceof AppCmd.Submit submit) {
            commandExecutor.submit(() -> {
                SubmissionResult result = service.submit(submit.level(), submit.source(), submit.elapsedSeconds());
                dispatch.accept(new AppMsg.SubmitFinished(submit.level(), submit.source(), result));
            });
        } else if (cmd instanceof AppCmd.PersistSave persistSave) {
            commandExecutor.submit(() -> {
                saveStateRepository.save(persistSave.saveState());
            });
        } else if (cmd instanceof AppCmd.Exit) {
            System.exit(0);
        } else if (cmd instanceof AppCmd.PlaySound playSound) {
            audioExecutor.submit(() -> {
                try (InputStream is = getClass().getResourceAsStream(playSound.resourcePath())) {
                    if (is != null) {
                        Player player = new Player(is);
                        player.play();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to play sound: " + playSound.resourcePath());
                    e.printStackTrace();
                }
            });
        } else if (cmd instanceof AppCmd.StartLoopingMusic startLoopingMusic) {
            startLoopingMusic(startLoopingMusic.resourcePath());
        } else if (cmd instanceof AppCmd.StopLoopingMusic) {
            stopLoopingMusic();
        }
    }

    @Override
    public void close() {
        stopLoopingMusic();
        commandExecutor.shutdownNow();
        audioExecutor.shutdownNow();
        musicExecutor.shutdownNow();
    }

    private void startLoopingMusic(String resourcePath) {
        synchronized (musicLock) {
            if (resourcePath.equals(loopingMusicResourcePath)
                    && loopingMusicTask != null
                    && !loopingMusicTask.isDone()) {
                return;
            }
            stopLoopingMusicLocked();
            loopingMusicResourcePath = resourcePath;
            long session = musicSession.incrementAndGet();
            loopingMusicTask = musicExecutor.submit(() -> playLoopingMusic(resourcePath, session));
        }
    }

    private void stopLoopingMusic() {
        synchronized (musicLock) {
            stopLoopingMusicLocked();
        }
    }

    private void stopLoopingMusicLocked() {
        loopingMusicResourcePath = null;
        musicSession.incrementAndGet();
        Future<?> task = loopingMusicTask;
        loopingMusicTask = null;
        Player player = currentMusicPlayer;
        currentMusicPlayer = null;
        if (task != null) {
            task.cancel(false);
        }
        if (player != null) {
            player.close();
        }
    }

    private void playLoopingMusic(String resourcePath, long session) {
        while (session == musicSession.get() && resourcePath.equals(loopingMusicResourcePath)) {
            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    System.err.println("Failed to load looping music: " + resourcePath);
                    clearLoopingMusicOnFailure(session, resourcePath);
                    return;
                }

                Player player = new Player(is);
                currentMusicPlayer = player;
                try {
                    player.play();
                } finally {
                    if (currentMusicPlayer == player) {
                        currentMusicPlayer = null;
                    }
                    player.close();
                }
            } catch (Exception e) {
                System.err.println("Failed to play looping music: " + resourcePath);
                e.printStackTrace();
                clearLoopingMusicOnFailure(session, resourcePath);
                return;
            }
        }
    }

    private void clearLoopingMusicOnFailure(long session, String resourcePath) {
        synchronized (musicLock) {
            if (session == musicSession.get() && resourcePath.equals(loopingMusicResourcePath)) {
                loopingMusicResourcePath = null;
                loopingMusicTask = null;
                currentMusicPlayer = null;
            }
        }
    }
}
