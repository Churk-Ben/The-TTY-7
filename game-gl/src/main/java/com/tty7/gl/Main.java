package com.tty7.gl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import com.tty7.core.engine.BlockRegistry;
import com.tty7.core.engine.GameCoreService;
import com.tty7.core.levels.Level;
import com.tty7.core.levels.LevelLoader;
import com.tty7.gl.input.GlfwInputAdapter;
import com.tty7.gl.input.InputEventQueue;
import com.tty7.gl.input.event.InputEvent;
import com.tty7.gl.input.intent.InputIntentMapper;
import com.tty7.gl.input.intent.InputIntentQueue;
import com.tty7.gl.input.intent.IntentEnvelope;
import com.tty7.gl.renderer.core.RenderFrame;
import com.tty7.gl.renderer.core.TerminalBuffer;
import com.tty7.gl.renderer.cursor.CursorRenderer;
import com.tty7.gl.renderer.effect.EffectsRenderer;
import com.tty7.gl.renderer.text.FontAtlas;
import com.tty7.gl.renderer.text.TextRenderer;
import com.tty7.gl.services.CompletionService;
import com.tty7.gl.ui.app.AppCmd;
import com.tty7.gl.ui.app.AppCmdHandler;
import com.tty7.gl.ui.app.AppModel;
import com.tty7.gl.ui.app.AppMsg;
import com.tty7.gl.ui.app.AppProgram;
import com.tty7.gl.ui.pages.BootPage;
import com.tty7.gl.ui.pages.DiagnosticsPage;
import com.tty7.gl.ui.pages.GamePage;
import com.tty7.gl.ui.pages.StartPage;
import com.tty7.gl.ui.tea.TeaRuntime;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class Main {
    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to init GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        long window = glfwCreateWindow(1280, 720, "TheTTY 7", 0, 0);
        if (window == 0L) {
            throw new IllegalStateException("Failed to create window");
        }

        glfwSetWindowSizeLimits(
                window,
                960,
                540,
                GLFW_DONT_CARE,
                GLFW_DONT_CARE);

        AtomicInteger fbWidth = new AtomicInteger(1280);
        AtomicInteger fbHeight = new AtomicInteger(720);

        // Read initial size
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var w = stack.mallocInt(1);
            var h = stack.mallocInt(1);
            glfwGetFramebufferSize(window, w, h);
            fbWidth.set(w.get(0));
            fbHeight.set(h.get(0));
        }

        glfwSetFramebufferSizeCallback(window, (w, width, height) -> {
            fbWidth.set(width);
            fbHeight.set(height);
        });
        glfwShowWindow(window);
        glfwFocusWindow(window);

        LevelLoader levelLoader = new LevelLoader();
        List<Level> levels = levelLoader.loadRange(1, 10);
        BlockRegistry registry = new BlockRegistry();
        GameCoreService service = new GameCoreService(registry);

        BootPage bootPage = new BootPage();
        StartPage startPage = new StartPage();
        GamePage gamePage = new GamePage(new CompletionService(registry));
        DiagnosticsPage diagnosticsPage = new DiagnosticsPage();
        AppProgram program = new AppProgram(bootPage, startPage, gamePage, diagnosticsPage, levels);
        AppCmdHandler cmdHandler = new AppCmdHandler(service);

        TeaRuntime<AppModel, AppMsg, AppCmd> uiRuntime = new TeaRuntime<>(program, cmdHandler);
        InputEventQueue eventQueue = new InputEventQueue();
        InputIntentQueue intentQueue = new InputIntentQueue();
        GlfwInputAdapter inputAdapter = new GlfwInputAdapter(eventQueue);
        inputAdapter.attach(window);

        Thread logicThread = new Thread(() -> {
            while (!glfwWindowShouldClose(window)) {
                try {
                    InputEvent event = eventQueue.take();
                    long nowMillis = System.currentTimeMillis();
                    List<IntentEnvelope> mappedIntents = InputIntentMapper.map(event, nowMillis);
                    for (IntentEnvelope envelope : mappedIntents) {
                        intentQueue.offer(envelope);
                    }

                    for (int i = 0; i < mappedIntents.size(); i++) {
                        IntentEnvelope envelope = intentQueue.take();
                        if (!envelope.isExpired(System.currentTimeMillis())) {
                            uiRuntime.dispatch(new AppMsg.Intent(envelope.intent()));
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "game-logic");
        logicThread.setDaemon(true);
        logicThread.start();

        Thread renderThread = new Thread(() -> {
            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            GL.createCapabilities();

            TerminalBuffer uiBuffer = new TerminalBuffer(120, 40);

            FontAtlas fontAtlas = new FontAtlas(resolveFontPath(), 24, 1024, 1024);
            TextRenderer textRenderer = new TextRenderer(fontAtlas);
            CursorRenderer cursorRenderer = new CursorRenderer();
            EffectsRenderer effectsRenderer = new EffectsRenderer();

            while (!glfwWindowShouldClose(window)) {
                long nowMillis = System.currentTimeMillis();
                AppModel snapshot = uiRuntime.snapshotModel();
                if (snapshot.screen() == AppModel.Screen.BOOT) {
                    uiRuntime.dispatch(new AppMsg.Tick(nowMillis));
                }

                AppModel model = uiRuntime.snapshotModel();
                boolean isFontDiag = model.screen() == AppModel.Screen.DIAGNOSTICS &&
                        model.diagnosticsModel().state() == DiagnosticsPage.State.FONT_DIAGNOSTIC;
                textRenderer.setFontDiagnosticMode(isFontDiag);

                int viewportW = fbWidth.get();
                int viewportH = fbHeight.get();
                if (viewportW > 0 && viewportH > 0) {
                    textRenderer.setViewport(viewportW, viewportH);
                    int dynamicCols = textRenderer.visibleCols();
                    int dynamicRows = textRenderer.visibleRows();
                    if (uiBuffer.cols() != dynamicCols || uiBuffer.rows() != dynamicRows) {
                        uiBuffer = new TerminalBuffer(dynamicCols, dynamicRows);
                    }

                    glClearColor(0.05f, 0.07f, 0.09f, 1f);
                    glClear(GL_COLOR_BUFFER_BIT);

                    RenderFrame uiFrame = uiRuntime.render(uiBuffer, nowMillis);
                    TerminalBuffer renderBuffer = uiFrame != null ? uiFrame.textBuffer() : uiBuffer;

                    textRenderer.upload(renderBuffer);
                    textRenderer.draw();
                    if (uiFrame != null) {
                        cursorRenderer.draw(uiFrame, textRenderer, glfwGetTime());
                        effectsRenderer.draw(uiFrame, textRenderer, glfwGetTime());
                    }
                    glfwSwapBuffers(window);
                }
            }
        }, "game-render");
        renderThread.start();

        while (!glfwWindowShouldClose(window)) {
            glfwWaitEventsTimeout(0.1);
            glfwSetWindowTitle(window, "tty7");
        }

        try {
            renderThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        uiRuntime.close();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private static String resolveFontPath() {
        return "assets/fonts/MapleMono-NF-CN-unhinted/MapleMono-NF-CN-Regular.ttf";
    }
}
