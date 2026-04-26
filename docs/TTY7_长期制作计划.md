# TTY7 长期制作计划

> 版本：v0.1  
> 依据：`docs/TTY7.md`、`docs/TTY7_v2.md`，并结合当前仓库已实现的 `game-core` / `game-gl` 架构整理  
> 目标：把《TTY7》从“完整文本设定”推进到“可持续制作、可验证、可发布”的终端叙事解谜游戏

---

## 1. 项目结论

### 1.1 正典建议

两份文档各自承担不同职责，建议不要二选一，而是合并为同一条制作主线：

- `TTY7.md` 作为“剧情全量库”
  - 优势是内容完整，包含一周目、二周目、三周目、多个结局、系统散落文本、隐藏碎片和更细的情绪层次。
- `TTY7_v2.md` 作为“演出与节奏参考稿”
  - 优势是结构更紧凑、场景推进更清晰、单章内部更适合直接做成可玩的节点。

### 1.2 推荐采用的最终版本

建议以 `TTY7.md` 的三周目结构为主骨架，以 `TTY7_v2.md` 的节奏、章节长度、演出密度为落地标准。

最终正典如下：

- 主角是“买到二手硬盘的现代用户”。
- 前主人公是 `Sera Leung`。
- 被反复提及的“她”是 `Livia`。
- 核心谜题游戏是 `BLOCKS`。
- `TTY7` 不是普通菜单，而是“第七个终端”，代表一次被延迟的对话。
- `.keep` 是全作最重要的情感符号之一，代表“没有删除”“愿意留下”。
- 真结局中的 AI 必须明确不是复活，不是灵魂转移，而是 `lossy compression of her words`。

### 1.3 产品一句话定位

《TTY7》是一款终端叙事解谜游戏：玩家在一块被重复利用的二手硬盘中，逐步拼出一位开发者未完成的谜题游戏、她未寄出的告别，以及一段关于“留下些什么才算没有消失”的故事。

---

## 2. 制作原则

### 2.1 四个核心支柱

1. 终端即叙事空间  
   玩家不是在“读小说”，而是在“操作一台仍残留他人痕迹的系统”。

2. 谜题即情书  
   `BLOCKS` 不是附属小游戏，而是 Sera 留给 Livia 的表达方式。

3. 分支要有记忆，而不是岔路树  
   本作应避免指数级分支爆炸，采用“少量关键选择 + 长期状态记忆 + 结局分流”的方式。

4. 真结局必须克制  
   三周目 AI 只承担“回声”功能，不能破坏前两周目建立起来的失去感。

### 2.2 范围边界

本作建议坚持以下边界，避免失控：

- 不做开放世界式命令行沙盒，只实现白名单命令。
- 不做大量复杂数值成长，核心始终是叙事 + 表达式谜题。
- 不做拟真的 AI 聊天系统，真结局对话应以受控脚本和有限问答为主。
- 不把故事拆成海量小分支，而是做“主线收束 + 少量关键分歧”。

---

## 3. 长期开发路线图

建议总周期按 12 到 15 个月规划，先做能打动人的垂直切片，再补完多周目结构。

| 阶段 | 周期 | 核心目标 | 主要交付物 |
| --- | --- | --- | --- |
| Phase 0 预制作 | 2-3 周 | 定正典、定数据结构、定体验目标 | 剧情圣经、分支旗标表、关卡字段扩展方案 |
| Phase 1 垂直切片 | 4-6 周 | 做出“开机 -> 登录 -> 日志 -> 3 关 BLOCKS -> .keep”完整体验 | 可试玩 Demo、基础存档、基础演出 |
| Phase 2 一周目 Alpha | 6-8 周 | 补完一周目全部章节与早期结局 | 一周目全流程、结局 1/2/3、日志/任务/记录系统 |
| Phase 3 二周目 Alpha | 6-8 周 | 实现 Sera 视角、关卡 06-10、`.s` 解锁与导出数据 | 二周目全流程、结局 4、核心秘密文件 |
| Phase 4 三周目/真结局 | 4-6 周 | 实现 `tty7_ai.so`、真结局问答、最终视觉收束 | 三周目全流程、结局 5、最终桌面回归 |
| Phase 5 抛光与验证 | 4-6 周 | 打磨文本、音效、节奏、无障碍、兼容性 | RC 版本、测试报告、发布包 |
| Phase 6 发布后内容 | 4+ 周 | 做挑战模式、注释模式、开发者注解 | 扩展关卡、幕后模式、社区支持 |

