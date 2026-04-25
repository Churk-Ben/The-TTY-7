package com.tty7.gl.ui.pages;

import java.util.ArrayList;
import java.util.List;

import com.tty7.gl.input.intent.InputIntent;
import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.cursor.CursorState;
import com.tty7.gl.renderer.effect.UiEffect;
import com.tty7.gl.ui.components.CompleterComponent;
import com.tty7.gl.ui.components.PanelComponent;
import com.tty7.gl.ui.components.StoryPanelComponent;
import com.tty7.gl.ui.tea.Program;
import com.tty7.gl.ui.tea.UpdateResult;
import com.tty7.gl.utils.TextUtil;

public class ConsolePage implements Program<ConsolePage.Model, ConsolePage.Msg, ConsolePage.Cmd> {
    private static final int BG = 0x0D1117;
    private static final int PANEL_BG = BG;
    private static final int STORY_BG = 0x131A21;
    private static final int FG = 0xCDD9E5;
    private static final int DIM_FG = 0x8B949E;
    private static final int ACCENT = 0x22CC22;
    private static final int WARN = 0xE3B341;
    private static final int BORDER = 0x555555;
    private static final int CURSOR = 0x22CC22;

    private static final String SFX_TYPE_IN = "/assets/audio/sfx/type-in.mp3";
    private static final String SFX_INTERACT = "/assets/audio/sfx/interact.mp3";

    private static final long CURSOR_SOLID_AFTER_EDIT_MS = 800L;
    private static final List<String> COMMANDS = List.of("help", "mail", "memory", "reply", "scan", "whoami",
            "logout");
    private static final String PROMPT = "root@tty7:~$ ";

    public record Model(
            List<String> outputLines,
            String line,
            int cursorIndex,
            long cursorSolidUntilMillis,
            int storyStage,
            CompleterComponent.Model completerModel,
            boolean paused,
            int pauseSelectedIndex,
            StoryPanelComponent.Model storyModel) {

        public static Model init() {
            return new Model(
                    List.of(
                            "tty7 session established.",
                            "Type `help` to inspect the room.",
                            "The cursor is yours now."),
                    "",
                    0,
                    0L,
                    0,
                    CompleterComponent.Model.init(),
                    false,
                    0,
                    StoryPanelComponent
                            .update(StoryPanelComponent.Model.init(), new StoryPanelComponent.Msg.Show(storyLines(0)))
                            .model());
        }

        public Model withCompleter(CompleterComponent.Model completer) {
            return new Model(outputLines, line, cursorIndex, cursorSolidUntilMillis, storyStage, completer, paused,
                    pauseSelectedIndex, storyModel);
        }

        public Model withPause(boolean paused, int pauseSelectedIndex) {
            return new Model(outputLines, line, cursorIndex, cursorSolidUntilMillis, storyStage, completerModel, paused,
                    pauseSelectedIndex, storyModel);
        }
    }

    public sealed interface Msg {
        record Intent(InputIntent intent) implements Msg {
        }
    }

    public sealed interface Cmd {
        record ReturnToLogin() implements Cmd {
        }

        record PlaySound(String resourcePath) implements Cmd {
        }
    }

    @Override
    public Model init() {
        return Model.init();
    }

