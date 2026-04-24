package com.tty7.gl.ui.app;

import com.tty7.core.engine.SubmissionResult;
import com.tty7.gl.input.intent.InputIntent;

public sealed interface AppMsg {
    record Intent(InputIntent intent) implements AppMsg {
    }

    record SubmitFinished(SubmissionResult result) implements AppMsg {
    }
}
