package com.tty7.gl.input.event;

public sealed interface InputEvent permits CharEvent, KeyEvent, PasteEvent, WheelEvent {
}