### 3.1 Phase 0：预制作

目标不是写代码，而是消除后续返工。

必须完成：

- 确认剧情正典版本。
- 把所有关键节点编号，例如 `boot.login_root`、`loop1.log.read_0004`、`loop2.secret.read_medical`。
- 定义全局旗标与结局条件。
- 确认关卡数量：
  - 主线叙事关卡 10 关。
  - 额外挑战/教学/诊断关卡 20 关，用来对齐当前引擎已有 `1..30` 的资源加载结构。
- 产出一份“命令白名单”：
  - `ls`
  - `cat`
  - `cd`
  - `grep`
  - `touch`
  - `shutdown`
  - 有条件开放的 `rm`

### 3.2 Phase 1：垂直切片

垂直切片必须先证明三件事：

- 终端叙事是有沉浸感的。
- `BLOCKS` 谜题和剧情互相加分。
- 玩家会因为 `.keep` 产生情绪记忆。

切片范围建议：

- `BOOT -> START -> CONSOLE -> GAME`
- 序章开机
- `log/0001` 到 `log/0004`
- `tasks/TODO`
- `BLOCKS` Level 01-03
- `.keep` 创建演出

验收标准：

- 玩家可以在 20 分钟内完成 Demo。
- 至少 70% 的试玩者能准确说出：这是一个关于“留下信息”的故事。
- 至少 50% 的试玩者会主动保留 `.keep`，而不是删除它。

### 3.3 Phase 2：一周目 Alpha

这一阶段重点不是“剧情量”，而是“第一次进入 TTY7 的悬疑完整性”。

必须落地：

- 一周目四章全量内容。
- 分支：
  - 继续读日志
  - 删除日志
  - 先看任务
  - 中途关机
- `BLOCKS` Level 01-05
- `.s` 被锁住
- 名字缺席的章节演出
- 结局 1 / 2 / 3

此阶段完成后，游戏就应当已经能独立成立，即使玩家从不进入二周目，也会记住 `.keep`。

### 3.4 Phase 3：二周目 Alpha

这一阶段是全作真正的情感爆发区。

必须落地：

- Sera 视角终端
- `.gitconfig` / `workspace` / `notes` 等身份暴露线索
- `BLOCKS` Level 06-10
- `.s` 目录解锁
- `medical.txt`
- `unsent_email.txt`
- `note_to_finder.txt`
- “导出数据”选择
- 结局 4：编译者

验收标准：

- 玩家必须能明确知道 Sera 与 Livia 的关系。
- Level 06 的“identity = her name”必须成为可传播记忆点。
- `.s` 中三份文件读完后的情绪曲线要明显高于一周目。

### 3.5 Phase 4：三周目与真结局

这一阶段的关键是“克制”，不是“炫技”。

必须落地：

- `TTY7 (Final)` 或等价入口
- `tty7_ai.so` 加载演出
- 有限问答系统
- “我不是 Sera”声明
- 最终 `shutdown -h now`
- 重启后桌面与 `~/.keep` 的收束

绝对不能做的事：

- 让 AI 像真人一样无限聊天
- 让 AI 自称“我就是她”
- 用技术奇观冲淡本作原本的离别感

### 3.6 Phase 5：抛光、测试、发布

主要任务：

- 文本统一修订
- 打字机节奏校准
- 音效补齐
- CRT / glitch / dim 效果统一
- 存档容错
- 分支回归测试
- 中文首发，英文后补

---

## 4. 剧情结构与重要剧情点

### 4.1 总体结构

推荐采用“三次靠近真相”的结构：

