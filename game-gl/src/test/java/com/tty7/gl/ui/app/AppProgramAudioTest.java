package com.tty7.gl.ui.app;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.tty7.core.engine.BlockRegistry;
import com.tty7.core.levels.Level;
import com.tty7.gl.input.intent.InputIntent;
import com.tty7.gl.services.CompletionService;
import com.tty7.gl.ui.pages.DiagnosticsPage;
import com.tty7.gl.ui.pages.GamePage;
import com.tty7.gl.ui.pages.StartPage;
import com.tty7.gl.ui.tea.UpdateResult;

class AppProgramAudioTest {
        private static AppProgram createProgram(Level level) {
                return new AppProgram(
                                new StartPage(),
                                new GamePage(new CompletionService(new BlockRegistry())),
                                new DiagnosticsPage(),
                                List.of(level));
        }

        private static Level createLevel() {
                return new Level(
                                1,
                                1,
                                "demo",
                                "desc",
                                List.of(1),
                                List.of(1),
                                List.of("Array", "_INPUT_"),
                                List.of(),
                                List.of(),
                                1,
                                1,
                                1,
                                "");
        }

        @Test
        void enteringGameStopsTitleMusic() {
                Level level = createLevel();
                AppProgram program = createProgram(level);

                AppModel initial = program.init();
                UpdateResult<AppModel, AppCmd> result = program.update(initial,
                                new AppMsg.Intent(new InputIntent.Submit()));

                assertTrue(result.model().screen() == AppModel.Screen.GAME);
                assertTrue(result.commands().stream().anyMatch(AppCmd.StopLoopingMusic.class::isInstance));
                assertFalse(result.commands().stream().anyMatch(AppCmd.StartLoopingMusic.class::isInstance));
        }

        @Test
        void enteringDiagnosticsKeepsTitleMusicRunning() {
                Level level = createLevel();
                AppProgram program = createProgram(level);

                AppModel initial = program.init();
                UpdateResult<AppModel, AppCmd> result = program.update(
                                initial,
                                new AppMsg.Intent(new InputIntent.NavigateNext()));
                result = program.update(result.model(), new AppMsg.Intent(new InputIntent.Submit()));

                assertTrue(result.model().screen() == AppModel.Screen.DIAGNOSTICS);
                assertFalse(result.commands().stream().anyMatch(AppCmd.StopLoopingMusic.class::isInstance));
                assertFalse(result.commands().stream().anyMatch(AppCmd.StartLoopingMusic.class::isInstance));
        }

        @Test
        void returningToStartFromDiagnosticsDoesNotRestartMusic() {
                Level level = createLevel();
                AppProgram program = createProgram(level);

                AppModel diagnosticsModel = new AppModel(
                                AppModel.Screen.DIAGNOSTICS,
                                StartPage.Model.init(),
                                GamePage.Model.init(level, System.currentTimeMillis() / 1000),
                                DiagnosticsPage.Model.init(),
                                level);
                UpdateResult<AppModel, AppCmd> result = program.update(
                                diagnosticsModel,
                                new AppMsg.Intent(new InputIntent.Cancel()));

                assertTrue(result.model().screen() == AppModel.Screen.START);
                assertFalse(result.commands().stream().anyMatch(AppCmd.StartLoopingMusic.class::isInstance));
                assertFalse(result.commands().stream().anyMatch(AppCmd.StopLoopingMusic.class::isInstance));
        }

        @Test
        void returningToStartFromGameRestartsTitleMusic() {
                Level level = createLevel();
                AppProgram program = createProgram(level);

                AppModel gameModel = new AppModel(
                                AppModel.Screen.GAME,
                                StartPage.Model.init(),
                                GamePage.Model.init(level, System.currentTimeMillis() / 1000).withPause(true, 1),
                                DiagnosticsPage.Model.init(),
                                level);
                UpdateResult<AppModel, AppCmd> result = program.update(
                                gameModel,
                                new AppMsg.Intent(new InputIntent.Submit()));

                AppCmd loopingMusicCmd = result.commands().stream()
                                .filter(AppCmd.StartLoopingMusic.class::isInstance)
                                .findFirst()
                                .orElseThrow();
                AppCmd.StartLoopingMusic typedCmd = assertInstanceOf(AppCmd.StartLoopingMusic.class, loopingMusicCmd);
                assertTrue(result.model().screen() == AppModel.Screen.START);
                assertTrue(AppCmdHandler.TITLE_BGM_FIRST_MEET.equals(typedCmd.resourcePath()));
        }
}
