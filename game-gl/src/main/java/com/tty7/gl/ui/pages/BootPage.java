package com.tty7.gl.ui.pages;

import java.util.ArrayList;
import java.util.List;

import com.tty7.gl.input.intent.InputIntent;
import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.cursor.CursorState;
import com.tty7.gl.renderer.effect.UiEffect;
import com.tty7.gl.ui.components.PanelComponent;
import com.tty7.gl.ui.components.StoryPanelComponent;
import com.tty7.gl.ui.effect.GlitchEffect;
import com.tty7.gl.ui.tea.Program;
import com.tty7.gl.ui.tea.UpdateResult;
import com.tty7.gl.utils.TextUtil;

public class BootPage implements Program<BootPage.Model, BootPage.Msg, BootPage.Cmd> {
    private static final int BG = 0x0D1117;
    private static final int PANEL_BG = BG;
    private static final int BORDER = 0x555555;
    private static final int FG = 0xCDD9E5;
    private static final int DIM_FG = 0x8B949E;
    private static final int ACCENT = 0x22CC22;
    private static final int WARN = 0xE3B341;
    private static final int SELECT_BG = 0x1A2A1A;
    private static final int STORY_BG = 0x131A21;
    private static final int CURSOR = 0x22CC22;

    private static final String SFX_CURSOR_MOVE = "/assets/audio/sfx/cursor-move.mp3";
    private static final String SFX_CURSOR_ENTER = "/assets/audio/sfx/cursor-enter.mp3";
    private static final String SFX_ACCEPT = "/assets/audio/sfx/accept.mp3";

    private static final long GRUB_TIMEOUT_MS = 6000L;
    private static final long LOG_LINE_INTERVAL_MS = 120L;
    private static final long STORY_LINE_INTERVAL_MS = 1100L;
    private static final long SEQUENCE_COMPLETE_HOLD_MS = 900L;

    private static final int DEFAULT_ENTRY_INDEX = 0;

    private static final Entry[] ENTRIES = {
            new Entry("TTY7", "Boot the hidden seventh terminal", Target.LOGIN),
            new Entry("TTY7 (Live Environment)", "Open diagnostics without mounting the player session",
                    Target.DIAGNOSTICS),
            new Entry("Power Off", "Display shutdown guidance and terminate the process", Target.POWEROFF)
    };

    private static final List<String> LOGIN_BOOT_LOG = List.of(
            "[    0.000000] Linux version 6.6.31-arch1-1 (tty7@boot)",
            "[    0.024913] Command line: root=/dev/mapper/tty7 rw quiet splash",
            "[    0.118403] tty7: probing reused sectors...",
            "[  OK  ] Mounted /mnt/undef/data",
            "[  OK  ] Reached target Local File Systems.",
            "[ WARN ] hidden-room.mount: journal disabled by previous owner",
            "[  OK  ] Started systemd-udevd.service",
            "[  OK  ] Started load-random-seed.service",
            "[ INFO ] grub: tty7 selected by operator",
            "[  OK  ] Started someone-wants-to-talk.service",
            "[ INFO ] /home/.tty7/root: recovered with minor corruption",
            "[ WARN ] session state marked dirty",
            "[  OK  ] Started gdm.service",
            "[  OK  ] Reached target Graphical Interface.",
            "[ INFO ] gdm: waiting on tty7 login manager");

    private static final List<String> LIVE_BOOT_LOG = List.of(
            "[    0.000000] Linux version 6.6.31-arch1-1 (tty7@boot)",
            "[    0.024913] Command line: root=/dev/mapper/tty7 rw quiet nomodeset",
            "[    0.118403] tty7: probing reused sectors...",
            "[  OK  ] Mounted /mnt/undef/data (read-only)",
            "[  OK  ] Reached target Local File Systems.",
            "[ WARN ] hidden-room.mount: journal disabled by previous owner",
            "[  OK  ] Started display-diagnostics.service",
            "[ INFO ] grub: live environment selected by operator",
            "[ WARN ] user session mount skipped",
            "[  OK  ] Launching tty7 live diagnostics");

    private static final List<String> REBOOT_LOG = List.of(
            "[  OK  ] Broadcasting wall message: system reboot requested",
            "[  OK  ] Stopping gdm.service",
            "[  OK  ] Unmounting /mnt/undef/data",
            "[  OK  ] Saving volatile session fragments",
            "[ INFO ] handoff: returning control to firmware",
            "[  OK  ] Rebooting now");