    @Override
    public UpdateResult<Model, Cmd> update(Model model, Msg msg) {
        if (!(msg instanceof Msg.Intent(InputIntent intent))) {
            return new UpdateResult<>(model, List.of());
        }

        if (model.storyModel().active()) {
            boolean isAdvanceIntent = intent instanceof InputIntent.Submit
                    || (intent instanceof InputIntent.TextTyped tt && (tt.value() == ' ' || tt.value() == '\n'));
            if (isAdvanceIntent) {
                UpdateResult<StoryPanelComponent.Model, Void> r = StoryPanelComponent.update(model.storyModel(),
                        new StoryPanelComponent.Msg.Next());
                Model nextModel = new Model(model.outputLines(), model.line(), model.cursorIndex(),
                        model.cursorSolidUntilMillis(), model.storyStage(), model.completerModel(), model.paused(),
                        model.pauseSelectedIndex(), r.model());
                return new UpdateResult<>(nextModel, List.of(new Cmd.PlaySound(SFX_INTERACT)));
            }
            return new UpdateResult<>(model, List.of());
        }

        if (model.paused()) {
            return handlePauseIntent(model, intent);
        }

        if (intent instanceof InputIntent.TextTyped(char value)) {
            return appendTypedChar(model, value);
        }

        if (intent instanceof InputIntent.PasteText(String value) && value != null && !value.isEmpty()) {
            return appendPastedText(model, value);
        }

        if (intent instanceof InputIntent.Cancel) {
            return new UpdateResult<>(model.withPause(true, 0), List.of(new Cmd.PlaySound(SFX_INTERACT)));
        }

        if (model.completerModel().active()) {
            UpdateResult<Model, Cmd> handled = handleCompleterIntent(model, intent);
            if (handled != null) {
                return handled;
            }
        }

        String line = model.line();
        int cursor = clampCursor(model.cursorIndex(), line.length());

        if (intent instanceof InputIntent.Backspace && cursor > 0) {
            String nextLine = line.substring(0, cursor - 1) + line.substring(cursor);
            return new UpdateResult<>(new Model(model.outputLines(), nextLine, cursor - 1,
                    System.currentTimeMillis() + CURSOR_SOLID_AFTER_EDIT_MS, model.storyStage(),
                    hideCompleter(model.completerModel()), false, model.pauseSelectedIndex(), model.storyModel()),
                    List.of());
        }

        if (intent instanceof InputIntent.Delete && cursor < line.length()) {
            String nextLine = line.substring(0, cursor) + line.substring(cursor + 1);
            return new UpdateResult<>(new Model(model.outputLines(), nextLine, cursor,
                    System.currentTimeMillis() + CURSOR_SOLID_AFTER_EDIT_MS, model.storyStage(),
                    hideCompleter(model.completerModel()), false, model.pauseSelectedIndex(), model.storyModel()),
                    List.of());
        }

        if (intent instanceof InputIntent.MoveCursorLeft) {
            return new UpdateResult<>(new Model(model.outputLines(), line, Math.max(0, cursor - 1),
                    model.cursorSolidUntilMillis(), model.storyStage(), model.completerModel(), false,
                    model.pauseSelectedIndex(), model.storyModel()), List.of());
        }

        if (intent instanceof InputIntent.MoveCursorRight) {
            return new UpdateResult<>(new Model(model.outputLines(), line, Math.min(line.length(), cursor + 1),
                    model.cursorSolidUntilMillis(), model.storyStage(), model.completerModel(), false,
                    model.pauseSelectedIndex(), model.storyModel()), List.of());
        }

        if (intent instanceof InputIntent.Tab) {
            String prefix = currentPrefix(line, cursor);
            List<String> matches = complete(prefix);
            if (matches.isEmpty()) {
                return new UpdateResult<>(model, List.of());
            }
            return new UpdateResult<>(model.withCompleter(showCompleter(matches)),
                    List.of(new Cmd.PlaySound(SFX_INTERACT)));
        }

        if (intent instanceof InputIntent.Submit) {
            return executeCommand(model);
        }

        return new UpdateResult<>(model, List.of());
    }

