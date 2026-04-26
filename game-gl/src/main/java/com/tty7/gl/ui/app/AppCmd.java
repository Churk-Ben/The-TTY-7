package com.tty7.gl.ui.app;

import com.tty7.core.levels.Level;
import com.tty7.core.save.SaveState;

public sealed interface AppCmd {
    record Submit(Level level, String source, long elapsedSeconds) implements AppCmd {
    }

    record PersistSave(SaveState saveState) implements AppCmd {
    }

    record PlaySound(String resourcePath) implements AppCmd {
    }

    record StartLoopingMusic(String resourcePath) implements AppCmd {
    }

    record StopLoopingMusic() implements AppCmd {
    }

    record Exit() implements AppCmd {
    }
}
