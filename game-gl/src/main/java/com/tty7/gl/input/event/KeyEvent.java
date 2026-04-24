package com.tty7.gl.input.event;

import com.tty7.gl.input.InputKey;

public record KeyEvent(InputKey key) implements InputEvent {
}
