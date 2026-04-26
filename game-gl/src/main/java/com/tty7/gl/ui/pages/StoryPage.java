package com.tty7.gl.ui.pages;

import java.util.List;

import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.ui.tea.Program;
import com.tty7.gl.ui.tea.UpdateResult;

public class StoryPage implements Program<StoryPage.Model, StoryPage.Msg, StoryPage.Cmd> {

    public record Model() {
        public static Model init() {
            return new Model();
        }
    }

    public sealed interface Msg {
        record Dummy() implements Msg {
        }
    }

    public sealed interface Cmd {
        record Dummy() implements Cmd {
        }
    }

    @Override
    public Model init() {
        return Model.init();
    }

    @Override
    public UpdateResult<Model, Cmd> update(Model model, Msg msg) {
        return new UpdateResult<>(model, List.of());
    }

    @Override
    public RenderFrame view(Model model, TerminalBuffer buffer, long nowMillis) {
        return null;
    }
}