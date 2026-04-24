package com.tty7.gl.ui.app;

import com.tty7.core.levels.Level;

public sealed interface AppCmd {
    record Submit(Level level, String source, long elapsedSeconds) implements AppCmd {
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