    @Override
    public RenderFrame view(Model model, TerminalBuffer buffer, long nowMillis) {
        buffer.clear();
        int cols = buffer.cols();
        int rows = buffer.rows();

        if (cols < 40 || rows < 12) {
            String text = PROMPT + model.line();
            buffer.print(0, 0, text, FG, BG);
            int cursorCol = Math.min(cols - 1,
                    visualOffset(PROMPT + model.line(), PROMPT.length() + model.cursorIndex()));
            return new RenderFrame(buffer, new CursorState(cursorCol, 0, true, true, CURSOR),
                    List.of(new UiEffect.Crt(0.15f)));
        }

        int boxWidth = Math.min(120, Math.max(40, cols - 4));
        int boxHeight = Math.min(40, Math.max(12, rows - 2));
        int boxX = Math.max(0, (cols - boxWidth) / 2);
        int boxY = Math.max(0, (rows - boxHeight) / 2);

        PanelComponent.drawBoxWithTitle(buffer, boxX, boxY, boxWidth, boxHeight, " Console Session ", BORDER, PANEL_BG,
                ACCENT);
        int outputTop = boxY + 1;
        int outputBottom = boxY + boxHeight - 3;
        int outputWidth = boxWidth - 4;
        List<String> visibleLines = flattenOutput(model, outputWidth);
        int visibleCount = Math.max(0, outputBottom - outputTop + 1);
        int firstVisible = Math.max(0, visibleLines.size() - visibleCount);
        int row = outputTop;
        for (int i = firstVisible; i < visibleLines.size() && row <= outputBottom; i++) {
            buffer.print(boxX + 2, row++, visibleLines.get(i), FG, PANEL_BG);
        }

        int inputRow = boxY + boxHeight - 2;
        String input = PROMPT + model.line();
        buffer.print(boxX + 2, inputRow, padRight(input, Math.max(1, boxWidth - 4)), FG, PANEL_BG);

        int cursorCol = Math.min(cols - 1, boxX + 2 + visualOffset(input, PROMPT.length() + model.cursorIndex()));
        int cursorRow = inputRow;

        List<UiEffect> effects = new ArrayList<>();
        effects.add(new UiEffect.Crt(0.15f));

        if (model.completerModel().active() && !model.paused() && !model.storyModel().active()) {
            CompleterComponent.view(model.completerModel(), buffer, Math.min(cols - 22, cursorCol), inputRow - 5, 3,
                    22);
        }

        if (model.paused()) {
            String[] pauseOptions = { "Resume Session", "Return To Login" };
            int maxWidth = Math.max(TextUtil.getDisplayWidth(pauseOptions[0]),
                    TextUtil.getDisplayWidth(pauseOptions[1]));
            int pauseBoxWidth = maxWidth + 12;
            int pauseBoxHeight = pauseOptions.length * 2 + 3;
            int pauseBoxX = (cols - pauseBoxWidth) / 2;
            int pauseBoxY = (rows - pauseBoxHeight) / 2;

            effects.add(new UiEffect.Dim(0.85f, pauseBoxX, pauseBoxY, pauseBoxWidth, pauseBoxHeight));
            PanelComponent.drawBoxWithTitle(buffer, pauseBoxX, pauseBoxY, pauseBoxWidth, pauseBoxHeight, " Paused ",
                    BORDER, BG, HOVER());
            int[] pauseCursor = PanelComponent.drawLeftAlignedOptions(buffer, pauseBoxX, pauseBoxWidth, pauseBoxY + 2,
                    pauseOptions,
                    model.pauseSelectedIndex(), 2, 2, DIM_FG, FG, BG);
            cursorCol = pauseCursor[0];
            cursorRow = pauseCursor[1];
        }

        CursorState cursorState = null;
        if (model.storyModel().active()) {
            StoryPanelComponent.view(model.storyModel(), buffer, "narration", cols, rows, nowMillis, effects);
        } else {
            boolean forceSolid = model.paused() || nowMillis < model.cursorSolidUntilMillis();
            boolean visible = forceSolid || ((nowMillis / 500L) % 2L) == 0L;
            cursorState = new CursorState(cursorCol, cursorRow, visible, true, CURSOR);
        }

        return new RenderFrame(buffer, cursorState, effects);
    }

    private UpdateResult<Model, Cmd> handlePauseIntent(Model model, InputIntent intent) {
        if (intent instanceof InputIntent.Cancel) {
            return new UpdateResult<>(model.withPause(false, model.pauseSelectedIndex()),
                    List.of(new Cmd.PlaySound(SFX_INTERACT)));
        }
        if (intent instanceof InputIntent.NavigatePrev) {
            int next = model.pauseSelectedIndex() == 0 ? 1 : 0;
            return new UpdateResult<>(model.withPause(true, next), List.of(new Cmd.PlaySound(SFX_INTERACT)));
        }
        if (intent instanceof InputIntent.NavigateNext) {
            int next = (model.pauseSelectedIndex() + 1) % 2;
            return new UpdateResult<>(model.withPause(true, next), List.of(new Cmd.PlaySound(SFX_INTERACT)));
        }
        if (intent instanceof InputIntent.Submit) {
            if (model.pauseSelectedIndex() == 0) {
                return new UpdateResult<>(model.withPause(false, 0), List.of(new Cmd.PlaySound(SFX_INTERACT)));
            }
            return new UpdateResult<>(model, List.of(new Cmd.ReturnToLogin(), new Cmd.PlaySound(SFX_INTERACT)));
        }
        return new UpdateResult<>(model, List.of());
    }

