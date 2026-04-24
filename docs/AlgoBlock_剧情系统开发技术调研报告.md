# AlgoBlock 剧情系统开发技术调研报告

## 1. 项目现状分析

### 1.1 核心架构
AlgoBlock 目前采用 **TEA (The Elm Architecture)** 风格的模块化架构，主要分为三个模块：
- **game-api**: 定义了积木（Block）和求值上下文（EvalContext）的基础合约。
- **game-core**: 负责关卡加载、表达式解析、规则校验与评分逻辑。
- **game-gl**: 基于 LWJGL 的图形渲染层，管理页面状态机（StartPage, GamePage, DiagnosticsPage）。

### 1.2 剧情现状
当前项目在 `Level` 模型中已预留了 `story` 字段（String 类型），并在 `GamePage` 的左上角面板（Level Brief）中以纯文本形式展示。
- **优点**: 结构清晰，逻辑与渲染解耦，易于扩展。
- **不足**: 缺乏交互性，无法承载多角色对话、过场动画或分支剧情。

---

## 2. 剧情系统设计方案

为了提升游戏的沉浸感（类似《Hacknet》或《TIS-100》），建议从以下三个维度入手改造：

### 2.1 数据模型扩展 (Data Model)
建议将单一的 `story` 字符串扩展为结构化的 `Narrative` 对象。

**建议的 JSON 结构 (level-n.json):**
```json
"narrative": {
  "pre_story": [
    {"speaker": "System", "text": "正在初始化终端...", "effect": "glitch"},
    {"speaker": "Unknown", "text": "你能听到我吗？我们需要你破解这个序列。"}
  ],
  "post_story": [
    {"speaker": "Unknown", "text": "做得好。连接已加密。"}
  ]
}
```

### 2.2 表现层组件 (UI Components)
- **DialogueComponent**: 在 `GamePage` 中新增一个浮层组件，支持逐字打印（Typewriter）效果。
- **StoryPage**: 新增一个全屏页面类型，用于展示大段背景介绍或章节过渡。
- **TerminalLog**: 在 Workspace 面板中模拟系统日志输出，增加环境叙事。

### 2.3 流程控制 (Flow Control)
在 `AppProgram` 的状态转移逻辑中加入剧情状态：
1. `StartPage` -> `StoryPage` (开场剧情)
2. `StoryPage` -> `GamePage` (关卡挑战)
3. `GamePage` (通关) -> `StoryPage` (过场/对话) -> 下一关

---

## 3. 技术路线与修改建议

### 第一阶段：基础对话框架实现
1.  **修改 `game-core` 中的 `Level` Record**:
    - 增加 `List<DialogueLine> preStory` 等字段。
2.  **在 `game-gl` 中实现 `DialogueComponent`**:
    - 利用 `TerminalBuffer` 绘制对话框。
    - 使用 `nowMillis` 计算当前应显示的字符索引，实现逐字显示。
3.  **更新 `GamePage` 的 `view` 方法**:
    - 在渲染完基础面板后，若存在未处理剧情，则渲染 `DialogueComponent` 并暂停游戏逻辑。

### 第二阶段：过场动画与转场特效
1.  **新增 `StoryPage`**:
    - 仿照 `StartPage` 实现，专门用于展示全屏叙事。
    - 结合 `CrtEffect` 和 `GlitchEffect` 增强视觉冲击力。
2.  **修改 `AppProgram.update`**:
    - 拦截 `Msg.SubmitFinished`，若 `accepted == true`，先跳转至剧情展示状态，再进入下一关。

### 第三阶段：交互式剧情 (Lore)
1.  **扩展积木元数据**:
    - 给某些特定积木增加“背景描述”，当玩家第一次使用该积木时触发特殊对话。
2.  **动态关卡目标**:
    - 根据剧情进度，动态修改 `Level` 的 `discovery_hint`。

---

## 4. 关键代码修改点示例

### 修改 `AppModel.java` 增加页面状态
```java
public enum Screen {
    START,
    STORY, // 新增剧情页
    GAME,
    DIAGNOSTICS
}
```

### 修改 `GamePage.java` 渲染对话
```java
private void drawDialogue(TerminalBuffer buffer, String text, int progress) {
    int boxW = buffer.cols() - 10;
    int boxH = 5;
    int boxX = 5;
    int boxY = buffer.rows() - 7;
    PanelComponent.drawBoxWithTitle(buffer, boxX, boxY, boxW, boxH, " MESSAGE ", 0x00FF00, 0x000000, 0xFFFFFF);
    String visibleText = text.substring(0, Math.min(text.length(), progress));
    printWrapped(buffer, boxX + 2, boxY + 2, boxW - 4, boxY + boxH - 2, visibleText, 0x00FF00);
}
```

---

## 5. 总结
AlgoBlock 的架构非常适合加入剧情。通过在 `game-gl` 模块中引入新的 `StoryPage` 和在 `AppProgram` 中调整状态流转，可以以极小的侵入性实现一套完整的叙事系统。建议先从“关卡前对话”入手，利用现有的终端特效营造极客氛围。