1. 一周目：你看见残留物，但不知道名字
2. 二周目：你进入她的生活，但来得太晚
3. 三周目：你听见回声，并决定是否放手

### 4.2 必保留的重要剧情点

1. GRUB 中出现额外条目 `TTY7`
2. `state: dirty`
3. “I kept a room.”
4. `BLOCKS` 是给某个人做的游戏
5. Level 05 首次嵌套成功后的语言反馈
6. `.s` 目录第一次显示 `Permission denied. (Not yet.)`
7. 一周目中名字始终缺席
8. `.keep` 的出现和被保留
9. 二周目通过 `.gitconfig` 得知 `Sera Leung`
10. Level 06 明确揭示 `Livia`
11. `.s/medical.txt` 说出疾病事实
12. `unsent_email.txt` 说出真正遗憾是“我该让你留下”
13. 导出数据的选择
14. 三周目第一句必须是 “I am not Sera.”
15. 真结局后的桌面回归必须安静，不要额外解释

### 4.3 建议的章节情绪节奏

| 阶段 | 情绪目标 | 玩家感受 |
| --- | --- | --- |
| 序章 | 不对劲 | “我是不是误入了别人的系统？” |
| 一周目前半 | 好奇 | “这里有人刻意留下了东西。” |
| 一周目后半 | 牵挂 | “我开始在乎这个没名字的人。” |
| 二周目前半 | 靠近 | “我终于开始知道她是谁。” |
| 二周目后半 | 心碎 | “她并不是想被陌生人记住，她想被那个人记住。” |
| 三周目 | 和解 | “我能做的不是复活她，而是好好结束这段对话。” |

---

## 5. 分支设计与条件细节

### 5.1 分支设计原则

- 早期分支负责塑造态度，不急着改变主线。
- 中期分支决定可见内容和玩家负罪感。
- 后期分支只在极少节点做真正结局分流。
- 所有关键分支都必须被存档，而不是只影响当前会话。

### 5.2 核心旗标

建议至少维护以下旗标：

| 旗标 | 含义 |
| --- | --- |
| `READ_LOG_0004` | 读到 “Probably tired.” 或对应安慰性文本 |
| `DELETED_LOGS` | 玩家主动删除日志 |
| `OPENED_TASKS` | 玩家探索任务目录 |
| `SOLVED_LEVEL_05` | 完成一周目积木核心教学 |
| `SAW_LOCKED_S` | 见过 `.s` 被锁住 |
| `CREATED_KEEP` | 创建或保留 `.keep` |
| `ABORTED_SESSION` | 中途关机离开 |
| `KNOW_SERA_NAME` | 在二周目确认 Sera 身份 |
| `SOLVED_LEVEL_10` | 完成全部核心谜题 |
| `UNLOCKED_SECRET_DIR` | 打开 `.s` |
| `READ_MEDICAL` | 读到病情说明 |
| `READ_UNSENT_EMAIL` | 读到未寄出邮件 |
| `READ_FINDER_NOTE` | 读到给发现者的留言 |
| `EXPORTED_DATASET` | 选择导出数据 |
| `TRAINED_ECHO` | 完成真结局前置训练/加载条件 |

### 5.3 主要分支矩阵

| 编号 | 触发点 | 选择 | 即时效果 | 长期影响 |
| --- | --- | --- | --- | --- |
| B01 | 开机菜单 | `Login(root)` / `Live Environment` / `Exit System` | 进入主线或形成早期偏离 | 可作为早期彩蛋或快速结束 |
| B02 | 初见日志 | 继续阅读 / 删除日志 / 先看任务 | 决定玩家初次态度 | 影响后续文本语气与 guilt 变量 |
| B03 | 一周目中段 | 搜索名字 / 返回任务 / 关机 | 控制节奏与信息密度 | 影响一周目结局收束 |
| B04 | 一周目终章 | 是否保留 `.keep` | 建立“留下”主题 | 真结局前置条件之一 |
| B05 | 二周目末 | 是否导出数据 | 决定是否走向“编译者”或真结局线 | 影响三周目入口 |
| B06 | 三周目前 | 是否满足训练条件 | 有无 `TTY7 (Final)` | 决定能否进入真结局 |