    private static final List<String> POWEROFF_LOG = List.of(
            "[  OK  ] Broadcasting wall message: power-off requested",
            "[  OK  ] Stopping tty7 session",
            "[  OK  ] Flushing reused sectors",
            "[  OK  ] Power rails entering standby",
            "[ INFO ] goodbye",
            "[  OK  ] System halted");

    private static final List<String> INTRO_STORY = List.of(
            "I moved into this room because the rent was cheap and the owner asked no questions.",
            "The machine was already here, humming like it had been waiting for someone patient enough to listen.",
            "The old tenant left no forwarding address, only dirty sectors, half-written notes, and a login prompt that refuses to forget them.",
            "Whoever \"I\" used to be in these logs, I inherited the remains. Tonight is the first time I am finally booting all the way in.");

    private final GlitchEffect glitchEffect = new GlitchEffect();

    public record Model(
            Phase phase,
            long phaseStartedAtMillis,
            int selectedIndex,
            boolean timeoutEnabled,
            Target target,
            int visibleLogLines,
            StoryPanelComponent.Model storyModel,
            boolean transitionDispatched) {

        public static Model init() {
            return firstBoot();
        }

        public static Model firstBoot() {
            return grub(false, System.currentTimeMillis());
        }

        public static Model returnToGrub() {
            return grub(true, System.currentTimeMillis());
        }

        public static Model startReboot() {
            return new Model(Phase.SHUTDOWN, System.currentTimeMillis(), DEFAULT_ENTRY_INDEX, false, Target.REBOOT, 0,
                    StoryPanelComponent.Model.init(), false);
        }

        public static Model startPowerOff() {
            return new Model(Phase.SHUTDOWN, System.currentTimeMillis(), DEFAULT_ENTRY_INDEX, false, Target.POWEROFF, 0,
                    StoryPanelComponent.Model.init(), false);
        }

        private static Model grub(boolean timeoutEnabled, long nowMillis) {
            return new Model(Phase.GRUB, nowMillis, DEFAULT_ENTRY_INDEX, timeoutEnabled,
                    ENTRIES[DEFAULT_ENTRY_INDEX].target(),
                    0, StoryPanelComponent.Model.init(), false);
        }
    }

    public enum Phase {
        GRUB,
        BOOTING,
        SHUTDOWN
    }

    private enum Target {
        LOGIN,
        DIAGNOSTICS,
        REBOOT,
        POWEROFF
    }

    private record Entry(String label, String description, Target target) {
    }

    public sealed interface Msg {
        record Intent(InputIntent intent) implements Msg {
        }

        record Tick(long nowMillis) implements Msg {
        }
    }

    public sealed interface Cmd {
        record OpenStart() implements Cmd {
        }

        record OpenDiagnostics() implements Cmd {
        }

        record Exit() implements Cmd {
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
        if (msg instanceof Msg.Intent(InputIntent intent)) {
            return switch (model.phase()) {
                case GRUB -> handleGrubIntent(model, intent);
                case BOOTING, SHUTDOWN -> handleSequenceIntent(model, intent);
            };
        }

        if (msg instanceof Msg.Tick(long nowMillis)) {
            return switch (model.phase()) {
                case GRUB -> handleGrubTick(model, nowMillis);
                case BOOTING, SHUTDOWN -> handleSequenceTick(model, nowMillis);
            };
        }

        return new UpdateResult<>(model, List.of());
    }

    @Override
    public RenderFrame view(Model model, TerminalBuffer buffer, long nowMillis) {
        buffer.clear();
        return switch (model.phase()) {
            case GRUB -> renderGrub(model, buffer, nowMillis);
            case BOOTING, SHUTDOWN -> renderSequence(model, buffer, nowMillis);
        };
    }

