package com.tty7.gl.ui.app;

import com.tty7.core.levels.Level;
import com.tty7.core.save.SaveState;
import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.ui.pages.BootPage;
import com.tty7.gl.ui.pages.ConsolePage;
import com.tty7.gl.ui.pages.DiagnosticsPage;
import com.tty7.gl.ui.pages.EndingPage;
import com.tty7.gl.ui.pages.GamePage;
import com.tty7.gl.ui.pages.StartPage;
import com.tty7.gl.ui.pages.StoryPage;
import com.tty7.gl.ui.tea.Program;
import com.tty7.gl.ui.tea.UpdateResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppProgram implements Program<AppModel, AppMsg, AppCmd> {
    private final BootPage bootPage;
    private final StartPage startPage;
    private final GamePage gamePage;
    private final ConsolePage consolePage;
    private final StoryPage storyPage;
    private final EndingPage endingPage;
    private final DiagnosticsPage diagnosticsPage;
    private final Level initialLevel;
    private final Map<Integer, Level> levelById;
    private final SaveState initialSaveState;

    private static final String SFX_ACCEPT = "/assets/audio/sfx/accept.mp3";
    private static final String SFX_WRONG_ANSWER = "/assets/audio/sfx/wrong-answer.mp3";

    public AppProgram(BootPage bootPage, StartPage startPage, GamePage gamePage, ConsolePage consolePage,
            StoryPage storyPage, EndingPage endingPage, DiagnosticsPage diagnosticsPage, List<Level> levels,
            SaveState initialSaveState) {
        if (levels == null || levels.isEmpty()) {
            throw new IllegalArgumentException("levels must not be empty");
        }
        this.bootPage = bootPage;
        this.startPage = startPage;
        this.gamePage = gamePage;
        this.consolePage = consolePage;
        this.storyPage = storyPage;
        this.endingPage = endingPage;
        this.diagnosticsPage = diagnosticsPage;
        this.initialLevel = levels.stream().min((a, b) -> Integer.compare(a.id(), b.id())).orElseThrow();
        this.levelById = new HashMap<>();
        for (Level level : levels) {
            this.levelById.put(level.id(), level);
        }
        this.initialSaveState = initialSaveState;
    }

    @Override
    public AppModel init() {
        return AppModel.init(initialLevel, System.currentTimeMillis() / 1000, initialSaveState, consolePage.init());
    }

    @Override
    public UpdateResult<AppModel, AppCmd> update(AppModel model, AppMsg msg) {
        if (model.screen() == AppModel.Screen.BOOT) {
            BootPage.Msg bootMsg = null;
            if (msg instanceof AppMsg.Intent im) {
                bootMsg = new BootPage.Msg.Intent(im.intent());
            } else if (msg instanceof AppMsg.Tick tick) {
                bootMsg = new BootPage.Msg.Tick(tick.nowMillis());
            }

            if (bootMsg != null) {
                UpdateResult<BootPage.Model, BootPage.Cmd> result = bootPage.update(model.bootModel(), bootMsg);
                List<AppCmd> commands = new ArrayList<>();
                AppModel.Screen nextScreen = model.screen();

                if (result.commands() != null) {
                    for (BootPage.Cmd cmd : result.commands()) {
                        if (cmd instanceof BootPage.Cmd.OpenStart) {
                            nextScreen = AppModel.Screen.START;
                        } else if (cmd instanceof BootPage.Cmd.OpenDiagnostics) {
                            nextScreen = AppModel.Screen.DIAGNOSTICS;
                        } else if (cmd instanceof BootPage.Cmd.Exit) {
                            commands.add(new AppCmd.Exit());
                        } else if (cmd instanceof BootPage.Cmd.PlaySound playSound) {
                            commands.add(new AppCmd.PlaySound(playSound.resourcePath()));
                        }
                    }
                }

                appendScreenTransitionAudio(model.screen(), nextScreen, commands);
                AppModel nextModel = new AppModel(nextScreen, result.model(), StartPage.Model.init(), model.gameModel(),
                        model.consoleModel(), model.storyPageModel(), model.endingPageModel(), model.diagnosticsModel(),
                        model.currentLevel(), model.saveState(), model.storyState());
                return new UpdateResult<>(nextModel, commands);
            }
            return new UpdateResult<>(model, List.of());
        }

        if (model.screen() == AppModel.Screen.START) {
            StartPage.Msg startMsg = null;
            if (msg instanceof AppMsg.Intent im) {
                startMsg = new StartPage.Msg.Intent(im.intent());
            }

            if (startMsg != null) {
                UpdateResult<StartPage.Model, StartPage.Cmd> result = startPage.update(model.startModel(), startMsg);
                List<AppCmd> commands = new ArrayList<>();
                AppModel.Screen nextScreen = model.screen();
                GamePage.Model nextGameModel = model.gameModel();
                ConsolePage.Model nextConsoleModel = model.consoleModel();
                BootPage.Model nextBootModel = model.bootModel();
                StartPage.Model nextStartModel = result.model();

                if (result.commands() != null) {
                    for (StartPage.Cmd cmd : result.commands()) {
                        if (cmd instanceof StartPage.Cmd.Login) {
                            nextScreen = AppModel.Screen.CONSOLE;
                            nextConsoleModel = consolePage.newSessionModel(model.saveState());
                        } else if (cmd instanceof StartPage.Cmd.Reboot) {
                            nextScreen = AppModel.Screen.BOOT;
                            nextBootModel = BootPage.Model.startReboot(model.saveState());
                            nextStartModel = StartPage.Model.init();
                        } else if (cmd instanceof StartPage.Cmd.PowerOff) {
                            nextScreen = AppModel.Screen.BOOT;
                            nextBootModel = BootPage.Model.startPowerOff(model.saveState());
                            nextStartModel = StartPage.Model.init();
                        } else if (cmd instanceof StartPage.Cmd.PlaySound playSound) {
                            commands.add(new AppCmd.PlaySound(playSound.resourcePath()));
                        }
                    }
                }
                appendScreenTransitionAudio(model.screen(), nextScreen, commands);
                AppModel nextModel = new AppModel(nextScreen, nextBootModel, nextStartModel, nextGameModel,
                        nextConsoleModel, model.storyPageModel(), model.endingPageModel(), model.diagnosticsModel(),
                        model.currentLevel(), model.saveState(), model.storyState());
                return new UpdateResult<>(nextModel, commands);
            }
            return new UpdateResult<>(model, List.of());
        }

        if (model.screen() == AppModel.Screen.CONSOLE) {
            ConsolePage.Msg consoleMsg = null;
            if (msg instanceof AppMsg.Intent im) {
                consoleMsg = new ConsolePage.Msg.Intent(im.intent());
            } else if (msg instanceof AppMsg.Tick tick) {
                consoleMsg = new ConsolePage.Msg.Tick(tick.nowMillis());
            }

            if (consoleMsg != null) {
                UpdateResult<ConsolePage.Model, ConsolePage.Cmd> result = consolePage.update(model.consoleModel(),
                        consoleMsg);
                List<AppCmd> commands = new ArrayList<>();
                AppModel.Screen nextScreen = model.screen();
                StartPage.Model nextStartModel = model.startModel();
                GamePage.Model nextGameModel = model.gameModel();
                SaveState nextSaveState = model.saveState();
                Level nextCurrentLevel = model.currentLevel();
                EndingPage.Model nextEndingModel = model.endingPageModel();

                if (result.commands() != null) {
                    for (ConsolePage.Cmd cmd : result.commands()) {
                        if (cmd instanceof ConsolePage.Cmd.ReturnToLogin) {
                            nextScreen = AppModel.Screen.START;
                            nextStartModel = StartPage.Model.init();
                        } else if (cmd instanceof ConsolePage.Cmd.PlaySound playSound) {
                            commands.add(new AppCmd.PlaySound(playSound.resourcePath()));
                        } else if (cmd instanceof ConsolePage.Cmd.OpenGame) {
                            nextScreen = AppModel.Screen.GAME;
                            int startLevel = model.saveState().loop() == 1 ? 1 : 6;
                            int endLevel = model.saveState().loop() == 1 ? 5 : 10;
                            Level targetLevel = levelById.get(startLevel);
                            for (int i = startLevel; i <= endLevel; i++) {
                                if (!model.saveState().hasSolvedLevel(i)) {
                                    targetLevel = levelById.get(i);
                                    break;
                                }
                            }
                            if (targetLevel != null) {
                                nextCurrentLevel = targetLevel;
                                nextGameModel = GamePage.Model.init(targetLevel, System.currentTimeMillis() / 1000);
                            }
                        } else if (cmd instanceof ConsolePage.Cmd.RecordProgress rp) {
                            nextSaveState = nextSaveState.withFlags(rp.flags());
                            nextSaveState = nextSaveState.withSeenNodes(rp.seenNodes());
                            commands.add(new AppCmd.PersistSave(nextSaveState));
                        } else if (cmd instanceof ConsolePage.Cmd.OpenEnding oe) {
                            nextScreen = AppModel.Screen.ENDING;
                            nextEndingModel = endingPage.start(oe.endingId());
                        }
                    }
                }

                appendScreenTransitionAudio(model.screen(), nextScreen, commands);
                AppModel nextModel = new AppModel(nextScreen, model.bootModel(), nextStartModel, nextGameModel,
                        result.model(), model.storyPageModel(), nextEndingModel, model.diagnosticsModel(),
                        nextCurrentLevel, nextSaveState, model.storyState());
                return new UpdateResult<>(nextModel, commands);
            }
            return new UpdateResult<>(model, List.of());
        }

        if (model.screen() == AppModel.Screen.GAME) {
            GamePage.Msg gameMsg = null;
            if (msg instanceof AppMsg.Intent im) {
                gameMsg = new GamePage.Msg.Intent(im.intent());
            } else if (msg instanceof AppMsg.SubmitFinished sf) {
                gameMsg = new GamePage.Msg.SubmitFinished(sf.result());
            }

            if (gameMsg != null) {
                UpdateResult<GamePage.Model, GamePage.Cmd> result = gamePage.update(model.gameModel(), gameMsg);
                List<AppCmd> commands = new ArrayList<>();
                AppModel.Screen nextScreen = model.screen();
                GamePage.Model nextGameModel = result.model();
                Level nextCurrentLevel = model.currentLevel();
                SaveState nextSaveState = model.saveState();
                ConsolePage.Model nextConsoleModel = model.consoleModel();

                if (msg instanceof AppMsg.SubmitFinished sf) {
                    if (sf.result().accepted()) {
                        commands.add(new AppCmd.PlaySound(SFX_ACCEPT));
                        nextSaveState = nextSaveState.withSolvedExpression(sf.level().id(), sf.source());
                        nextSaveState = nextSaveState
                                .withFlag("SOLVED_LEVEL_" + String.format("%02d", sf.level().id()));
                        commands.add(new AppCmd.PersistSave(nextSaveState));

                        Level nextLevel = levelById.get(model.currentLevel().id() + 1);
                        int currentLevelId = model.currentLevel().id();
                        int maxLevelForLoop = model.saveState().loop() == 1 ? 5 : 10;
                        
                        if (currentLevelId < maxLevelForLoop && nextLevel != null) {
                            nextCurrentLevel = nextLevel;
                            nextGameModel = GamePage.Model.init(nextLevel, System.currentTimeMillis() / 1000);
                        } else {
                            nextScreen = AppModel.Screen.CONSOLE;
                            nextConsoleModel = consolePage.resumeAfterBlocks(model.consoleModel(), nextSaveState);
                        }
                    } else {
                        commands.add(new AppCmd.PlaySound(SFX_WRONG_ANSWER));
                    }
                }

                if (result.commands() != null) {
                    for (GamePage.Cmd cmd : result.commands()) {
                        if (cmd instanceof GamePage.Cmd.Submit submit) {
                            commands.add(new AppCmd.Submit(submit.level(), submit.source(), submit.elapsedSeconds()));
                        } else if (cmd instanceof GamePage.Cmd.PlaySound playSound) {
                            commands.add(new AppCmd.PlaySound(playSound.resourcePath()));
                        } else if (cmd instanceof GamePage.Cmd.ReturnToStart) {
                            nextScreen = AppModel.Screen.CONSOLE;
                            nextConsoleModel = consolePage.resumeAfterBlocks(model.consoleModel(), nextSaveState);
                        }
                    }
                }
                appendScreenTransitionAudio(model.screen(), nextScreen, commands);
                AppModel nextModel = new AppModel(nextScreen, model.bootModel(), model.startModel(), nextGameModel,
                        nextConsoleModel, model.storyPageModel(), model.endingPageModel(), model.diagnosticsModel(),
                        nextCurrentLevel, nextSaveState, model.storyState());
                return new UpdateResult<>(nextModel, commands);
            }
            return new UpdateResult<>(model, List.of());
        }

        if (model.screen() == AppModel.Screen.ENDING) {
            EndingPage.Msg endingMsg = null;
            if (msg instanceof AppMsg.Intent im) {
                endingMsg = new EndingPage.Msg.Intent(im.intent());
            }

            if (endingMsg != null) {
                UpdateResult<EndingPage.Model, EndingPage.Cmd> result = endingPage.update(model.endingPageModel(),
                        endingMsg);
                List<AppCmd> commands = new ArrayList<>();
                AppModel.Screen nextScreen = model.screen();
                StartPage.Model nextStartModel = model.startModel();

                if (result.commands() != null) {
                    for (EndingPage.Cmd cmd : result.commands()) {
                        if (cmd instanceof EndingPage.Cmd.ReturnToLogin) {
                            nextScreen = AppModel.Screen.START;
                            nextStartModel = StartPage.Model.init();
                        } else if (cmd instanceof EndingPage.Cmd.PlaySound playSound) {
                            commands.add(new AppCmd.PlaySound(playSound.resourcePath()));
                        }
                    }
                }
                appendScreenTransitionAudio(model.screen(), nextScreen, commands);
                AppModel nextModel = new AppModel(nextScreen, model.bootModel(), nextStartModel, model.gameModel(),
                        model.consoleModel(), model.storyPageModel(), result.model(), model.diagnosticsModel(),
                        model.currentLevel(), model.saveState(), model.storyState());
                return new UpdateResult<>(nextModel, commands);
            }
            return new UpdateResult<>(model, List.of());
        }

        if (model.screen() == AppModel.Screen.DIAGNOSTICS) {
            DiagnosticsPage.Msg diagMsg = null;
            if (msg instanceof AppMsg.Intent im) {
                diagMsg = new DiagnosticsPage.Msg.Intent(im.intent());
            }

            if (diagMsg != null) {
                UpdateResult<DiagnosticsPage.Model, DiagnosticsPage.Cmd> result = diagnosticsPage
                        .update(model.diagnosticsModel(), diagMsg);
                List<AppCmd> commands = new ArrayList<>();
                AppModel.Screen nextScreen = model.screen();

                if (result.commands() != null) {
                    for (DiagnosticsPage.Cmd cmd : result.commands()) {
                        if (cmd instanceof DiagnosticsPage.Cmd.ReturnToBoot) {
                            nextScreen = AppModel.Screen.BOOT;
                        } else if (cmd instanceof DiagnosticsPage.Cmd.PlaySound playSound) {
                            commands.add(new AppCmd.PlaySound(playSound.resourcePath()));
                        }
                    }
                }
                appendScreenTransitionAudio(model.screen(), nextScreen, commands);
                BootPage.Model nextBootModel = nextScreen == AppModel.Screen.BOOT ? BootPage.Model.returnToGrub(model.saveState())
                        : model.bootModel();
                AppModel nextModel = new AppModel(nextScreen, nextBootModel, model.startModel(), model.gameModel(),
                        model.consoleModel(), model.storyPageModel(), model.endingPageModel(), result.model(),
                        model.currentLevel(), model.saveState(), model.storyState());
                return new UpdateResult<>(nextModel, commands);
            }
            return new UpdateResult<>(model, List.of());
        }

        return new UpdateResult<>(model, List.of());
    }

    @Override
    public RenderFrame view(AppModel model, TerminalBuffer buffer, long nowMillis) {
        buffer.clear();
        if (model.screen() == AppModel.Screen.BOOT) {
            return bootPage.view(model.bootModel(), buffer, nowMillis);
        } else if (model.screen() == AppModel.Screen.START) {
            return startPage.view(model.startModel(), buffer, nowMillis);
        } else if (model.screen() == AppModel.Screen.CONSOLE) {
            return consolePage.view(model.consoleModel(), buffer, nowMillis);
        } else if (model.screen() == AppModel.Screen.GAME) {
            return gamePage.view(model.gameModel(), buffer, nowMillis);
        } else if (model.screen() == AppModel.Screen.STORY) {
            return storyPage.view(model.storyPageModel(), buffer, nowMillis);
        } else if (model.screen() == AppModel.Screen.ENDING) {
            return endingPage.view(model.endingPageModel(), buffer, nowMillis);
        } else {
            return diagnosticsPage.view(model.diagnosticsModel(), buffer, nowMillis);
        }
    }

    private void appendScreenTransitionAudio(AppModel.Screen currentScreen, AppModel.Screen nextScreen,
            List<AppCmd> commands) {
        boolean currentHasLoopingMusic = shouldPlayLoopingMusic(currentScreen);
        boolean nextHasLoopingMusic = shouldPlayLoopingMusic(nextScreen);

        if (currentHasLoopingMusic && !nextHasLoopingMusic) {
            commands.add(new AppCmd.StopLoopingMusic());
        } else if (!currentHasLoopingMusic && nextHasLoopingMusic) {
            commands.add(new AppCmd.StartLoopingMusic(AppCmdHandler.TITLE_BGM_FIRST_MEET));
        }
    }

    private boolean shouldPlayLoopingMusic(AppModel.Screen screen) {
        return screen == AppModel.Screen.BOOT || screen == AppModel.Screen.START
                || screen == AppModel.Screen.DIAGNOSTICS;
    }
}