    private UpdateResult<Model, Cmd> handleCompleterIntent(Model model, InputIntent intent) {
        if (intent instanceof InputIntent.NavigatePrev) {
            UpdateResult<CompleterComponent.Model, Void> result = CompleterComponent.update(model.completerModel(),
                    new CompleterComponent.Msg.Prev());
            return new UpdateResult<>(model.withCompleter(result.model()), List.of(new Cmd.PlaySound(SFX_INTERACT)));
        }

        if (intent instanceof InputIntent.NavigateNext) {
            UpdateResult<CompleterComponent.Model, Void> result = CompleterComponent.update(model.completerModel(),
                    new CompleterComponent.Msg.Next());
            return new UpdateResult<>(model.withCompleter(result.model()), List.of(new Cmd.PlaySound(SFX_INTERACT)));
        }

        if (intent instanceof InputIntent.Submit || intent instanceof InputIntent.Tab) {
            List<String> items = model.completerModel().items();
            int index = model.completerModel().selectedIndex();
            if (index >= 0 && index < items.size()) {
                String selected = items.get(index);
                String line = model.line();
                int cursor = clampCursor(model.cursorIndex(), line.length());
                String prefix = currentPrefix(line, cursor);
                String nextLine = line.substring(0, cursor - prefix.length()) + selected + line.substring(cursor);
                return new UpdateResult<>(new Model(model.outputLines(), nextLine,
                        cursor - prefix.length() + selected.length(),
                        System.currentTimeMillis() + CURSOR_SOLID_AFTER_EDIT_MS,
                        model.storyStage(), hideCompleter(model.completerModel()), false, model.pauseSelectedIndex(),
                        model.storyModel()),
                        List.of(new Cmd.PlaySound(SFX_TYPE_IN)));
            }
            return new UpdateResult<>(model.withCompleter(hideCompleter(model.completerModel())), List.of());
        }

        if (intent instanceof InputIntent.MoveCursorLeft || intent instanceof InputIntent.MoveCursorRight
                || intent instanceof InputIntent.Backspace || intent instanceof InputIntent.Delete) {
            return new UpdateResult<>(model.withCompleter(hideCompleter(model.completerModel())), List.of());
        }

        return null;
    }

    private UpdateResult<Model, Cmd> appendTypedChar(Model model, char value) {
        String line = model.line();
        int cursor = clampCursor(model.cursorIndex(), line.length());
        String nextLine = line.substring(0, cursor) + value + line.substring(cursor);
        Model next = new Model(model.outputLines(), nextLine, cursor + 1,
                System.currentTimeMillis() + CURSOR_SOLID_AFTER_EDIT_MS,
                model.storyStage(), hideCompleter(model.completerModel()), false, model.pauseSelectedIndex(),
                model.storyModel());
        return new UpdateResult<>(next, List.of(new Cmd.PlaySound(SFX_TYPE_IN)));
    }

    private UpdateResult<Model, Cmd> appendPastedText(Model model, String value) {
        String line = model.line();
        int cursor = clampCursor(model.cursorIndex(), line.length());
        String nextLine = line.substring(0, cursor) + value + line.substring(cursor);
        Model next = new Model(model.outputLines(), nextLine, cursor + value.length(),
                System.currentTimeMillis() + CURSOR_SOLID_AFTER_EDIT_MS, model.storyStage(),
                hideCompleter(model.completerModel()), false, model.pauseSelectedIndex(), model.storyModel());
        return new UpdateResult<>(next, List.of());
    }

