package com.tty7.core.engine;

import com.tty7.api.Block;
import com.tty7.core.levels.Level;

public class Scorer {
    public ScoreResult score(boolean correct, Block<?> root, long elapsedSeconds, Level level) {
        boolean minimal = root.nodeCount() <= level.optimalSize();
        boolean speed = elapsedSeconds <= level.timePar();
        int stars = (correct ? 1 : 0) + (minimal ? 1 : 0) + (speed ? 1 : 0);
        return new ScoreResult(correct, minimal, speed, stars);
    }
}
