package com.tty7.gl.ui.components;

import java.util.List;

import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.effect.UiEffect;
import com.tty7.gl.ui.tea.UpdateResult;
import com.tty7.gl.utils.TextUtil;

public final class ChoicePanelComponent {
    private ChoicePanelComponent() {
    }

    public record Model(
            boolean active,
            List<String> options,
            int selectedIndex) {
        public static Model init() {
            return new Model(false, List.of(), 0);
        }
    }

    public sealed interface Msg {
        record Show(List<String> options) implements Msg {
        }

        record Hide() implements Msg {
        }

        record Next() implements Msg {
        }

        record Prev() implements Msg {
        }

        record Select() implements Msg {
        }
    }

    public static UpdateResult<Model, Void> update(Model model, Msg msg) {
        if (msg instanceof Msg.Show(var options)) {
            return new UpdateResult<>(new Model(true, options, 0), null);
        }
        if (msg instanceof Msg.Hide) {
            return new UpdateResult<>(Model.init(), null);
        }
        if (!model.active()) {
            return new UpdateResult<>(model, null);
        }
        if (msg instanceof Msg.Next) {
            int next = (model.selectedIndex() + 1) % Math.max(1, model.options().size());
            return new UpdateResult<>(new Model(true, model.options(), next), null);
        }
        if (msg instanceof Msg.Prev) {
            int prev = model.selectedIndex() - 1;
            if (prev < 0) {
                prev = Math.max(0, model.options().size() - 1);
            }
            return new UpdateResult<>(new Model(true, model.options(), prev), null);
        }
        return new UpdateResult<>(model, null);
    }

    public static void view(Model model, TerminalBuffer buffer, int cols, int rows, List<UiEffect> effects) {
        if (!model.active() || model.options().isEmpty()) {
            return;
        }

        int maxLen = 0;
        for (String opt : model.options()) {
            maxLen = Math.max(maxLen, TextUtil.getDisplayWidth(opt));
        }

        int width = Math.min(80, maxLen + 12);
        int height = model.options().size() * 2 + 3;
        int x = (cols - width) / 2;
        int y = (rows - height) / 2;

        int borderColor = 0x555555;
        int bgColor = 0x0D1117;
        int titleColor = 0xE3B341;
        int fgColor = 0xCDD9E5;
        int dimColor = 0x8B949E;

        effects.add(new UiEffect.Dim("choice_dim", 0.85f, x + 1, y + 1, width - 2, height - 2));

        PanelComponent.drawBoxWithTitle(buffer, x, y, width, height, " Choice ", borderColor, bgColor, titleColor);

        PanelComponent.drawLeftAlignedOptions(buffer, x, width, y + 2, model.options().toArray(new String[0]),
                model.selectedIndex(), 2, 2, dimColor, fgColor, bgColor);
    }
}