    private UpdateResult<Model, Cmd> executeCommand(Model model) {
        String raw = model.line();
        String command = raw.trim();
        List<String> output = new ArrayList<>(model.outputLines());
        output.add(PROMPT + raw);

        int nextStoryStage = model.storyStage();
        List<Cmd> commands = new ArrayList<>();
        commands.add(new Cmd.PlaySound(SFX_TYPE_IN));

        if (command.isEmpty()) {
            output.add("");
        } else if ("help".equals(command)) {
            output.add("Available commands: help, mail, memory, reply, scan, whoami, logout");
        } else if ("whoami".equals(command)) {
            output.add("root, temporary owner of the machine and its unfinished memories.");
        } else if ("scan".equals(command)) {
            output.add("scan: 1 hidden mailbox, 4 corrupted note fragments, 1 active login shell.");
        } else if ("mail".equals(command)) {
            output.add("mail[0]: unread message recovered from the previous tenant.");
            output.add(
                    "\"If the machine ever reaches tty7 again, do not trust the first thing it asks you to remember.\"");
            nextStoryStage = Math.max(nextStoryStage, 1);
        } else if ("memory".equals(command)) {
            output.add("memory: sectors 0041-0044 contain personal notes, all signed only as `me`.");
            nextStoryStage = Math.max(nextStoryStage, 2);
        } else if ("reply".equals(command)) {
            output.add("reply: draft buffer opened. No recipient address survives.");
            output.add("Maybe the real answer is to keep digging before sending anything.");
            nextStoryStage = Math.max(nextStoryStage, 3);
        } else if ("logout".equals(command)) {
            output.add("Session closed. Returning to gdm.");
            commands.add(new Cmd.ReturnToLogin());
        } else {
            output.add(command + ": command not found");
        }

        StoryPanelComponent.Model nextStoryModel = model.storyModel();
        if (nextStoryStage != model.storyStage()) {
            nextStoryModel = StoryPanelComponent
                    .update(nextStoryModel, new StoryPanelComponent.Msg.Show(storyLines(nextStoryStage))).model();
        }

        Model next = new Model(List.copyOf(output), "", 0, System.currentTimeMillis() + CURSOR_SOLID_AFTER_EDIT_MS,
                nextStoryStage, hideCompleter(model.completerModel()), false, model.pauseSelectedIndex(),
                nextStoryModel);
        return new UpdateResult<>(next, commands);
    }

    private static List<String> storyLines(int stage) {
        return switch (stage) {
            case 0 -> List.of(
                    "The terminal feels bigger than the room. There is no quest marker yet, only unease.",
                    "A cracked note is taped below the monitor: start with `help`, then decide what kind of witness you want to be.");
            case 1 -> List.of(
                    "The first recovered mail confirms somebody expected this machine to boot again.",
                    "Three obvious leads remain: inspect `memory`, prepare a `reply`, or keep probing with `scan`.");
            case 2 -> List.of(
                    "The recovered notes describe a life already half-overwritten by the disk itself.",
                    "You can `reply` to the vanished sender, keep cataloguing with `scan`, or retreat with `logout`.");
            default -> List.of(
                    "The room is no longer silent. Whatever happened here is ready to answer back.",
                    "The placeholder console is live now; later branches can replace these hints with real narrative choices.");
        };
    }

    private static List<String> complete(String prefix) {
        return COMMANDS.stream()
                .filter(command -> command.startsWith(prefix))
                .limit(3)
                .toList();
    }

    private static CompleterComponent.Model showCompleter(List<String> items) {
        return CompleterComponent.update(CompleterComponent.Model.init(), new CompleterComponent.Msg.Show(items))
                .model();
    }

    private static CompleterComponent.Model hideCompleter(CompleterComponent.Model current) {
        return CompleterComponent.update(current, new CompleterComponent.Msg.Hide()).model();
    }

    private static List<String> flattenOutput(Model model, int width) {
        List<String> lines = new ArrayList<>();
        for (String line : model.outputLines()) {
            lines.addAll(splitByWidth(line, width));
        }
        return lines;
    }

    private static List<String> splitByWidth(String text, int width) {
        if (text == null || text.isEmpty()) {
            return List.of("");
        }

        List<String> lines = new ArrayList<>();
        String remaining = text;
        while (!remaining.isEmpty()) {
            int end = Math.min(remaining.length(), Math.max(1, width));
            while (end > 1 && TextUtil.getDisplayWidth(remaining.substring(0, end)) > width) {
                end--;
            }
            lines.add(remaining.substring(0, end));
            remaining = remaining.substring(end);
        }
        return lines;
    }

    private static int clampCursor(int cursor, int lineLength) {
        return Math.max(0, Math.min(cursor, lineLength));
    }

    private static String currentPrefix(String line, int cursor) {
        int start = cursor;
        while (start > 0) {
            char ch = line.charAt(start - 1);
            if (!Character.isLetter(ch)) {
                break;
            }
            start--;
        }
        return line.substring(start, cursor);
    }

    private static int visualOffset(String text, int cursorIndex) {
        int safeIndex = Math.max(0, Math.min(cursorIndex, text.length()));
        return TextUtil.getDisplayWidth(text.substring(0, safeIndex));
    }

    private static String padRight(String text, int width) {
        int displayWidth = TextUtil.getDisplayWidth(text);
        if (displayWidth >= width) {
            return text;
        }
        return text + " ".repeat(width - displayWidth);
    }

    private static int HOVER() {
        return 0xFFFFFF;
    }
}
