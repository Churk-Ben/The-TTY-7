package com.tty7.gl.ui.app;

import com.tty7.core.levels.Level;
import com.tty7.core.save.SaveState;
import com.tty7.core.story.StoryState;
import com.tty7.gl.ui.pages.BootPage;
import com.tty7.gl.ui.pages.ConsolePage;
import com.tty7.gl.ui.pages.DiagnosticsPage;
import com.tty7.gl.ui.pages.GamePage;
import com.tty7.gl.ui.pages.StartPage;
import com.tty7.gl.ui.pages.StoryPage;
import com.tty7.gl.ui.pages.EndingPage;

public record AppModel(
        Screen screen,
        BootPage.Model bootModel,
        StartPage.Model startModel,
        GamePage.Model gameModel,
        ConsolePage.Model consoleModel,
        StoryPage.Model storyPageModel,
        EndingPage.Model endingPageModel,
        DiagnosticsPage.Model diagnosticsModel,
        Level currentLevel,
        SaveState saveState,
        StoryState storyState) {

    public enum Screen {
        BOOT, START, GAME, CONSOLE, STORY, ENDING, DIAGNOSTICS
    }

    public static AppModel init(Level level, long startEpochSeconds, SaveState initialSaveState,
            ConsolePage.Model consoleModel) {
        return new AppModel(
                Screen.BOOT,
                BootPage.Model.init(initialSaveState),
                StartPage.Model.init(),
                GamePage.Model.init(level, startEpochSeconds),
                consoleModel,
                StoryPage.Model.init(),
                EndingPage.Model.init(),
                DiagnosticsPage.Model.init(),
                level,
                initialSaveState,
                StoryState.init());
    }
}
