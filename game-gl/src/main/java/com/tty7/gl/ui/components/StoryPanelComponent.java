package com.tty7.gl.ui.components;

import java.util.List;

import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.effect.UiEffect;
import com.tty7.gl.ui.tea.UpdateResult;
import com.tty7.gl.utils.TextUtil;

public final class StoryPanelComponent {
    private StoryPanelComponent() {
    }

    public record Model(
            boolean active,
            List<String> lines,
            int currentIndex,
            boolean isFinished) {
        public static Model init() {
            return new Model(false, List.of(), 0, false);
        }
    }

    public sealed interface Msg {
        record Show(List<String> lines) implements Msg {
        }

        record Next() implements Msg {
        }

        record Hide() implements Msg {
        }
    }

    public static UpdateResult<Model, Void> update(Model model, Msg msg) {
        if (msg instanceof Msg.Show(var lines)) {
            return new UpdateResult<>(new Model(true, lines, 0, false), null);
        }
        if (msg instanceof Msg.Hide) {
            return new UpdateResult<>(Model.init(), null);
        }
        if (msg instanceof Msg.Next && model.active()) {
            if (model.currentIndex() < model.lines().size() - 1) {
                return new UpdateResult<>(new Model(true, model.lines(), model.currentIndex() + 1, false), null);
            } else {
                return new UpdateResult<>(new Model(false, model.lines(), model.currentIndex(), true), null);
            }
        }
        return new UpdateResult<>(model, null);
    }

    public static void view(Model model, TerminalBuffer buffer, String speaker, int cols, int rows, long nowMillis,
            List<UiEffect> effects) {
        if (!model.active() || model.lines().isEmpty() || model.currentIndex() >= model.lines().size()) {
            return;
        }

        int width = Math.min(90, Math.max(40, cols - 8));
        int height = Math.min(14, Math.max(8, rows / 4));
        int x = (cols - width) / 2;
        int y = rows - height - Math.max(2, rows / 10);

        effects.add(new UiEffect.Dim("story", 0.85f, x + 1, y + 1, width - 2, height - 2));

        int borderColor = 0x555555;
        int bgColor = 0x131A21;
        int titleColor = 0xE3B341;
        int speakerColor = 0x22CC22;
        int textColor = 0xCDD9E5;
        int dimTextColor = 0x8B949E;

        PanelComponent.drawBoxWithTitle(buffer, x, y, width, height, " Message ", borderColor, bgColor, titleColor);

        int innerX = x + 3;
        int innerWidth = Math.max(1, width - 6);
        int row = y + 2;
        int endRow = y + height - 2;

        if (speaker != null && !speaker.isBlank()) {
            buffer.print(innerX, row++, "[" + speaker + "]", speakerColor, bgColor);
            row++;
        }

        String currentLine = model.lines().get(model.currentIndex());
        List<String> wrapped = wrap(currentLine, innerWidth);

        for (String line : wrapped) {
            if (row > endRow - 1) {
                break;
            }
            buffer.print(innerX, row++, line, textColor, bgColor);
        }

        if ((nowMillis / 500) % 2 == 0) {
            String indicator = " [Enter / Space] ";
            buffer.print(x + width - TextUtil.getDisplayWidth(indicator) - 2, y + height - 1, indicator, dimTextColor,
                    bgColor);
        }
    }

    private static List<String> wrap(String text, int width) {
        if (text == null || text.isEmpty()) {
            return List.of("");
        }

        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (current.isEmpty()) {
                current.append(word);
                continue;
            }

            String candidate = current + " " + word;
            if (TextUtil.getDisplayWidth(candidate) <= width) {
                current.append(' ').append(word);
            } else {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }
}
