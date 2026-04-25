package com.tty7.gl.ui.pages;

import java.util.ArrayList;
import java.util.List;

import com.tty7.gl.input.intent.InputIntent;
import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.cursor.CursorState;
import com.tty7.gl.renderer.effect.UiEffect;
import com.tty7.gl.ui.components.PanelComponent;
import com.tty7.gl.ui.effect.GlitchEffect;
import com.tty7.gl.ui.tea.Program;
import com.tty7.gl.ui.tea.UpdateResult;
import com.tty7.gl.utils.TextUtil;

public class BootPage implements Program<BootPage.Model, BootPage.Msg, BootPage.Cmd> {
    private static final int BG = 0x0D1117;
    private static final int PANEL_BG = 0x11161D;
    private static final int BORDER = 0x555555;
    private static final int FG = 0xCDD9E5;
    private static final int DIM_FG = 0x8B949E;
    private static final int ACCENT = 0x22CC22;
    private static final int WARN = 0xE3B341;
    private static final int SELECT_BG = 0x1A2A1A;
    private static final int CURSOR = 0x22CC22;

    private static final String SFX_CURSOR_MOVE = "/assets/audio/sfx/cursor-move.mp3";
    private static final String SFX_CURSOR_ENTER = "/assets/audio/sfx/cursor-enter.mp3";
    private static final String SFX_ACCEPT = "/assets/audio/sfx/accept.mp3";

    private static final long GRUB_TIMEOUT_MS = 6000L;
    private static final long LOG_LINE_INTERVAL_MS = 120L;
    private static final long LOG_COMPLETE_HOLD_MS = 900L;

    // Future story hooks can replace these constants.
    private static final boolean AUTO_BOOT_ENABLED = true;
    private static final int AUTO_BOOT_INDEX = 0;

    private static final Entry[] ENTRIES = {
            new Entry("TTY7", "Boot the hidden seventh terminal", Route.START),
            new Entry("TTY7 (Live Environment)", "Run diagnostics without mounting user session", Route.DIAGNOSTICS),
            new Entry("Power Off", "Leave the reused disk alone for now", Route.EXIT)
    };

    private static final List<String> START_BOOT_LOG = List.of(
            "[    0.000000] Linux version 6.6.31-arch1-1 (tty7@boot)",
            "[    0.024913] Command line: root=/dev/mapper/tty7 rw quiet splash",
            "[    0.118403] tty7: probing reused sectors...",
            "[  OK  ] Mounted /mnt/undef/data",
            "[  OK  ] Reached target Local File Systems.",
            "[ WARN ] hidden-room.mount: journal disabled by previous owner",
            "[  OK  ] Started systemd-udevd.service",
            "[  OK  ] Started load-random-seed.service",
            "[ INFO ] grub: timeout reached, selecting entry 'TTY7'",
            "[  OK  ] Started someone-wants-to-talk.service",
            "[ INFO ] note: You didn't find me. You just happened to live here now.",
            "[  OK  ] Started blocks-runtime-prewarm.service",
            "[ WARN ] session state marked dirty",
            "[  OK  ] Started getty@tty7.service",
            "[ INFO ] /home/.tty7/root: recovered with minor corruption",
            "[ INFO ] log/0004: Probably tired.",
            "[  OK  ] Reached target Multi-User System.",
            "[  OK  ] Launching tty7 login menu"
    );

    private static final List<String> LIVE_BOOT_LOG = List.of(
            "[    0.000000] Linux version 6.6.31-arch1-1 (tty7@boot)",
            "[    0.024913] Command line: root=/dev/mapper/tty7 rw quiet nomodeset",
            "[    0.118403] tty7: probing reused sectors...",
            "[  OK  ] Mounted /mnt/undef/data (read-only)",
            "[  OK  ] Reached target Local File Systems.",
            "[ WARN ] hidden-room.mount: journal disabled by previous owner",
            "[  OK  ] Started systemd-udevd.service",
            "[  OK  ] Started display-diagnostics.service",
            "[ INFO ] grub: live environment selected by operator",
            "[ INFO ] note: some tests are for the screen, some are for me.",
            "[  OK  ] Started tty7-live.service",
            "[ WARN ] user session mount skipped",
            "[  OK  ] Launching live environment"
    );

