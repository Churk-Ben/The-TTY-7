package com.tty7.gl.renderer.effect;

public interface UiEffectRenderer<T extends UiEffect> {
    Class<T> effectType();

    void render(T effect, UiEffectRenderContext context);
}
