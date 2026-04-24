# AlgoBlock 剧情系统技术实现指南：基于《TTY7》叙事框架

本指南旨在指导开发者如何将《TTY7》的终端交互式剧情深度整合进 AlgoBlock 项目。我们将利用项目现有的 **TEA (The Elm Architecture)** 架构，通过最小化侵入的方式实现一套沉浸式的叙事系统。

---

## 1. 核心设计理念：终端即叙事 (Terminal as Narrative)
《TTY7》的剧情核心在于“二手硬盘中的幽灵”。为了配合这一主题，AlgoBlock 的 UI 应当从“解谜工具”转变为“遗留系统终端”。
- **视觉风格**：保留并强化 CRT 扫描线、Glitch 故障效果。
- **交互方式**：模拟 Linux 终端行为（`cat`, `ls`, `rm`），在谜题关卡之间穿插命令行交互。

---

## 2. 数据结构重构 (Data Architecture)

### 2.1 扩展 Level 模型
在 `game-core` 模块的 `Level.java` 中，我们需要将单一的 `story` 字符串升级为结构化的剧情定义。

```java
// 修改后的 Level.java (Record 扩展)
public record Level(
    // ... 原有字段
    Narrative narrative // 新增剧情对象
) {}

public record Narrative(
    List<DialogueLine> preStory,  // 关卡开始前的对话/日志
    List<DialogueLine> postStory, // 关卡过关后的反馈
    List<Choice> choices          // 剧情分支选择（可选）
) {}

public record DialogueLine(
    String speaker, // "System", "Unknown", "User"
    String text,    // 剧情文本
    String effect,  // "glitch", "typewriter", "instant"
    String sfx      // 触发音效路径
) {}
```

### 2.2 剧情文本外置化
建议在 `game-core/src/main/resources/narrative/` 目录下建立 `tty7_script.json`，统一管理长篇文本，通过 `LevelLoader` 关联到对应关卡。

---

## 3. UI 组件开发 (UI Components)

### 3.1 `DialogueOverlay` (对话浮层)
在 `game-gl` 中新增组件，用于在 `GamePage` 之上渲染对话。

**核心逻辑 (DialogueComponent.java):**
```java
public void view(Model model, TerminalBuffer buffer) {
    // 绘制类似 TTY7 风格的边框
    PanelComponent.drawBoxWithTitle(buffer, x, y, w, h, " " + model.currentSpeaker() + " ", ...);
    
    // 实现逐字打印效果
    int visibleChars = (int)((now - model.startTime) * model.typingSpeed);
    String displayedText = model.fullText().substring(0, Math.min(visibleChars, model.fullText().length()));
    
    TextUtil.printWrapped(buffer, x + 2, y + 2, w - 4, displayedText, ...);
}
```

### 3.2 `TerminalEmulatorPage` (模拟终端页)
为了实现《TTY7》序章中 `Login (root)` 的效果，我们需要一个新的页面类型。

**AppModel.Screen 扩展:**
```java
public enum Screen {
    START,
    BOOT_SEQUENCE, // 新增：开机自检剧情
    TERMINAL_SIM,  // 新增：模拟命令行交互
    GAME,
    DIAGNOSTICS
}
```

---

## 4. 流程控制与状态机 (State Machine)

### 4.1 剧情触发链路
修改 `AppProgram.update` 逻辑，在关卡切换时插入剧情判断。

```java
if (msg instanceof AppMsg.SubmitFinished sf && sf.result().accepted()) {
    // 检查当前关卡是否有 postStory
    if (model.currentLevel().narrative().hasPostStory()) {
        // 切换到剧情展示状态，而不是直接加载下一关
        return new UpdateResult<>(model.withScreen(Screen.STORY_DISPLAY), ...);
    }
}
```

### 4.2 交互式分支实现
针对《TTY7》中的分支选择（如：`1. cat log`, `2. rm log`），在 `TerminalSimPage` 中捕获数字键输入：
- **分支 1**: 展示更多日志文本。
- **分支 2**: 触发 `GlitchEffect`，并清空当前 `AppModel` 中的日志缓存，模拟“删除”效果。

---

## 5. 视觉与音效增强 (VFX & SFX)

### 5.1 动态效果映射
根据《TTY7》文本中的描述，实时触发 `game-gl` 的特效：
- **"机箱风扇转了三次"**: 配合 `electronic-buzz.mp3` 循环播放。
- **"屏幕黑了，又亮了"**: 调用 `EffectsRenderer` 的 `DimEffect` 进行全屏闪烁。
- **"绿色的荧光"**: 修改 `TextRenderer` 的默认前景颜色为 `0x00FF00`。

### 5.2 剧情特有音效
- `sfx/boot.mp3`: 序章开机声。
- `sfx/typewriter.mp3`: 文本刷出声。
- `sfx/confirm.mp3`: 分支选择确认声。

---

## 6. 实施建议 (Implementation Roadmap)

1.  **资源准备**: 将 `pasted_content.txt` 中的文本按照关卡（Level 01 - 30）进行切分，存入 `narrative.json`。
2.  **原型搭建**: 先在 `GamePage` 中实现一个简单的“点击继续”对话框。
3.  **沉浸化改造**: 实现 `TerminalSimPage`，将游戏启动流程改为从 `Login (root)` 开始。
4.  **分支集成**: 实现第一章的三个分支逻辑，并观察对后续关卡 `AppModel` 的持久化影响。

---

*文档版本：v1.2 (TTY7-Aligned)*
*日期：2026-04-24*
