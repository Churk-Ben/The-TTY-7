package com.tty7.gl.renderer.effect;

import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.text.TextRenderer;

public record UiEffectRenderContext(
                RenderFrame frame,
                TextRenderer textRenderer,
                int viewportWidth,
                int viewportHeight,
                double timeSeconds) {
}
