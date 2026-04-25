package com.tty7.gl.ui.pages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.tty7.gl.input.intent.InputIntent;
import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.cursor.CursorState;
import com.tty7.gl.renderer.effect.UiEffect;
import com.tty7.gl.ui.components.PanelComponent;
import com.tty7.gl.ui.components.CMatrixComponent;
import com.tty7.gl.ui.effect.GlitchEffect;
import com.tty7.gl.ui.tea.Program;
import com.tty7.gl.ui.tea.UpdateResult;
import com.tty7.gl.utils.TextUtil;

public class StartPage implements Program<StartPage.Model, StartPage.Msg, StartPage.Cmd> {
    private static final String SFX_CURSOR_MOVE = "/assets/audio/sfx/cursor-move.mp3";
    private static final String SFX_CURSOR_ENTER = "/assets/audio/sfx/cursor-enter.mp3";

    private static final int BG = 0x0D1117;
    private static final int PANEL_BG = BG;
    private static final int BORDER = 0x555555;
    private static final int CURSOR = 0x22CC22;
    private static final int NORMAL_TEXT = 0x8B949E;
    private static final int HOVER_TEXT = 0xFFFFFF;
    private static final int ACCENT = 0x22CC22;
    private static final int WARN = 0xE3B341;
    private static final String TITLE_ASCII = "/assets/titles/ascii_title_chunky.txt";

    private static final String[] OPTIONS = { "Login", "Reboot", "Power Off" };
    private static final String[] DESCRIPTIONS = {
            "Open tty7 and begin the actual session.",
            "Run the shutdown sequence, then return to GRUB.",
            "Display the shutdown guide once and exit the process."
    };

    private final GlitchEffect glitchEffect = new GlitchEffect();
    private final CMatrixComponent cmatrix = new CMatrixComponent();
    private final String[] titleArt = loadTitleArt();

    public record Model(int selectedIndex) {
        public static Model init() {
            return new Model(0);
        }
    }

    public sealed interface Msg {
        record Intent(InputIntent intent) implements Msg {
        }
    }

    public sealed interface Cmd {
        record Login() implements Cmd {
        }

        record Reboot() implements Cmd {
        }

        record PowerOff() implements Cmd {
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

