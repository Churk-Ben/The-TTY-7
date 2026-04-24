package com.tty7.gl.renderer.core;

import java.util.List;

import com.tty7.gl.renderer.cursor.CursorState;
import com.tty7.gl.renderer.effect.UiEffect;

public record RenderFrame(TerminalBuffer textBuffer, CursorState cursorState, List<UiEffect> effects) {
}