    private UpdateResult<Model, Cmd> handleGrubIntent(Model model, InputIntent intent) {
        if (intent instanceof InputIntent.NavigatePrev) {
            int next = model.selectedIndex() == 0 ? ENTRIES.length - 1 : model.selectedIndex() - 1;
            return new UpdateResult<>(new Model(Phase.GRUB, model.phaseStartedAtMillis(), next, false,
                    ENTRIES[next].target(), 0, StoryPanelComponent.Model.init(), false),
                    List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.NavigateNext) {
            int next = (model.selectedIndex() + 1) % ENTRIES.length;
            return new UpdateResult<>(new Model(Phase.GRUB, model.phaseStartedAtMillis(), next, false,
                    ENTRIES[next].target(), 0, StoryPanelComponent.Model.init(), false),
                    List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.Cancel) {
            return new UpdateResult<>(Model.returnToGrub(), List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.Submit) {
            return startSelectedEntry(model, System.currentTimeMillis());
        }

        return new UpdateResult<>(model, List.of());
    }

    private UpdateResult<Model, Cmd> handleSequenceIntent(Model model, InputIntent intent) {
        if (model.storyModel().active()) {
            boolean isAdvanceIntent = intent instanceof InputIntent.Submit
                    || (intent instanceof InputIntent.TextTyped tt && (tt.value() == ' ' || tt.value() == '\n'));
            if (isAdvanceIntent) {
                UpdateResult<StoryPanelComponent.Model, Void> r = StoryPanelComponent.update(model.storyModel(),
                        new StoryPanelComponent.Msg.Next());
                Model nextModel = new Model(model.phase(), model.phaseStartedAtMillis(), model.selectedIndex(),
                        model.timeoutEnabled(), model.target(), model.visibleLogLines(), r.model(),
                        model.transitionDispatched());

                if (r.model().isFinished() && !nextModel.transitionDispatched()) {
                    return completeSequence(nextModel, true);
                }
                return new UpdateResult<>(nextModel, List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
            }
            return new UpdateResult<>(model, List.of());
        }

        if (intent instanceof InputIntent.Submit || intent instanceof InputIntent.Cancel) {
            return fastForwardSequence(model, System.currentTimeMillis());
        }
        return new UpdateResult<>(model, List.of());
    }

    private UpdateResult<Model, Cmd> handleGrubTick(Model model, long nowMillis) {
        if (!model.timeoutEnabled()) {
            return new UpdateResult<>(model, List.of());
        }
        if (nowMillis - model.phaseStartedAtMillis() < GRUB_TIMEOUT_MS) {
            return new UpdateResult<>(model, List.of());
        }
        return startSelectedEntry(model, nowMillis);
    }

    private UpdateResult<Model, Cmd> handleSequenceTick(Model model, long nowMillis) {
        if (model.transitionDispatched()) {
            return new UpdateResult<>(model, List.of());
        }

        List<String> logLines = sequenceLogLines(model.target());
        List<String> storyLines = sequenceStoryLines(model.target());
        long elapsed = Math.max(0L, nowMillis - model.phaseStartedAtMillis());

        int revealedLogs = Math.min(logLines.size(), (int) (elapsed / LOG_LINE_INTERVAL_MS) + 1);

        Model nextModel = model;
        StoryPanelComponent.Model nextStoryModel = model.storyModel();

        if (revealedLogs != model.visibleLogLines()) {
            nextModel = new Model(model.phase(), model.phaseStartedAtMillis(), model.selectedIndex(),
                    model.timeoutEnabled(), model.target(), revealedLogs, nextStoryModel, model.transitionDispatched());
        }

        if (revealedLogs >= logLines.size() && !storyLines.isEmpty() && !nextStoryModel.active()
                && !nextStoryModel.isFinished()) {
            UpdateResult<StoryPanelComponent.Model, Void> r = StoryPanelComponent.update(nextStoryModel,
                    new StoryPanelComponent.Msg.Show(storyLines));
            nextStoryModel = r.model();
            nextModel = new Model(nextModel.phase(), nextModel.phaseStartedAtMillis(), nextModel.selectedIndex(),
                    nextModel.timeoutEnabled(), nextModel.target(), revealedLogs, nextStoryModel,
                    nextModel.transitionDispatched());
        }

        if (revealedLogs >= logLines.size() && (storyLines.isEmpty() || nextStoryModel.isFinished())) {
            long logFinishTime = model.phaseStartedAtMillis() + logLines.size() * LOG_LINE_INTERVAL_MS;
            if (!nextModel.transitionDispatched() && nowMillis >= logFinishTime + SEQUENCE_COMPLETE_HOLD_MS) {
                return completeSequence(nextModel, true);
            }
        }

        return new UpdateResult<>(nextModel, List.of());
    }

    private UpdateResult<Model, Cmd> fastForwardSequence(Model model, long nowMillis) {
        List<String> logLines = sequenceLogLines(model.target());
        List<String> storyLines = sequenceStoryLines(model.target());

        if (!storyLines.isEmpty() && !model.storyModel().isFinished()) {
            StoryPanelComponent.Model nextStoryModel = model.storyModel().active() ? model.storyModel()
                    : StoryPanelComponent.update(model.storyModel(), new StoryPanelComponent.Msg.Show(storyLines))
                            .model();
            Model nextModel = new Model(model.phase(), model.phaseStartedAtMillis(), model.selectedIndex(),
                    model.timeoutEnabled(), model.target(), logLines.size(), nextStoryModel, false);
            return new UpdateResult<>(nextModel, List.of());
        }

        Model finished = new Model(model.phase(), nowMillis, model.selectedIndex(), model.timeoutEnabled(),
                model.target(), logLines.size(), model.storyModel(), false);
        return completeSequence(finished, true);
    }

    private UpdateResult<Model, Cmd> completeSequence(Model model, boolean playAccept) {
        List<Cmd> commands = new ArrayList<>();
        if (playAccept) {
            commands.add(new Cmd.PlaySound(SFX_ACCEPT));
        }

        if (model.target() == Target.REBOOT) {
            return new UpdateResult<>(Model.returnToGrub(), commands);
        }

        if (model.target() == Target.LOGIN) {
            commands.add(new Cmd.OpenStart());
        } else if (model.target() == Target.DIAGNOSTICS) {
            commands.add(new Cmd.OpenDiagnostics());
        } else if (model.target() == Target.POWEROFF) {
            commands.add(new Cmd.Exit());
        }

        Model nextModel = new Model(model.phase(), model.phaseStartedAtMillis(), model.selectedIndex(),
                model.timeoutEnabled(), model.target(), sequenceLogLines(model.target()).size(), model.storyModel(),
                true);
        return new UpdateResult<>(nextModel, commands);
    }

    private UpdateResult<Model, Cmd> startSelectedEntry(Model model, long nowMillis) {
        Entry entry = ENTRIES[model.selectedIndex()];
        Phase nextPhase = entry.target() == Target.POWEROFF ? Phase.SHUTDOWN : Phase.BOOTING;
        Model nextModel = new Model(nextPhase, nowMillis, model.selectedIndex(), false, entry.target(), 0,
                StoryPanelComponent.Model.init(), false);
        return new UpdateResult<>(nextModel, List.of(new Cmd.PlaySound(SFX_CURSOR_ENTER)));
    }

    private RenderFrame renderGrub(Model model, TerminalBuffer buffer, long nowMillis) {
        int cols = buffer.cols();
        int rows = buffer.rows();
        if (cols < 24 || rows < 8) {
            printClipped(buffer, 0, 0, Math.max(1, cols), "tty7 boot menu", FG, BG);
            printClipped(buffer, 0, Math.min(rows - 1, 1), Math.max(1, cols), ENTRIES[model.selectedIndex()].label(),
                    ACCENT, BG);
            return new RenderFrame(buffer, new CursorState(0, Math.min(rows - 1, 1), true, true, CURSOR),
                    List.of(new UiEffect.Crt(0.15f)));
        }

        int boxWidth = Math.min(92, Math.max(24, cols - 4));
        int boxHeight = Math.min(Math.max(14, ENTRIES.length + 11), Math.max(8, rows - 2));
        int boxX = Math.max(0, (cols - boxWidth) / 2);
        int boxY = Math.max(0, (rows - boxHeight) / 2);

        PanelComponent.drawBoxWithTitle(buffer, boxX, boxY, boxWidth, boxHeight, " GNU GRUB 2.12-tty7 ", BORDER,
                PANEL_BG, FG);

        int contentX = boxX + 3;
        int contentWidth = Math.max(1, boxWidth - 6);
        int row = boxY + 2;

        printClipped(buffer, contentX, row++, contentWidth, "Reused disk detected. Select the environment to boot.",
                DIM_FG, PANEL_BG);
        printClipped(buffer, contentX, row++, contentWidth,
                "First arrival keeps autoboot disabled so the operator can inspect the menu.", DIM_FG, PANEL_BG);
        row++;

        int cursorCol = contentX;
        int cursorRow = row;
        for (int i = 0; i < ENTRIES.length; i++) {
            Entry entry = ENTRIES[i];
            boolean selected = i == model.selectedIndex();
            int bg = selected ? SELECT_BG : PANEL_BG;
            int fg = selected ? FG : DIM_FG;
            String line = "  " + entry.label();
            buffer.print(contentX, row, padRight(line, contentWidth), fg, bg);
            if (selected) {
                buffer.print(contentX, row, "> ", ACCENT, bg);
                cursorCol = contentX;
                cursorRow = row;
            }
            row++;
        }

        row++;
        printClipped(buffer, contentX, row++, contentWidth, ENTRIES[model.selectedIndex()].description(), WARN,
                PANEL_BG);

        String timeoutLine;
        if (model.timeoutEnabled()) {
            long remaining = Math.max(0L, GRUB_TIMEOUT_MS - (nowMillis - model.phaseStartedAtMillis()));
            long seconds = Math.max(0L, (remaining + 999L) / 1000L);
            timeoutLine = "The highlighted entry will boot automatically in " + seconds + "s.";
        } else {
            timeoutLine = "Autoboot paused. Press Esc to restore timeout on future boots.";
        }
        printClipped(buffer, contentX, boxY + boxHeight - 3, contentWidth, timeoutLine, FG, PANEL_BG);
        printClipped(buffer, contentX, boxY + boxHeight - 2, contentWidth,
                "Enter=boot  Up/Down=navigate  Esc=restore default timeout", DIM_FG, PANEL_BG);

        return new RenderFrame(buffer, new CursorState(cursorCol, cursorRow, true, true, CURSOR),
                List.of(new UiEffect.Crt(0.15f)));
    }

    private RenderFrame renderSequence(Model model, TerminalBuffer buffer, long nowMillis) {
        int cols = buffer.cols();
        int rows = buffer.rows();
        if (cols < 28 || rows < 8) {
            printClipped(buffer, 0, 0, Math.max(1, cols), sequenceHeader(model.target()), ACCENT, BG);
            return new RenderFrame(buffer, null, List.of(new UiEffect.Crt(0.15f)));
        }

        int boxWidth = Math.min(100, Math.max(28, cols - 8));
        int boxHeight = Math.min(30, Math.max(8, rows - 4));
        int boxX = Math.max(0, (cols - boxWidth) / 2);
        int boxY = Math.max(0, (rows - boxHeight) / 2);

        PanelComponent.drawBoxWithTitle(buffer, boxX, boxY, boxWidth, boxHeight, " Boot Sequence ", BORDER, BG, ACCENT);
        drawSequenceLogs(model, buffer, boxX + 2, boxY + 1, boxWidth - 4, boxHeight - 3, nowMillis);

        int statusRow = boxY + boxHeight - 2;
        String status = sequenceStatusLine(model, nowMillis);
        printClipped(buffer, boxX + 1, statusRow, Math.max(1, boxWidth - 2), status,
                isSequenceComplete(model) ? ACCENT : FG, BG);
        printClipped(buffer, boxX + 1, boxY + boxHeight - 1, Math.max(1, boxWidth - 2),
                "Enter=skip animation  Esc=skip animation", DIM_FG, BG);

        List<UiEffect> effects = new ArrayList<>();
        effects.add(new UiEffect.Crt(0.15f));
        UiEffect.Glitch glitch = glitchEffect.update(nowMillis);
        if (glitch != null) {
            effects.add(glitch);
        }

        int cursorCol = Math.min(cols - 1, Math.max(0, boxX + 1 + TextUtil.getDisplayWidth(status)));
        CursorState cursorState = new CursorState(cursorCol, statusRow, !isSequenceComplete(model), true, CURSOR);

        if (model.target() == Target.LOGIN && model.storyModel().active()) {
            StoryPanelComponent.view(model.storyModel(), buffer, "me", cols, rows, nowMillis, effects);
            cursorState = null; // hide main cursor when story is active
        }

        return new RenderFrame(buffer, cursorState, effects);
    }

    private void drawSequenceLogs(Model model, TerminalBuffer buffer, int x, int y, int width, int height,
            long nowMillis) {
        if (height <= 0 || width <= 0) {
            return;
        }

        List<String> lines = sequenceLogLines(model.target());
        int visibleCount = Math.max(0, Math.min(model.visibleLogLines(), lines.size()));
        int firstVisible = Math.max(0, visibleCount - height);
        int row = y;
        for (int i = firstVisible; i < visibleCount && row < y + height; i++) {
            printBootLine(buffer, x, row++, width, lines.get(i));
        }

        String footer = isSequenceComplete(model)
                ? "[  OK  ] " + sequenceFooter(model.target())
                : "[ INFO ] " + sequenceProgressLabel(model.target()) + " " + spinnerFrame(nowMillis);
        printClipped(buffer, x, Math.min(buffer.rows() - 2, y + height), width, footer,
                isSequenceComplete(model) ? ACCENT : FG, BG);
    }

    private void printBootLine(TerminalBuffer buffer, int x, int y, int width, String line) {
        int fg = FG;
        if (line.contains("[ WARN ]")) {
            fg = WARN;
        } else if (line.contains("[ INFO ]")) {
            fg = DIM_FG;
        }
        printClipped(buffer, x, y, width, line, fg, BG);
    }

    private List<String> sequenceLogLines(Target target) {
        return switch (target) {
            case LOGIN -> LOGIN_BOOT_LOG;
            case DIAGNOSTICS -> LIVE_BOOT_LOG;
            case REBOOT -> REBOOT_LOG;
            case POWEROFF -> POWEROFF_LOG;
        };
    }

    private List<String> sequenceStoryLines(Target target) {
        return target == Target.LOGIN ? INTRO_STORY : List.of();
    }

    private boolean isSequenceComplete(Model model) {
        boolean logsDone = model.visibleLogLines() >= sequenceLogLines(model.target()).size();
        boolean storyDone = sequenceStoryLines(model.target()).isEmpty() || model.storyModel().isFinished();
        return logsDone && storyDone;
    }

    private String sequenceHeader(Target target) {
        return switch (target) {
            case LOGIN -> "booting tty7";
            case DIAGNOSTICS -> "booting tty7 live";
            case REBOOT -> "rebooting tty7";
            case POWEROFF -> "powering off tty7";
        };
    }

    private String sequenceProgressLabel(Target target) {
        return switch (target) {
            case LOGIN -> "booting tty7 + memory handoff";
            case DIAGNOSTICS -> "booting tty7 live environment";
            case REBOOT -> "stopping services for reboot";
            case POWEROFF -> "stopping services for shutdown";
        };
    }

    private String sequenceFooter(Target target) {
        return switch (target) {
            case LOGIN -> "handoff complete, opening gdm";
            case DIAGNOSTICS -> "handoff complete, opening live diagnostics";
            case REBOOT -> "shutdown complete, returning to grub";
            case POWEROFF -> "shutdown complete, terminating process";
        };
    }

    private String sequenceStatusLine(Model model, long nowMillis) {
        if (isSequenceComplete(model)) {
            return sequenceFooter(model.target());
        }
        return sequenceProgressLabel(model.target()) + " " + spinnerFrame(nowMillis);
    }

    private String spinnerFrame(long nowMillis) {
        String[] frames = { "-", "\\", "|", "/" };
        return frames[(int) ((nowMillis / 120L) % frames.length)];
    }

    private void printClipped(TerminalBuffer buffer, int x, int y, int width, String text, int fg, int bg) {
        if (y < 0 || y >= buffer.rows() || width <= 0 || x >= buffer.cols()) {
            return;
        }
        int safeX = Math.max(0, x);
        int safeWidth = Math.min(width - (safeX - x), buffer.cols() - safeX);
        if (safeWidth <= 0) {
            return;
        }

        String content = text == null ? "" : text;
        while (TextUtil.getDisplayWidth(content) > safeWidth && !content.isEmpty()) {
            content = content.substring(0, content.length() - 1);
        }
        buffer.print(safeX, y, content, fg, bg);
    }

    private String padRight(String text, int width) {
        int displayWidth = TextUtil.getDisplayWidth(text);
        if (displayWidth >= width) {
            return text;
        }
        return text + " ".repeat(width - displayWidth);
    }
}