    private final GlitchEffect glitchEffect = new GlitchEffect();

    public record Model(
            Phase phase,
            long phaseStartedAtMillis,
            int selectedIndex,
            boolean timeoutEnabled,
            Route bootRoute,
            int visibleLogLines,
            boolean transitionDispatched) {

        public static Model init() {
            return new Model(
                    Phase.GRUB,
                    System.currentTimeMillis(),
                    AUTO_BOOT_INDEX,
                    AUTO_BOOT_ENABLED,
                    ENTRIES[AUTO_BOOT_INDEX].route(),
                    0,
                    false);
        }
    }

    public enum Phase {
        GRUB,
        BOOT_LOG
    }

    private enum Route {
        START,
        DIAGNOSTICS,
        EXIT
    }

    private record Entry(String label, String description, Route route) {
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
            if (model.phase() == Phase.GRUB) {
                return handleGrubIntent(model, intent);
            }
            return handleBootLogIntent(model, intent);
        }

        if (msg instanceof Msg.Tick(long nowMillis)) {
            if (model.phase() == Phase.GRUB) {
                return handleGrubTick(model, nowMillis);
            }
            return handleBootTick(model, nowMillis);
        }

        return new UpdateResult<>(model, List.of());
    }

    @Override
    public RenderFrame view(Model model, TerminalBuffer buffer, long nowMillis) {
        buffer.clear();
        return switch (model.phase()) {
            case GRUB -> renderGrub(model, buffer, nowMillis);
            case BOOT_LOG -> renderBootLog(model, buffer, nowMillis);
        };
    }

    private UpdateResult<Model, Cmd> handleGrubIntent(Model model, InputIntent intent) {
        if (intent instanceof InputIntent.NavigatePrev) {
            int next = model.selectedIndex() == 0 ? ENTRIES.length - 1 : model.selectedIndex() - 1;
            Model nextModel = new Model(Phase.GRUB, model.phaseStartedAtMillis(), next, false,
                    ENTRIES[next].route(), 0, false);
            return new UpdateResult<>(nextModel, List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.NavigateNext) {
            int next = (model.selectedIndex() + 1) % ENTRIES.length;
            Model nextModel = new Model(Phase.GRUB, model.phaseStartedAtMillis(), next, false,
                    ENTRIES[next].route(), 0, false);
            return new UpdateResult<>(nextModel, List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.Cancel) {
            int next = AUTO_BOOT_INDEX;
            Model nextModel = new Model(Phase.GRUB, System.currentTimeMillis(), next, AUTO_BOOT_ENABLED,
                    ENTRIES[next].route(), 0, false);
            return new UpdateResult<>(nextModel, List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.Submit) {
            return startSelectedEntry(model, System.currentTimeMillis());
        }

        return new UpdateResult<>(model, List.of());
    }

    private UpdateResult<Model, Cmd> handleBootLogIntent(Model model, InputIntent intent) {
        if (intent instanceof InputIntent.Submit || intent instanceof InputIntent.Cancel) {
            List<Cmd> commands = new ArrayList<>();
            commands.add(new Cmd.PlaySound(SFX_ACCEPT));
            commands.add(routeToCommand(model.bootRoute()));
            Model nextModel = new Model(model.phase(), model.phaseStartedAtMillis(), model.selectedIndex(),
                    model.timeoutEnabled(), model.bootRoute(), bootLines(model.bootRoute()).size(), true);
            return new UpdateResult<>(nextModel, commands);
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

    private UpdateResult<Model, Cmd> handleBootTick(Model model, long nowMillis) {
        List<String> lines = bootLines(model.bootRoute());
        long elapsed = Math.max(0L, nowMillis - model.phaseStartedAtMillis());
        int revealed = Math.min(lines.size(), (int) (elapsed / LOG_LINE_INTERVAL_MS) + 1);

        Model nextModel = model;
        if (revealed != model.visibleLogLines()) {
            nextModel = new Model(model.phase(), model.phaseStartedAtMillis(), model.selectedIndex(),
                    model.timeoutEnabled(), model.bootRoute(), revealed, model.transitionDispatched());
        }

        long totalRevealDuration = Math.max(0L, (long) lines.size() * LOG_LINE_INTERVAL_MS);
        if (!nextModel.transitionDispatched() && elapsed >= totalRevealDuration + LOG_COMPLETE_HOLD_MS) {
            List<Cmd> commands = new ArrayList<>();
            commands.add(new Cmd.PlaySound(SFX_ACCEPT));
            commands.add(routeToCommand(nextModel.bootRoute()));
            Model doneModel = new Model(nextModel.phase(), nextModel.phaseStartedAtMillis(), nextModel.selectedIndex(),
                    nextModel.timeoutEnabled(), nextModel.bootRoute(), lines.size(), true);
            return new UpdateResult<>(doneModel, commands);
        }

        return new UpdateResult<>(nextModel, List.of());
    }

    private UpdateResult<Model, Cmd> startSelectedEntry(Model model, long nowMillis) {
        Entry entry = ENTRIES[model.selectedIndex()];
        if (entry.route() == Route.EXIT) {
            return new UpdateResult<>(model, List.of(
                    new Cmd.PlaySound(SFX_CURSOR_ENTER),
                    new Cmd.Exit()));
        }

        Model nextModel = new Model(Phase.BOOT_LOG, nowMillis, model.selectedIndex(), false, entry.route(), 0, false);
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
                    List.of(new UiEffect.Crt(0.30f)));
        }

        int boxWidth = Math.min(86, Math.max(24, cols - 4));
        int boxHeight = Math.min(Math.max(12, ENTRIES.length + 9), Math.max(8, rows - 2));
        int boxX = Math.max(0, (cols - boxWidth) / 2);
        int boxY = Math.max(0, (rows - boxHeight) / 2);

        PanelComponent.drawBoxWithTitle(buffer, boxX, boxY, boxWidth, boxHeight, " GNU GRUB 2.12-tty7 ", BORDER,
                PANEL_BG, FG);

        int contentX = boxX + 3;
        int contentWidth = Math.max(1, boxWidth - 6);
        int row = boxY + 2;

        printClipped(buffer, contentX, row++, contentWidth,
                "Reused disk detected. Select the entry to boot.", DIM_FG, PANEL_BG);
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
        Entry selectedEntry = ENTRIES[model.selectedIndex()];
        printClipped(buffer, contentX, row++, contentWidth, selectedEntry.description(), WARN, PANEL_BG);

        String timeoutLine;
        if (model.timeoutEnabled()) {
            long remaining = Math.max(0L, GRUB_TIMEOUT_MS - (nowMillis - model.phaseStartedAtMillis()));
            long seconds = Math.max(0L, (remaining + 999L) / 1000L);
            timeoutLine = "The highlighted entry will boot automatically in " + seconds + "s.";
        } else {
            timeoutLine = "Autoboot paused. Press Esc to restore timeout.";
        }
        printClipped(buffer, contentX, boxY + boxHeight - 3, contentWidth, timeoutLine, FG, PANEL_BG);
        printClipped(buffer, contentX, boxY + boxHeight - 2, contentWidth,
                "Enter=boot  Up/Down=navigate  Esc=restore timeout", DIM_FG, PANEL_BG);

        List<UiEffect> effects = List.of(new UiEffect.Crt(0.30f));
        CursorState cursor = new CursorState(cursorCol, cursorRow, true, true, CURSOR);
        return new RenderFrame(buffer, cursor, effects);
    }

    private RenderFrame renderBootLog(Model model, TerminalBuffer buffer, long nowMillis) {
        int cols = buffer.cols();
        int rows = buffer.rows();
        if (cols < 20 || rows < 6) {
            printClipped(buffer, 0, 0, Math.max(1, cols), "booting " + bootLabel(model.bootRoute()), ACCENT, BG);
            return new RenderFrame(buffer, null, List.of(new UiEffect.Crt(0.32f)));
        }

        List<String> lines = bootLines(model.bootRoute());
        int visibleCount = Math.max(0, Math.min(model.visibleLogLines(), lines.size()));
        int topRow = 1;
        int bottomRow = Math.max(topRow, rows - 3);
        int availableRows = Math.max(1, bottomRow - topRow + 1);
        int firstVisible = Math.max(0, visibleCount - availableRows);

        printClipped(buffer, 1, 0, Math.max(1, cols - 2), ":: tty7 boot sequence / " + bootLabel(model.bootRoute()),
                ACCENT, BG);

        int drawRow = topRow;
        for (int i = firstVisible; i < visibleCount && drawRow <= bottomRow; i++) {
            String line = lines.get(i);
            printBootLine(buffer, 1, drawRow++, Math.max(1, cols - 2), line);
        }

        boolean complete = visibleCount >= lines.size();
        String spinner = spinnerFrame(nowMillis);
        String status = complete
                ? "[  OK  ] handoff complete, opening " + bootLabel(model.bootRoute()).toLowerCase()
                : "[ INFO ] booting " + bootLabel(model.bootRoute()).toLowerCase() + " " + spinner;
        printClipped(buffer, 1, rows - 2, Math.max(1, cols - 2), status, complete ? ACCENT : FG, BG);
        printClipped(buffer, 1, rows - 1, Math.max(1, cols - 2),
                complete ? "Press Enter to continue immediately" : "Enter=skip animation", DIM_FG, BG);

        List<UiEffect> effects = new ArrayList<>();
        effects.add(new UiEffect.Crt(0.32f));
        UiEffect.Glitch glitch = glitchEffect.update(nowMillis);
        if (glitch != null) {
            effects.add(glitch);
        }

        int cursorCol = Math.min(cols - 1, Math.max(0, status.length() + 1));
        CursorState cursor = new CursorState(cursorCol, rows - 2, !complete, true, CURSOR);
        return new RenderFrame(buffer, cursor, effects);
    }

    private void printBootLine(TerminalBuffer buffer, int x, int y, int width, String line) {
        int fg = FG;
        if (line.contains("[ WARN ]")) {
            fg = WARN;
        } else if (line.contains("[ INFO ]")) {
            fg = DIM_FG;
        } else if (line.contains("[    ")) {
            fg = FG;
        }
        printClipped(buffer, x, y, width, line, fg, BG);
    }

    private List<String> bootLines(Route route) {
        return route == Route.DIAGNOSTICS ? LIVE_BOOT_LOG : START_BOOT_LOG;
    }

    private Cmd routeToCommand(Route route) {
        return switch (route) {
            case START -> new Cmd.OpenStart();
            case DIAGNOSTICS -> new Cmd.OpenDiagnostics();
            case EXIT -> new Cmd.Exit();
        };
    }

    private String bootLabel(Route route) {
        return switch (route) {
            case START -> "TTY7";
            case DIAGNOSTICS -> "TTY7 Live Environment";
            case EXIT -> "Power Off";
        };
    }

    private String spinnerFrame(long nowMillis) {
        String[] frames = { "-", "\\", "|", "/" };
        int index = (int) ((nowMillis / 120L) % frames.length);
        return frames[index];
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
        if (TextUtil.getDisplayWidth(content) > safeWidth) {
            if (safeWidth >= 2) {
                content = content.substring(0, Math.max(0, safeWidth - 1)) + ".";
            } else if (!content.isEmpty()) {
                content = content.substring(0, 1);
            }
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
