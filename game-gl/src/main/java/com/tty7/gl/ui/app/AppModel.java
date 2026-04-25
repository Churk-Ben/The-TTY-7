package com.tty7.gl.ui.app;

import com.tty7.core.levels.Level;
import com.tty7.gl.ui.pages.BootPage;
import com.tty7.gl.ui.pages.DiagnosticsPage;
import com.tty7.gl.ui.pages.GamePage;
import com.tty7.gl.ui.pages.StartPage;

public record AppModel(
        Screen screen,
        BootPage.Model bootModel,
        StartPage.Model startModel,
        GamePage.Model gameModel,
        DiagnosticsPage.Model diagnosticsModel,
        Level currentLevel) {

    public enum Screen {
        BOOT, START, GAME, DIAGNOSTICS
    }

    public static AppModel init(Level level, long startEpochSeconds) {
        return new AppModel(
                Screen.BOOT,
                BootPage.Model.init(),
                StartPage.Model.init(),
                GamePage.Model.init(level, startEpochSeconds),
                DiagnosticsPage.Model.init(),
                level);
    }
}
