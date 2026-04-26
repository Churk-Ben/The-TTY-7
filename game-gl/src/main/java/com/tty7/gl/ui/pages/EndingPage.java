package com.tty7.gl.ui.pages;

import java.util.List;

import com.tty7.core.story.StoryDatabase;
import com.tty7.core.story.StoryNode;
import com.tty7.gl.input.intent.InputIntent;
import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.effect.UiEffect;
import com.tty7.gl.ui.tea.Program;
import com.tty7.gl.ui.tea.UpdateResult;

public class EndingPage implements Program<EndingPage.Model, EndingPage.Msg, EndingPage.Cmd> {
    private final StoryDatabase storyDb;

    public EndingPage(StoryDatabase storyDb) {
        this.storyDb = storyDb;
    }

    public Model start(String endingId) {
        StoryNode node = storyDb.getNode(endingId);
        return Model.start(node);
    }

    public record Model(StoryNode node, int lineIndex, boolean finished) {
        public static Model init() {
            return new Model(null, 0, false);
        }

        public static Model start(StoryNode node) {
            return new Model(node, 0, false);
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

        if (model.node() == null) {
            return new UpdateResult<>(model, List.of(new Cmd.ReturnToLogin()));
        }

        boolean advance = intent instanceof InputIntent.Submit
                || (intent instanceof InputIntent.TextTyped tt && (tt.value() == ' ' || tt.value() == '\n'));

        if (advance) {
            int nextIndex = model.lineIndex() + 1;
            List<String> lines = model.node().storyLines();
            if (nextIndex < lines.size()) {
                return new UpdateResult<>(new Model(model.node(), nextIndex, false),
                        List.of(new Cmd.PlaySound("/assets/audio/sfx/interact.mp3")));
            } else {
                return new UpdateResult<>(new Model(model.node(), nextIndex, true),
                        List.of(new Cmd.ReturnToLogin()));
            }
        }

        return new UpdateResult<>(model, List.of());
    }

    @Override
    public RenderFrame view(Model model, TerminalBuffer buffer, long nowMillis) {
        buffer.clear();
        int cols = buffer.cols();
        int rows = buffer.rows();

        if (model.node() != null && model.lineIndex() < model.node().storyLines().size()) {
            String line = model.node().storyLines().get(model.lineIndex());
            int x = Math.max(0, (cols - line.length() * 2) / 2);
            int y = rows / 2;
            buffer.print(x, y, line, 0xCDD9E5, 0x050709);

            if ((nowMillis / 500) % 2 == 0) {
                String prompt = "[Press Space]";
                buffer.print((cols - prompt.length()) / 2, rows - 2, prompt, 0x8B949E, 0x050709);
            }
        }

        return new RenderFrame(buffer, null, List.of(new UiEffect.Crt(0.15f)));
    }
}