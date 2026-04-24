package com.tty7.core.engine;

import java.util.List;

public record SubmissionResult(boolean accepted, ScoreResult score, List<String> trace, Object result, String message) {
}