        if (intent instanceof InputIntent.NavigatePrev) {
            int next = model.selectedIndex() == 0 ? OPTIONS.length - 1 : model.selectedIndex() - 1;
            return new UpdateResult<>(new Model(next), List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.NavigateNext) {
            int next = (model.selectedIndex() + 1) % OPTIONS.length;
            return new UpdateResult<>(new Model(next), List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.Cancel) {
            return new UpdateResult<>(new Model(0), List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
        }

        if (intent instanceof InputIntent.Submit) {
            return switch (model.selectedIndex()) {
                case 0 -> new UpdateResult<>(model, List.of(new Cmd.Login(), new Cmd.PlaySound(SFX_CURSOR_ENTER)));
                case 1 -> new UpdateResult<>(model, List.of(new Cmd.Reboot(), new Cmd.PlaySound(SFX_CURSOR_ENTER)));
                case 2 -> new UpdateResult<>(model, List.of(new Cmd.PowerOff(), new Cmd.PlaySound(SFX_CURSOR_ENTER)));
                default -> new UpdateResult<>(model, List.of(new Cmd.PlaySound(SFX_CURSOR_MOVE)));
            };
        }

        return new UpdateResult<>(model, List.of());
    }

    @Override
    public RenderFrame view(Model model, TerminalBuffer buffer, long nowMillis) {
        buffer.clear();
        
        cmatrix.update(buffer.cols(), buffer.rows(), nowMillis);
        cmatrix.render(buffer);

        int rows = buffer.rows();
        int cols = buffer.cols();

        if (cols < 36 || rows < 12) {
            buffer.print(1, 1, "tty7 gdm", HOVER_TEXT, BG);
            buffer.print(1, 3, OPTIONS[model.selectedIndex()], ACCENT, BG);
            return new RenderFrame(buffer, new CursorState(1, 3, true, true, CURSOR),
                    List.of(new UiEffect.Crt(0.15f)));
        }

        int titleHeight = titleArt != null ? titleArt.length : 1;
        int maxOptLen = 0;
        for (String option : OPTIONS) {
            maxOptLen = Math.max(maxOptLen, TextUtil.getDisplayWidth(option));
        }
        
        int boxWidth = maxOptLen + 12;
        int boxHeight = OPTIONS.length * 2 + 3;
        
        int totalHeight = titleHeight + 4 + boxHeight + 4;
        int startY = Math.max(2, (rows - totalHeight) / 2);
        
        int row = startY;
        if (titleArt != null) {
            for (String line : titleArt) {
                int x = Math.max(0, (cols - TextUtil.getDisplayWidth(line)) / 2);
                buffer.print(x, row++, line, ACCENT, BG);
            }
        } else {
            String title = "TTY7";
            int x = Math.max(0, (cols - TextUtil.getDisplayWidth(title)) / 2);
            buffer.print(x, row++, title, ACCENT, BG);
        }
        
        row += 2;
        String info1 = "Display manager is ready.";
        String info2 = "User: root  Seat: tty7";
        buffer.print(Math.max(0, (cols - TextUtil.getDisplayWidth(info1)) / 2), row++, info1, NORMAL_TEXT, BG);
        buffer.print(Math.max(0, (cols - TextUtil.getDisplayWidth(info2)) / 2), row++, info2, NORMAL_TEXT, BG);

        row += 2;
        int boxX = Math.max(0, (cols - boxWidth) / 2);
        int boxY = row;
        
        PanelComponent.drawBoxWithTitle(buffer, boxX, boxY, boxWidth, boxHeight, " Session ", BORDER, PANEL_BG, HOVER_TEXT);
        
        int[] cursorInfo = PanelComponent.drawLeftAlignedOptions(buffer, boxX, boxWidth, boxY + 2,
                OPTIONS, model.selectedIndex(), 2, 2, NORMAL_TEXT, HOVER_TEXT, PANEL_BG);

        int descRow = boxY + boxHeight + 1;
        String desc = DESCRIPTIONS[model.selectedIndex()];
        buffer.print(Math.max(0, (cols - TextUtil.getDisplayWidth(desc)) / 2), descRow, desc, WARN, BG);
        
        String navHint = "Enter=select  Up/Down=navigate";
        int navX = Math.max(0, (cols - TextUtil.getDisplayWidth(navHint)) / 2);
        buffer.print(navX, rows - 2, navHint, DIM_FG(), BG);

        List<UiEffect> effects = new ArrayList<>();
        effects.add(new UiEffect.Crt(0.15f));
        UiEffect.Glitch glitch = glitchEffect.update(nowMillis);
        if (glitch != null) {
            effects.add(glitch);
        }

        return new RenderFrame(buffer, new CursorState(cursorInfo[0], cursorInfo[1], true, true, CURSOR), effects);
    }
    
    private static int DIM_FG() {
        return 0x555555;
    }

    private static String[] loadTitleArt() {
        try (InputStream is = StartPage.class.getResourceAsStream(TITLE_ASCII)) {
            if (is == null) {
                return null;
            }
            String[] content = new String(is.readAllBytes(), StandardCharsets.UTF_8).split("\r?\n");
            List<String> lines = new ArrayList<>(List.of(content));
            trimTrailingEmptyLines(lines);
            return lines.isEmpty() ? null : lines.toArray(String[]::new);
        } catch (IOException e) {
            return null;
        }
    }

    private static void trimTrailingEmptyLines(List<String> lines) {
        int last = lines.size() - 1;
        while (last >= 0 && lines.get(last).trim().isEmpty()) {
            lines.remove(last--);
        }
    }

    private static void printClipped(TerminalBuffer buffer, int x, int y, int width, String text, int fg, int bg) {
        if (width <= 0 || y < 0 || y >= buffer.rows()) {
            return;
        }
        String content = text == null ? "" : text;
        while (TextUtil.getDisplayWidth(content) > width && !content.isEmpty()) {
            content = content.substring(0, content.length() - 1);
        }
        buffer.print(x, y, content, fg, bg);
    }
}
