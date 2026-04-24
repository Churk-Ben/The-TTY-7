package com.tty7.gl.ui.app;

import com.tty7.core.levels.Level;
import com.tty7.gl.ui.pages.GamePage;
import com.tty7.gl.ui.pages.StartPage;
import com.tty7.gl.ui.pages.diagnostics.DiagnosticsPage;

public record AppModel(
        Screen screen,
        StartPage.Model startModel,
        GamePage.Model gameModel,
        DiagnosticsPage.Model diagnosticsModel,
        Level currentLevel) {

    public enum Screen {
        START, GAME, DIAGNOSTICS
    }

    public static AppModel init(Level level, long startEpochSeconds) {
        return new AppModel(
                Screen.START,
                StartPage.Model.init(),
                GamePage.Model.init(level, startEpochSeconds),
                DiagnosticsPage.Model.init(),
                level);
    }
}
