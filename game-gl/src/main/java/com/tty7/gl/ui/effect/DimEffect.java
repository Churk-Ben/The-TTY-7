package com.tty7.gl.ui.effect;

import static org.lwjgl.opengl.GL11.*;

import com.tty7.gl.renderer.effect.*;
import com.tty7.gl.renderer.text.TextRenderer;

public class DimEffect implements UiEffectRenderer<UiEffect.Dim> {
    private static final float FADE_SPEED = 5.0f; // opacity units per second

    private float currentOpacity = 0f;
    private UiEffect.Dim lastDimEffect = null;
    private double lastTimeSeconds = -1;

    @Override
    public Class<UiEffect.Dim> effectType() {
        return UiEffect.Dim.class;
    }

    @Override
    public void render(UiEffect.Dim dimEffect, UiEffectRenderContext context) {
        double now = context.timeSeconds();
        if (lastTimeSeconds < 0) {
            lastTimeSeconds = now;
        }
        float dt = (float) (now - lastTimeSeconds);
        lastTimeSeconds = now;

        float targetOpacity = 0f;
        if (dimEffect != null && dimEffect.targetOpacity() > 0f) {
            targetOpacity = dimEffect.targetOpacity();
            lastDimEffect = dimEffect;
        }

        if (currentOpacity < targetOpacity) {
            currentOpacity = Math.min(targetOpacity, currentOpacity + FADE_SPEED * dt);
        } else if (currentOpacity > targetOpacity) {
            currentOpacity = Math.max(targetOpacity, currentOpacity - FADE_SPEED * dt);
        }

        if (currentOpacity <= 0.01f) {
            lastDimEffect = null;
            return;
        }

        UiEffect.Dim activeDim = (dimEffect != null && dimEffect.targetOpacity() > 0f) ? dimEffect : lastDimEffect;
        if (activeDim == null) {
            return;
        }

        TextRenderer textRenderer = context.textRenderer();
        int viewportWidth = context.viewportWidth();
        int viewportHeight = context.viewportHeight();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_QUADS);
        glColor4f(0f, 0f, 0f, currentOpacity);

        float cellWidth = textRenderer.cellWidthPx();
        var frame = context.frame();
        float cellHeight = textRenderer.cellHeightPx();
        int cols = frame.textBuffer() != null ? frame.textBuffer().cols() : 0;
        int rows = frame.textBuffer() != null ? frame.textBuffer().rows() : 0;
        float marginX = textRenderer.gridOffsetXPx(cols);
        float marginY = textRenderer.gridOffsetYPx(rows);

        // Expand exclude area by 1 cell on all sides to cover the panel borders.
        // This makes sure the bright borders of the overlay panel don't have dimming
        // overlapping them.
        int effectiveX = activeDim.excludeX() > 0 ? activeDim.excludeX() - 1 : 0;
        int effectiveY = activeDim.excludeY() > 0 ? activeDim.excludeY() - 1 : 0;
        int effectiveW = activeDim.excludeWidth() > 0 ? activeDim.excludeWidth() + 2 : 0;
        int effectiveH = activeDim.excludeHeight() > 0 ? activeDim.excludeHeight() + 2 : 0;

        if (effectiveW > 0 && effectiveH > 0) {
            float exX = marginX + effectiveX * cellWidth;
            float exY = marginY + effectiveY * cellHeight;
            float exW = effectiveW * cellWidth;
            float exH = effectiveH * cellHeight;

            glVertex2f(0f, 0f);
            glVertex2f(viewportWidth, 0f);
            glVertex2f(viewportWidth, exY);
            glVertex2f(0f, exY);

            glVertex2f(0f, exY + exH);
            glVertex2f(viewportWidth, exY + exH);
            glVertex2f(viewportWidth, viewportHeight);
            glVertex2f(0f, viewportHeight);

            glVertex2f(0f, exY);
            glVertex2f(exX, exY);
            glVertex2f(exX, exY + exH);
            glVertex2f(0f, exY + exH);

            glVertex2f(exX + exW, exY);
            glVertex2f(viewportWidth, exY);
            glVertex2f(viewportWidth, exY + exH);
            glVertex2f(exX + exW, exY + exH);
        } else {
            glVertex2f(0f, 0f);
            glVertex2f(viewportWidth, 0f);
            glVertex2f(viewportWidth, viewportHeight);
            glVertex2f(0f, viewportHeight);
        }

        glEnd();
        glDisable(GL_BLEND);
    }
}