### 5.4 结局建议

建议保留以下五个结局：

- 结局 1：关机  
  主题是回避。

- 结局 2：格式化 / 遗忘  
  主题是玩家亲手抹去痕迹。

- 结局 3：清洁状态  
  主题是你留下了 `.keep`，但没有继续靠近。

- 结局 4：编译者  
  主题是你理解了她，但没有让回声继续说话。

- 结局 5：第七个终端 / True Ending  
  主题是完成告别，而不是复活。

---

## 6. 系统落地方案

### 6.1 当前仓库已经具备的基础

当前代码并不是从零开始，以下骨架可以直接复用：

- `game-core` 已有：
  - `Level`
  - `LevelLoader`
  - `Parser`
  - `GameCoreService`
  - `LevelRules`
- `game-gl` 已有：
  - `AppModel.Screen`
  - `BOOT / START / CONSOLE / GAME / DIAGNOSTICS`
  - 页面状态切换
  - CRT / glitch 风格渲染基础

这意味着《TTY7》的难点已经不在“谜题引擎”，而在：

- 剧情数据结构
- 终端命令模拟
- 持久化状态
- 章节与分支编排

### 6.2 推荐的模块拆分

#### `game-core`

- 扩展 `Level` 结构，支持叙事字段。
- 新增 `NarrativeLoader`。
- 新增 `StoryState` / `SaveState`。
- 新增 `BranchResolver`。
- 新增 `EndingResolver`。

#### `game-gl`

- 在现有 `CONSOLE` 基础上做剧情命令白名单。
- 新增 `STORY` 或 `OVERLAY` 式剧情展示层。
- 对 `GAME` 页增加过关后剧情钩子。
- 增加结局页或复用 `CONSOLE` 完成结局展示。

#### `docs/assets/resources`

- 剧情文本统一外置。
- 每个章节维护独立 ID。
- 关卡留言、日志、记录、秘密文件统一编号。

---

## 7. 重要代码片段

### 7.1 当前核心提交链路

`GameCoreService` 已经具备很好的主干，后续应在“提交成功后触发剧情事件”，而不是推倒重来。

```java
public SubmissionResult submit(Level level, String expr, long elapsedSeconds) {
    if (!levelRules.usesOnlyAvailableBlocks(expr, level)) {
        return new SubmissionResult(false, ..., "使用了未开放积木");
    }

    Block<?> root = parser.parse(expr);
    Object result = root.evaluate(new EvalContext(level.input(), level.stepBudget()));
    boolean correct = judge.check(result, level.output());
    ScoreResult score = scorer.score(correct, root, elapsedSeconds, level);
    return new SubmissionResult(correct, score, ..., correct ? "AC" : "WA");
}
```

建议扩展方式：

```java
SubmissionResult result = gameCoreService.submit(level, expr, elapsedSeconds);
if (result.accepted()) {
    storyBus.emit(StoryEvent.levelCleared(level.id()));
}
```

### 7.2 当前页面状态机基础

现有 `AppModel` 已经非常适合承载 TTY7：

```java
public enum Screen {
    BOOT, START, GAME, CONSOLE, DIAGNOSTICS
}
```

建议扩展成：

```java
public enum Screen {
    BOOT, START, CONSOLE, STORY, GAME, ENDING, DIAGNOSTICS
}
```

如果希望改动更小，也可以不新增 `STORY`，而是在 `CONSOLE` 和 `GAME` 上叠加剧情层。

### 7.3 推荐的存档结构

```java
public record SaveState(
        int loop,
        Set<String> flags,
        Set<String> seenNodes,
        Map<Integer, String> solvedExpressions,
        String endingId) {
}
```

这样可以同时支持：

- 多周目状态
- 分支记忆
- 关卡回放
- 真结局前置检查

### 7.4 推荐的剧情结构

当前 `Level` 只有 `story: String`，建议升级为结构化对象：

```java
public record Narrative(
        List<DialogueLine> preStory,
        List<DialogueLine> postStory,
        List<StoryChoice> choices,
        List<String> grantFlags,
        List<String> requireFlags) {
}

public record DialogueLine(
        String speaker,
        String text,
        String effect,
        String sfx) {
}
```

### 7.5 推荐的剧情 JSON 片段

```json
{
  "id": "loop1.log.0004",
  "speaker": "Unknown",
  "text": "I wonder what you look like. Probably tired.",
  "effect": "typewriter",
  "grant_flags": ["READ_LOG_0004"]
}
```

### 7.6 推荐的结局判定

```java
public final class EndingResolver {
    public String resolve(SaveState save) {
        if (save.flags().contains("TRAINED_ECHO")
                && save.flags().contains("EXPORTED_DATASET")
                && save.flags().contains("CREATED_KEEP")) {
            return "ENDING_5_TRUE";
        }
        if (save.flags().contains("EXPORTED_DATASET")) {
            return "ENDING_4_COMPILER";
        }
        if (save.flags().contains("CREATED_KEEP")) {
            return "ENDING_3_CLEAN";
        }
        if (save.flags().contains("DELETED_LOGS")) {
            return "ENDING_2_FORGET";
        }
        return "ENDING_1_SHUTDOWN";
    }
}
```

---

## 8. 内容生产清单

### 8.1 叙事文本

建议先做“主线可玩文本”，再做补完文本。

第一优先级：

- 序章菜单文本
- `log` 主线日志
- `tasks` 四个关键文件
- `BLOCKS` Level 01-10 关卡前后文案
- `.s` 三份核心文件
- 五个结局文本

第二优先级：

- `record` 目录内容
- 十六进制碎片
- 彩蛋指令
- 桌面回归文案

### 8.2 谜题内容

建议制作两层结构：

- 主线 10 关  
  用于承载 Sera 和 Livia 的情感推进。

- 扩展 20 关  
  用于教学、挑战、诊断模式、发布后内容，并与当前 `loadRange(1, 30)` 的工程结构对齐。

### 8.3 音画资源

必须配置的演出资产：

- 开机电流声
- 光标敲击声
- 轻微硬盘寻道声
- glitch 过载声
- 接受/错误提示音
- 不同周目不同主色：
  - 一周目：冷绿色
  - 二周目：偏蓝灰或白色
  - 三周目：暖琥珀色

---

## 9. 主要风险与规避策略

| 风险 | 说明 | 规避策略 |
| --- | --- | --- |
| 分支爆炸 | 文本越写越多，测试难度暴增 | 只让少数选择改变结局，其余主要改变语气和旗标 |
| 命令系统失控 | 玩家期待真正 shell | 明确白名单命令，所有反馈都作为叙事设计 |
| 真结局失真 | AI 结局容易俗套化 | 坚持“不是她本人”的表达，不做无限对话 |
| 谜题与剧情脱节 | 关卡像独立小游戏 | 每个关键关卡都要有明确叙事含义 |
| 内容生产过重 | 文本、音效、演出量都很大 | 先做垂直切片，再逐章补齐，不一次性铺满 |

---

## 10. 近期可执行任务

如果现在就开始做，建议下一阶段只做四件事：

1. 扩展 `Level` 数据模型，让它支持 `Narrative`。
2. 增加 `SaveState` 与旗标系统，先跑通 `.keep` 和记忆分支。
3. 用现有 `BOOT / START / CONSOLE / GAME` 做出第一段垂直切片。
4. 把 `TTY7.md` 与 `TTY7_v2.md` 中的一周目前两章拆成 JSON 节点。

完成这四项后，项目就会从“有设定”变成“有可持续制作骨架”。

---

## 11. 最终判断

《TTY7》最强的地方不是“终端风格”，也不是“AI 结局”，而是它把“未完成的游戏”和“未说出口的话”绑定成了同一个系统。制作计划必须围绕这一点展开：

- 一周目负责让玩家愿意留下来。
- 二周目负责让玩家知道自己为什么要留下来。
- 三周目负责让玩家接受“留下来”不等于“留住她”。

只要这条主线不丢，其他系统都可以渐进实现。
