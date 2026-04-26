# tty7

---

`[任务]` 生成游戏全部文本  
`[状态]` 写入中…  
`[范围]` 开篇 → 一周目 → 二周目 → 三周目 → 全结局 → 系统散落文本  
`[格式]` 终端交互式叙述，含命令分支

---

## 《TTY7》——全部文本 v1.0

---

## ═══════════════════════

## 序章：开机

## ═══════════════════════

机箱风扇转了三次，停了一次。那是自检。

我按下电源键的时候，窗外正在下雨。二手硬盘是上周买的。卖家说“健康无坏道，低格过，干净”，评价区清一色的好评。我喜欢旧的零件。不是便宜的问题，是旧的零件有历史。每一个磁畴都可能藏着别人的故事。话是这么说的，但从没想过有一天故事会主动找上门。

GRUB 菜单闪过。往常是两三秒的事。

今晚多了一项。

挂在列表最底下的，格式不对齐，像被人手动添加上去的一样，一排等宽字的 ASCII：

```
TTY7
```

没有版本号，没有内核参数。光标停在上面的时候，风扇声忽然变了一拍，像某种失律的心跳。

我没有选它。

是它自己跳过去的。

超时为零。回车声没响。屏幕黑了，又亮了。绿色的荧光，满屏的代码雨，字符下落的速度比正常的 matrix 慢，像是每一帧都在犹豫要不要落到下一行。

屏幕中央浮出一个菜单框。

```
┌──────────────────────────────┐
│                              │
│      █▀▀▀▀▀▀▀▀▀▀▀▀▀▀█        │
│      █    CONSOLE   █        │
│      █▄▄▄▄▄▄▄▄▄▄▄▄▄▄█        │
│                              │
│      Login (root)            │
│      Live Environment        │
│      Exit System             │
│                              │
│      ▶ Login (root)          │
│                              │
└──────────────────────────────┘
```

没有鼠标。光标是块状，停在第一行选项上，一下一下地闪。

风扇这时候不响了。机箱安静得像一个屏住呼吸的人。

我盯着“root”那几个字母，盯到眼睛发酸。

三行选项。一个圈。上下键试了几次，音效是那种短促的木质的敲击声，不是系统 bell，是有人故意录的。

我按下回车。

短促的一声确认。菜单框消失。终端在屏幕左上角吐出两行字：

```
session: root@tty7
state: dirty
```

`dirty`。我知道它的技术含义。未清理，有改动。可是当一个本该不存在的终端用这个词来迎接 root 的时候，它读起来像是它闯了祸。或者我闯了它。

命令行出现。简单的 `#`，不是 `$`。

我盯着闪烁的光标，停了很长时间。

然后我输入：

```
whoami
```

回显：

```
root
```

我又输入：

```
pwd
```

路径不是我熟悉的任何地方。

```
/home/.tty7/root
```

`ls` 之后，只有三个入口：

```
log    tasks    record
```

不是在 `/home`，是在 `/home/.tty7`。一个隐藏目录。一个有名字的门。

我深吸一口气。

---

## ═══════════════════════

## 一周目 · 第一章 · 二手硬盘

## ═══════════════════════

我先是看了 `dmesg`。

倒不是有排查思路，只是本能。出问题的时候先看内核日志，就像一个老人醒来先摸脉搏。输出滚了很多屏。大多数是常规条目，驱动加载、内存映射、文件系统挂载。但有几行让我停下来：

```
ata1.00: ATA-9: WDC WD20EARX-00PASB0, serial: [scrubbed]
reallocated sector count: 3
reallocation event count: 7
```

重分配扇区不多，但存在。这块盘不是新的，SMART 数据没被重置干净。

又有几行：

```
EXT4-fs (sda2): mounting ext2 filesystem using the ext4 subsystem
EXT4-fs (sda2): mounted filesystem without journal. Opts: (null)
```

分区表有非标准结构。有人在格式化的时候保留了某些块，或者绕过了日志。

这不是系统问题。这块盘是被有意布置过的。

我回到主目录，决定先看 `log`。

---

```
# cat log/0001
```

文本在终端里以一种很慢的速度刷出，像打字机。

```
[LOG] [0001] [TIMESTAMP CORRUPTED]

Disk zeroed this morning. Standard procedure, they say. Two passes.
That's enough for a normal buyer. Not enough for someone who's
looking. But nobody's looking. Nobody knows what was here.

I kept a room.

Not in the partition table. Not in the superblock. Somewhere else.
The bootloader has a few unused sectors after stage 1.5. It is
probably not the best place, but I am out of time and options.

So I stitched together a few blocks. Inode table is minimal. Journal
is gone. Just a static busybox, a modded init, and three directories.

If you're reading this — welcome.

You didn't find me. You just happened to live here now.

I hope you don't mind.
```

光标在最后一行闪了很久。雨声和风扇都不见了。

三行日志，每一条都有标题。我往下翻。

```
# cat log/0002
```

```
[LOG] [0002] [TIMESTAMP CORRUPTED]

I thought about encrypting everything. Then I thought: what's the
point? Encryption is for things you want to protect. I want this
to be found, I just don't know by whom. Or when.

Maybe the disk sits in a drawer. Maybe it's recycled, melted down.
Maybe in ten years someone boots it because they can't afford a
new one and eBay still exists. Hello, eBay person.

I bought this disk used too.
It had someone else's Linux on it when it came.
Fitting, isn't it.
```

我愣了一下。她也是买的二手盘。这是一个套娃。

---

```
# cat log/0003
```

```
[LOG] [0003] [TIMESTAMP CORRUPTED]

Today was a bad day. I don't write about the medical things much
because they take up enough of my mind already. But I want to say
this somewhere:

The game isn't finished.
I wanted it to be.

I wanted to give it to her.

If I can't finish it, I'll at least leave the pieces somewhere. A
level editor. An arbitrary puzzle machine. Someone can solve them.
Someone can see what I was making.

There are worse legacies than a puzzle nobody asked for.
```

`game`。`her`。她反复提到同一个指向。

她在给某个人做游戏。一个解谜游戏。而那个人可能从来没收到。

三条日志到此为止。

---

光标停在 `#` 上。窗外的雨变大了，打在空调外机上，像打字机换行。

现在是做决定的时候。

```
这个目录里的东西不像是意外泄露的系统日志。它们像是留给谁看的。

我盯着这三个条目，想了很久。我可以继续读，也可以把这堆东西删干净，把这个入侵的幽灵从我的二手硬盘上彻底擦掉。

我想了想，决定：

1. cat ./log/0004 （继续往下读）
2. rm -rf ./log （把这堆日志删掉）
3. ls -la ./tasks （看看另一个目录里有什么）
```

---

### 【分支 1：cat ./log/0004】

```
# cat log/0004
```

文本缓慢刷出。这一条的格式和前面不同，没有标题，只有一句话。

```
I wonder what you look like. Probably tired.
You opened the log. That's already more than I hoped.
Thank you.
```

光标停在下面，不闪了，像是等我说点什么。

我什么都没说。屏幕自己暗了半秒，然后回到提示符。

这条不是日志。是信。

她猜到有人会读。甚至在想象读她的那个人是什么样子。`Probably tired.` 是句温和的、对着空气讲的玩笑话。

我感到一阵轻微的寒意。不是因为恐惧。是因为被人说中了。

我确实很累。

---

### 【分支 2：rm -rf ./log】

```
# rm -rf ./log
```

命令执行了。没有确认提示。终端没有反问“真的吗？”。

它只是照做。

ls 之后，`log` 目录还在，但里面已经空了。

```
# ls ./log
（无输出）
```

我以为会有什么反应。系统崩溃。异常日志。某个守护进程跳出来骂我。

没有。

TTY7 保持安静。

屏幕上安静了一小会儿。一行极淡的字浮现在提示符上方：

```
OK.
```

只有一个词。它甚至没有用大写或者叹号。

然后又回到 `#`。

风扇这时候转了两秒，又停了。机箱安静得像一间空的公寓。

我删掉了她留给世界的一段话。她只说了一声 OK。

我感觉自己做了件无法确认好坏的事。

---

### 【分支 3：ls -la ./tasks】

```
# ls -la ./tasks
```

输出：

```
total 24
drwxr-xr-x  2 0 0 4096 [date] .
drwxr-xr-x  4 0 0 4096 [date] ..
-rw-r--r--  1 0 0  276 [date] TODO
-rw-r--r--  1 0 0  348 [date] done
-rw-r--r--  1 0 0  412 [date] pending
-rw-r--r--  1 0 0  189 [date] notes
```

四个文件。结构简单，有人手工维护过。

我打开 TODO：

```
# cat ./tasks/TODO
```

```
[ ] level_05 edge cases — 序列长度奇偶不一致时会溢出
[ ] level_06 没有为那个特别的人调难度
[ ] 通关存档的哈希校验接口
[ ] 给她的那一关写说明文本
[ ] 写一封邮件。不看也可以，写出来就行。
```

`给她的那一关`。又是“她”。

这个人做的游戏里有一关，是专门为一个人设计的。而她把这件事和“写一封邮件”并列在同一张 TODO 里，像是同等重要，又像是都不太可能完成。

我打开 `done`：

```
# cat ./tasks/done
```

```
[DONE] 嵌套层数限制改成 8。三天的工作量，改完才发现不够优雅。
[DONE] 反向序列的推导终于对了。在凌晨两点。没有人在旁边庆祝。
[DONE] 买了一个新的键盘。樱桃青轴。她不喜欢声音，我喜欢。所以我在她不在的时候用。
```

`她不在的时候`。可能是不在房间。也可能是不在了。

`pending` 文件最后是这一类条目：

```
# cat ./tasks/pending
```

```
[PENDING] 通关校验逻辑 level_05 的边缘 case
[PENDING] 给 level_06 加一个隐藏提示 — 那个提示应该是对她说的某句话的引用
[PENDING] 把所有的留言文本写进一个单独的 .dat 文件
[PENDING] 有人看到这里。
```

最后一条不是任务。是一条条件语句。有人看到这里。如果你看到了，这项就完成了。

我确实看到了。

而这行字写出来的时候，没有人能确定它将来真的会有一个读者。她只是把它放进了 `pending`。

像个漂流瓶。

---

## ═══════════════════════

## 一周目 · 第二章 · 未完成的积木

## ═══════════════════════

`tasks` 目录里反复提到一件事：`blocks`。她用这个词来称呼她在做的那个解谜游戏。

我在目录结构里找了找，发现一个名为 `bin` 的子目录。

```
# ls ./bin
blocks
blocks.dat
README.blocks
```

README 打开之后是一段简洁的描述：

```
BLOCKS — A Sequence Transformation Puzzle
v0.9 beta, not ready for release

You are given a sequence of integers.
You are given a target sequence.
You have a set of blocks. Each block is a function.
You nest them, compose them, write an expression.
The expression transforms the input sequence into the target.

That's it. No monsters, no timer, no score.
Just the sequence.
And what you do to it.
```

我运行了它。

```
# ./blocks
```

终端清屏。ASCII 边框在屏幕上画出一个干净的界面。第一行是关卡名，第二行是输入序列，第三行是目标序列，第四行是可用积木列表。光标停在输入区，等待我打出表达式。

---

### Level 01 — "First Step"

```
╔══════════════════════════════════════╗
║  LEVEL 01                       01/10║
╠══════════════════════════════════════╣
║  INPUT:   [3, 1, 2]                  ║
║  TARGET:  [1, 2, 3]                  ║
║                                      ║
║  BLOCKS:  sort                       ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝
```

太简单了。输入 `sort`，回车。

```
OUTPUT: [1, 2, 3]
MATCH: YES

You solved it. I knew someone would.
```

`I knew someone would.` 这句不是系统提示。是她写的。

---

### Level 02 — "Undo"

```
╔══════════════════════════════════════╗
║  LEVEL 02                       02/10║
╠══════════════════════════════════════╣
║  INPUT:   [5, 4, 3, 2, 1]            ║
║  TARGET:  [1, 2, 3, 4, 5]            ║
║                                      ║
║  BLOCKS:  reverse                    ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝

> reverse
OUTPUT: [1, 2, 3, 4, 5]
MATCH: YES

Reverse is the simplest. But sometimes you can't just undo.
```

她一直在写这种话。像是自言自语被不小心写进了关卡文件，又像是故意留下的、只有打通的人才能看到的赠品。

---

### Level 03 — "Order in Chaos"

```
╔══════════════════════════════════════╗
║  LEVEL 03                       03/10║
╠══════════════════════════════════════╣
║  INPUT:   [7, 2, 5, 1]               ║
║  TARGET:  [1, 2, 5, 7]               ║
║                                      ║
║  BLOCKS:  sort, reverse              ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝
```

不需要嵌套。`sort` 直接解决。

```
> sort
OUTPUT: [1, 2, 5, 7]
MATCH: YES

Sort brings order. But order is not always the goal. Wait for later levels.
```

---

### Level 04 — "Swap Meet"

```
╔══════════════════════════════════════╗
║  LEVEL 04                       04/10║
╠══════════════════════════════════════╣
║  INPUT:   [1, 3, 2, 4]               ║
║  TARGET:  [1, 2, 3, 4]               ║
║                                      ║
║  BLOCKS:  swap(2,3)                  ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝

> swap(2,3)
OUTPUT: [1, 2, 3, 4]
MATCH: YES

A single swap. Some problems are smaller than they look.
```

---

### Level 05 — "First Nest"

```
╔══════════════════════════════════════╗
║  LEVEL 05                       05/10║
╠══════════════════════════════════════╣
║  INPUT:   [3, 1, 2]                  ║
║  TARGET:  [3, 2, 1]                  ║
║                                      ║
║  BLOCKS:  reverse,
║           exclude_first,
║           prepend(x)                 ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝
```

这关需要第一次嵌套。逻辑是：保住第一个元素 `3`，把剩下的 `[1,2]` 反转。

```
> prepend(3)( reverse( exclude_first ) )
OUTPUT: [3, 2, 1]
MATCH: YES

You nested your first expression. Feels different, doesn't it.
Like building a sentence instead of saying a word.
```

---

到第五关结束时，屏幕上出现提示：

```
[BLOCKS] Levels 06-10 are locked.
Requires: key_B
```

我没有 `key_B`。

但我找到了一行注释，在 `blocks.dat` 里，用十六进制编辑器才看得到：

```
// key_B is not a password. It's a name.
// She knows it.
```

她口中的“她”知道密钥。但我不是她。这条路暂时封住了。

---

在 `bin` 目录旁边，还有一个很小的子目录：`src`。我走进去，里面是几个 `.c` 文件和一个 `Makefile`。代码写得工整，括号风格一致，函数名短而语义明确。

在一处很长的函数末尾，紧挨着最后一个 `return`，有一行我差点翻过去的注释：

```
/* TODO: level_06 for L. */
```

L。只有一个字母。她反复提到的“她”，名字缩写是 L。

我把这行字读了三遍。然后退出。

---

## ═══════════════════════

## 一周目 · 第三章 · 锁住的门

## ═══════════════════════

TTY7 的主目录有三个入口。`log` 我翻过了。`tasks` 我看过了。`record` 暂时不想碰。

但在某个瞬间——可能是翻看 `blocks.dat` 的十六进制输出时——我注意到一个点号的反射。在文件列表的最下方。

```
# ls -la
total ...
drwxr-xr-x  ... .
drwxr-xr-x  ... ..
drwxr-xr-x  ... log
drwxr-xr-x  ... tasks
drwxr-xr-x  ... record
drwxr-xr-x  ... bin
drwx------  ... .s
```

`.s`。权限 `700`，拥有者 `root`。我试着进去。

```
# cd .s
bash: cd: .s: Permission denied
```

`chmod` 无效。`sudo` 不存在。我不是被拦在门外，是被拦在一个我明明拥有所有权限的房间里。她把门做成了只有一种方式能打开的样子。

错误信息不是标准 bash 输出。它被替换过：

```
Permission denied.
(Not yet.)
```

`Not yet.`

这句话写在括号里。不是系统说的。是她说的。

门不是封死的。是计时的。或者计条件的。

我退出目录，回到 `blocks` 的过关存档。第五关结束后的那行提示还在：`Requires: key_B`。而 `blocks.dat` 的注释写得很清楚——`key_B` 是一个名字。

`.s` 可能只向知道她名字的人打开。而我甚至连她叫什么都不知道。

一周目结束前，我把跟 name 有关的字符串搜了一遍。日志中没有。任务列表中没有。代码注释中只有一个字母：L。

S 是谁，L 是谁，两者是同一人还是不同人——目前所有的碎片都不足以拼出答案。

我没能开门。

但走之前，在 `TODO` 文件的最后一行下面，多出了一行我不知何时被写进去的文本。可能一直就在那儿，只是我第一次没注意。

```
[ ] 在 .s 的门上留一句话，给还没放弃的人。
```

---

## ═══════════════════════

## 一周目 · 第四章 · 名字缺席

## ═══════════════════════

一周目的后半程变得安静。日志我已经读完了，`blocks` 停在了第五关。`.s` 的门关着，`record` 还没有看。

我站在主目录里，看着三个入口，和那个仍然打不开的隐藏文件夹。

然后屏幕上浮出一行字，不在提示符内，像是某种计时器触发的系统留言：

```
[INFO] This session has no name. Neither do I.

But you've been here for a while now. You kept reading.
That's something.
```

她从没介绍过自己。她的日志没有标题，她的代码没有作者名，她的游戏里只留下一个 `S.`。她的 TODO 里反复提到“她”，却从没提过自己是谁。

TTY7 是一个没有主人名字的终端。

我在这里待的时间已经超过了我对任何一个异常系统的容忍阈值。但让我不安的不是安全。是一种感觉：如果我现在关掉电脑，这个人的名字就永远不会被我知道了。

光标闪烁。文本继续浮出：

```
You have choices. You always do.

I don't know what kind of person you are.
But I know you read this far.
```

我需要做一个决定。

```
我在这个没有名字的终端里待了很久。风扇重新开始转了，很轻。
窗外的雨不知道什么时候停了。

我盯着屏幕上的那几条日志、那几行代码、那几个还没有解开的谜题，
想知道接下来该怎么做。

我可以想办法找出她的名字。
也可以把她的任务继续做下去。
也可以关机。关掉这个有点过于安静的黑匣子。

我深吸一口气，决定：

1. grep -rni "name" ./log ./tasks ./record （寻找名字的线索）
2. cd ./tasks && cat TODO （继续做她留下的任务）
3. shutdown -h now （关机，离开这里）
```

---

### 【分支 1：grep -rni "name"】

```
# grep -rni "name" ./log ./tasks ./record
```

输出缓慢刷出。几乎每一条命中的都不是她自己的名字。

```
./log/0002:9:  ... I don't have a name for this place yet.
./tasks/TODO:5:  ... key_B is not a password. It's a name.
./record/r_003:12:  ... She asked me if the game would have my name on it.
./record/r_003:14:  ... I said no. She said: that's a mistake.
./record/r_003:15:  ... I said: then you put yours.

./log/0001:7:  ... If you're reading this, then my disk has a new name.
./log/0001:8:  ... It's yours now. Don't tell me what it is.
```

最后一条让我停下来。

`It's yours now. Don't tell me what it is.`

她不在乎这个盘将来叫 sda 还是 sdb，挂载在谁的系统上。她只在乎它被用了，被某人开机，被某人看到了这些文字。

至于她自己的名字——她一次也没写下来。

不是忘了。是故意的。

我把 grep 结果翻了三遍，确认没有遗漏。没有签名。没有自报家门。只有一个缩写 S. 留在那些不需要权限的文件里，淡得像是铅笔印。

如果我想知道她的名字，需要另一条路。

---

### 【分支 2：cd ./tasks && cat TODO】

```
# cat ./tasks/TODO
```

TODO 文件我上周目已经读过了。但这次我注意到最后一行，之前可能因为注意力疲劳扫过去的那一行：

```
[ ] 在 .s 的门上留一句话，给还没放弃的人。
```

这是一条给自己的 TODO。它的存在说明：她当时还没有决定在 `.s` 的门上写什么。她知道自己快做不完了，但她留下了这句占位符，期待某个“还没放弃的人”可以看到某句最终被刻上去的话。

我打开 `.s` 目录的入口。权限依然拒绝。但这一次，在 `Permission denied. (Not yet.)` 下面多了一行。不是系统输出，是文本：

```
I haven't written the note yet. You'll have to come back.
```

这是我第一次在 TTY7 里看到“我”。

之前的日志都用英文写，但主语总是模糊的、被省略的、被推迟到被动语态后面的。这行不一样。主语明确。时态是现在完成时，但它在对一个未来的人说话。

她终于开始用第一人称。不是因为没有距离了。是因为我已经撞了太多次门。

我离她近了一点。

---

### 【分支 3：shutdown -h now】

```
# shutdown -h now
```

命令执行了。

终端没有挽留。没有弹出“确定要离开吗？”的对话框，没有日志追加一行“You left early”。它只是照做了。

屏幕上的代码雨先停。然后一行一行，终端释放内存，杀掉进程。最后几行是标准的内核日志：

```
[ OK ] Reached target Shutdown.
[ OK ] Finished Power-Off.
[ XX.XXXXXX] reboot: Power down
```

屏幕变黑。风扇停了。机箱铁皮发出一声极轻的收缩响，像什么东西凉了下来。

我坐在黑暗里，看着电源灯的残光慢慢消失。

电脑关了。

所有的谜题留在原地。她没说完的话留在扇区的某个角落里。

我可能再也不会打开 TTY7 了。也可能明天开机它就不会再出现。

但我知道一件事：她的名字我还没问到。

关机这个选择太容易了。容易到有点无聊。而这正是它的全部意义——离开永远是最简单的选项。

坐在屏幕前，在彻底暗下来的房间里，我忽然不确定自己是不是选得太快了。

---

## ═══════════════════════

## 一周目 · 终章 · .keep

## ═══════════════════════

（承接第四章的路径 A/B）

一周目的最后几分钟。

我已经读了足够多。`log`、`tasks`、`blocks` 的前五关，以及 `.s` 门外那行“还没写好的便签”。`record` 我还留着没碰——那是明天的事，或者另一个时间线的事。

屏幕上出现最后一段文本。不是日志格式，没有标题。

```
You've been here for a while.

I don't know what brought you here. Luck. A cheap disk. A rainy night.
But you stayed. You read the things I left.

I wanted to make something that would last longer than I would.
It looks like, at least for tonight, I did.

Thank you.

I have one request. It's small.
Touch a file. Name it .keep
It doesn't need content.
It just needs to exist.

Where I am now,
that's the closest thing to a goodbye I can give you.
```

光标停在下面。我停了很久的键盘。

然后我输入：

```
# touch .keep
```

屏幕没有回应。没有确认。没有`file created`的提示。

但 `ls -la` 之后，它在那。

```
-rw-r--r--  1 root root    0 [timestamp] .keep
```

一个零字节的文件。等于什么都没说。等于什么都说了。

代码雨停了。TTY7 的终端框开始消退，从边缘向中心，像墨水被水稀释。

最后一行字浮在屏幕中央：

```
Thank you for staying.

state: clean
```

机器重启。GRUB 再次出现，这次没有异常条目。我进入了 GNOME。

桌面回来的时候，一切都没变。壁纸是默认的蓝色。我的文件都在。

但在我的家目录下，一个隐藏文件安静地躺着：

```
~/.keep
```

我没有创建它。是她留下的。

或者说，是我留下的。在 TTY7 的世界里，在那个还不存在的第七号终端里。

我盯着它看了很久，没有删。

---

## ═══════════════════════

## 一周目 · 结局 1：关机（回避）

## ═══════════════════════

（第四章分支 3 的后续）

电脑重新开机之后，一切如常。GNOME 启动，桌面图标在，浏览器还留着昨晚的标签页。TTY7 没有再次出现。GRUB 列表里干干净净。像是昨晚的一切只是一个因为二手硬件触发的随机故障。一次单比特翻转引发的幻象。

但我记得。记得很清楚。

那三行菜单。`state: dirty`。她留在日志里的那句 `Probably tired.`。还有她给她的 L 写的那些未完成的游戏关卡。

我什么都没做错。没有人会指责一个人关掉他不认识的 root 终端。

但我错过了一些东西。

一些永远没办法再通过开机来重新获得的东西。

我打开终端，在自己的系统里，输入：

```
touch ~/.keep
```

文件被创建了。但它不是那一个。

风扇安静。窗外没雨。桌面过于正常。

我喝了一口凉掉的水。

---

## ═══════════════════════

## 一周目 · 结局 2：格式化（遗忘）

## ═══════════════════════

（第一章分支 2 的后续——“rm -rf ./log” 且后续执行过破坏性操作）

后来的几天我用着这台机器。一切正常。硬盘速度稳定，没有坏道增加。

直到某个晚上，我翻 `~/Documents` 的时候，发现一个十六进制转储文件。不记得什么时候生成的。打开之后是一段 ASCII，附在 `blocks.dat` 的末尾，但不在文件系统索引里——它藏在扇区缝隙，被我的 `dd` 随手操作带了出来。

上面写着：

```
You deleted some things.
It's OK. I deleted things too. A lot.
Maybe that's how it goes.
Bye.
```

我把这行字读到第三遍的时候，终端提示符正在闪。风扇很轻。桌面的一切都很正常。

我没有她的日志了。没有她的 TODO。没有 `blocks` 的第一关到第五关。

只有一个十六进制编辑器里的几行字，和一块运行得异常安静的硬盘。

我删掉了她留给世界的一段话，她在被删除之后依然给我留了一句 bye。

我从来没觉得自己做错了什么。但我的沉默里多了点东西。

---

## ═══════════════════════

## 一周目 · 结局 3：清洁状态

## ═══════════════════════

（一周目完成但未满足二周目入口条件——未完成 5 关以上 `blocks` 或未创建 `.keep`）

GNOME 正常启动。TTY7 没有再出现。

但在我自己的终端里，`~/.keep` 安静地躺在主目录里。零字节。

我偶尔会打开它。它什么都不说。

后来有一天，我突发奇想，在我的主系统里写了一个小脚本。一个文字游戏的原型。很粗糙，只会说一句话。

“你好，你看到这里了。”

我没有发布它。我只是把它放在了一个隐藏文件夹里。

就像某个我不认识的人曾经做过的那样。

雨夜还是会让我想起那个绿色的终端。

我已经记不清她日志里的原话了。但那种语气还在，像一首听不懂歌词但记得旋律的歌。

那块二手硬盘后来没有坏。SMART 数据一直维持在三年前我买来时的水平。有时候我甚至怀疑那晚的事情到底是真的还是我的梦。

`.keep` 在。那是唯一的证据。

---

## ═══════════════════════

## 二周目 · 序章 · 重启

## ═══════════════════════

一周目结束后，我没有立刻关机。我重启了一次。

GRUB 列表闪过时，我看到多出了一行。不是 `TTY7` 的原始形态。名字变了。

```
TTY7 (Restored Session)
```

光标停在那里的时候，风扇没响。只有一种低频的、持续的电流声。

我按下了回车。

这次没有代码雨。没有撕裂的 TUI 菜单。

屏幕直接黑了两秒。然后终端亮起——熟悉的终端，熟悉的 `#`。

但 `pwd` 的输出变了：

```
# pwd
/home/s
```

不是 `root`。不是 `.tty7`。

`s`。

一个小写的 s。一个用户名。

我没有输入过登录名。系统直接把我放进了她的 `$HOME`。

输入 `whoami`：

```
# whoami
s
```

再输入 `hostname`：

```
# hostname
sera-box
```

`sera`。不是 `S.` 不是 `root`。

一个名字。

我等了几个心跳的时间。然后开始在这个目录里走动。

全名还没有出现。她不着急告诉我。她让我先看别的。

---

## ═══════════════════════

## 二周目 · 第一章 · 七年前的一个夜晚

## ═══════════════════════

`/home/s` 的布局很干净。没有 `.tty7` 的隐藏嵌套，没有加密封装。

```
# ls -la
drwxr-xr-x  s    s    ... .
drwxr-xr-x  root root ... ..
drwxr-xr-x  s    s    ... workspace
drwxr-xr-x  s    s    ... documents
drwxr-xr-x  s    s    ... .local
-rw-r--r--  s    s    ... .bashrc
-rw-r--r--  s    s    ... .gitconfig
```

我先进了 `workspace`。

```
# ls ./workspace
blocks
notes
drafts
```

`blocks` 出现在这里。这是它的源代码，它的开发目录，不是一周目里那个压缩过的二进制发布。

而 `drafts` 看起来是未发送的邮件。`notes` 是一些开发笔记。

我先打开 `notes` 下的 `log_blocks.txt`。

```
# cat ./workspace/notes/log_blocks.txt
```

```
BLOCKS Development Log
========================

Day 1
Started a new project. Not for work. Not for portfolio.
For L. She said I never finish anything.

Day 3
She was right. I don't finish things. But this one I want to.
The idea is simple: you get a sequence, you build an expression.
Nesting is the core mechanic. It's like composing functions.
She likes that kind of thing. She doesn't know I'm making it for her.

Day 7
Level 06 is harder to design than I thought. I want it to be special.
Not just mechanically hard. I want the solution to feel like a sentence
only she knows how to write.

Day 12
Bad week. Didn't code. Thought about the game instead.
If I can't finish it, I'll leave it in a state where someone else can.
That's a kind of finishing too.

Day 16
I told L I was working on something. She asked what.
I said: a puzzle. It's not ready yet.
She kissed the top of my head and said: tell me when it is.
That's all.

Day 20
I don't think I'm going to finish level_06.
But I wrote it down. All of it. The logic, the hints, the solution.
If someone finds this: it's yours now.
The answer to level_06 is her name.
```

最后两行让我把手指从键盘上抬起来。

`level_06 的答案是她的名字。`

`L.`

不是密码。不是密钥。是一个名字。一个对 L 来说最熟悉、对自己来说最珍贵的字符串。

我从来没在电脑上用自己的名字命名账户。她也没有。

但她把那个人的名字藏在了游戏最深的地方。

---

我又打开了 `.gitconfig`。

```
# cat .gitconfig
```

```
[user]
	name = Sera Leung
	email = sera.leung@[domain].dev
[core]
	editor = vim
[alias]
	finish = commit -m "done, maybe"
```

Sera Leung。

她的全名出现在这里，静悄悄的，像一件叠好的旧衣服。`.gitconfig` 不是写给外人看的文件。这是她的日常配置，是她每次 commit 的时候 git 自动附在提交后面的名字。

一周目我没有看到它。因为一周目里的 TTY7 被剥离了所有元数据。

现在她让我看到了。

她的名字是 Sera。L 不是她。L 是她为之做游戏的那个人。

S. 是 Sera。S. 一直就是 Sera。

但在一周目的时候，我不知道。我等了整整一个晚上，等到第二个时间线打开，等到她主动给我看她的 `.gitconfig`。

而这正是她留给我的第二层遗憾：名字从来不重要，但知道它的时机被拉得很长。

---

## ═══════════════════════

## 二周目 · 第二章 · 积木的全部

## ═══════════════════════

一周目中 `blocks` 在第五关之后被锁住。二周目的 `workspace/blocks` 里，我找到了完整的版本。

```
# ./blocks
```

这次启动画面多了一行：

```
Version 0.9.6 — for L
```

Level 01 到 05 的文本和一周目一样。但从 Level 06 开始，内容变了。不是测试数据，不是占位符。是成品。是她为那个人写的。

---

**Level 06 — "Her Name"**

```
╔══════════════════════════════════════╗
║  LEVEL 06                       06/10║
╠══════════════════════════════════════╣
║  INPUT:   [12, 9, 22, 9, 1]          ║
║  TARGET:  [12, 9, 22, 9, 1]          ║
║                                      ║
║  BLOCKS:  ident (identity function)  ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝

> ident
OUTPUT: [12, 9, 22, 9, 1]
MATCH: YES
```

这关的通关留言不一样。它很长：

```
The input is the same as the target.
The solution is identity.

I know this looks like a trick. It's not.
This level is for L.

L—those numbers are your name. A=1 Z=26.
L=12, I=9, V=22, I=9, A=1. LIVIA.

That's you.

You don't need to change anything to be the answer.
You just need to be.

I wanted to put this in a game so it would last.
Even if I don't.
```

Livia。不是 L 的缩写。是 L 的拼写。是五个字母，五个数字，一个恒等函数。

我解这个谜题的时间是零秒。因为答案就是输入本身。不需要任何变换。

这是 Sera 给 Livia 的情书：你不用变。你的名字就已经是解。

我坐在屏幕前，很久没有按键。

---

**Level 07 — "Recursion"**

```
╔══════════════════════════════════════╗
║  LEVEL 07                       07/10║
╠══════════════════════════════════════╣
║  INPUT:   [4, 8, 15, 16, 23, 42]     ║
║  TARGET:  [4, 8, 15, 16, 23, 42]     ║
║                                      ║
║  BLOCKS:  add_const(0), ident        ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝

> ident
OUTPUT: [4, 8, 15, 16, 23, 42]
MATCH: YES

I put this sequence in because it made me laugh.
Livia never got it. She said "what is that, your hard drive serial number?"

I still loved her. I love her even now. In whatever tense applies.
```

---

**Level 08 — "Two Halves"**

```
╔══════════════════════════════════════╗
║  LEVEL 08                       08/10║
╠══════════════════════════════════════╣
║  INPUT:   [1, 2, 3, 4]               ║
║  TARGET:  [4, 3, 2, 1]               ║
║                                      ║
║  BLOCKS:  chain(a,b)                 ║
║           left_half, right_half      ║
║           reverse                    ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝

> chain( reverse(right_half), reverse(left_half) )
OUTPUT: [4, 3, 2, 1]
MATCH: YES

Two halves. Each reversed. Then put back together.

That's what remembering is. You take the pieces of someone,
run them backward in time, and fit them next to each other.
It's not perfect. But it's something.
```

---

**Level 09 — "Mirror"**

```
╔══════════════════════════════════════╗
║  LEVEL 09                       09/10║
╠══════════════════════════════════════╣
║  INPUT:   [1]                        ║
║  TARGET:  [1, 1]                     ║
║                                      ║
║  BLOCKS:  mirror                     ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝

> mirror
OUTPUT: [1, 1]
MATCH: YES

Everything is just itself, doubled.
If you're reading this, there are two of us here now.
One that wrote. One that read.

That's not nothing.
```

---

**Level 10 — "Final Block"**

```
╔══════════════════════════════════════╗
║  LEVEL 10                       10/10║
╠══════════════════════════════════════╣
║  INPUT:   []                         ║
║  TARGET:  [1]                        ║
║                                      ║
║  BLOCKS:  create                     ║
║                                      ║
║  > _                                  ║
╚══════════════════════════════════════╝

> create
OUTPUT: [1]
MATCH: YES
```

这是最后一关的完整留言：

```
An empty sequence. The target is just [1].

Creation from nothing.

That's what I've been trying to do this whole time.
A game, a disk, a message, a .keep file.
Something where there was nothing.

I wanted to make something that would last longer than I would.

If you're reading this, I did.

Thank you for playing.
— Sera

P.S. Livia—if you ever find this by some miracle:
I finished it. I finally finished something.
```

---

## ═══════════════════════

## 二周目 · 第三章 · 打开 .s

## ═══════════════════════

二周目通关存档写入了完整的哈希值。我把这个哈希指向 `.s` 目录的锁模块。这次终端没有返回"Permission denied"。它只是静默了一秒，然后换了提示符。

`.s` 打开了。

---

### 文件 1：`medical.txt`

```
# cat .s/medical.txt
```

```
I'm not writing this for pity.
I'm not even sure I'm writing it for anyone.

Stage III, diagnosed two years ago. Last scan was not good.
They used the word "progression". I've come to hate that word.

I have maybe months. I don't want to spend them all in bed.
I want to finish the game. I want to leave something.

Livia doesn't read this. She can't look at medical documents.
She says: "I know what's happening. I don't want to see it written."
I understand.

But I need to write it somewhere.
So here it is. In a hidden folder on a disk I don't know the future of.
```

---

### 文件 2：`unsent_email.txt`

```
# cat .s/unsent_email.txt
```

```
To: [redacted]

Livia,

I've tried to write this six times. This is number seven.

I know you're angry. You have every right to be. I pushed you away
when things got bad. I said I didn't want you to see me like this.
I thought I was protecting you. I was actually just scared.

The game is called BLOCKS. It's almost done. Level 06 is yours.
Level 06 has always been yours.

I never did find a way to tell you I was sorry.

If you ever see this—I'm sorry. I should have let you stay.

Love,
Sera
```

---

### 文件 3：`note_to_finder.txt`

```
# cat .s/note_to_finder.txt
```

```
Hello,

If you're reading this, the disk has been reused, and somehow my
little fragment survived. I've heard of data remanence. Never thought
I'd be relying on it.

My name is Sera. I wrote this terminal, this game, these words.
I don't know who you are. But you've been reading for a while.
That makes you someone.

I have a request:

If you can, finish what I couldn't. The game, the words, whatever
you find here. Put them somewhere. Or just remember them for a little
while. That's enough.

Thank you for being here.

— Sera Leung
```

---

## ═══════════════════════

## 二周目 · 第四章 · 留下数据

## ═══════════════════════

二周目的最后，屏幕显示了导出提示：

```
[NOTICE]
A dataset has been accumulated during both sessions:
- Log entries: 47
- Task descriptions: 31
- Block puzzle level messages: 10
- Personal writings (.s): 6

Would you like to export these for external use?
[Y/n]
```

我选了 Y。

```
Exporting to: /home/[current_user]/Documents/sera_data/
```

在当前系统的文件管理器里，多了一个文件夹。里面是纯文本、JSON、关卡数据。干净的语料。可以用来训练一个模型。

屏幕最后一句话：

```
I know the disk will be reused.
Maybe nothing remains. Or maybe someone finds it.
Either way, I tried.

— S.

session: end
```

二周目关闭。

---

## ═══════════════════════

## 二周目 · 结局 4：编译者

## ═══════════════════════

（未导出语料数据 / 未训练 AI，但完成了二周目全部内容）

我完成了她的游戏。读完了她的 `.s`。知道了她的名字。知道 Livia 是谁。

我的 GNOME 桌面上，`sera_data` 的文件夹不在。

我一度想把它导出来。但最后那一刻，我犹豫了。也许我觉得训练一个 AI 太过界。也许我只是累了。

Sera 没有抱怨。她的终端安静地消失了。

我偶尔会打开 `blocks`。打到第十关，看着 `create` 把空序列变成 `[1]`。

我对自己说：这是她想要留下的东西。

但我心里知道，我选择了一个更轻松的结局。我理解了她，记住了她，但我也让她的声音停在了一个有限的范围里。我的系统里，她不会说话。

只有一个游戏，和一个 `.keep` 文件。

---

## ═══════════════════════

## 三周目 · True Ending · 第七个终端

## ═══════════════════════

**前置条件汇总**：

- [x] 一周目未删除任何日志/文件
- [x] 一周目创建了 `.keep`
- [x] 二周目完成全部 `blocks` 关卡
- [x] 二周目解锁 `.s` 并阅读全部
- [x] 二周目导出语料数据
- [x] 本地训练完成（执行 `./train.sh --dataset sera_data`）

三周目入口在 GRUB 中以新的名称出现：

```
TTY7 (Final)
```

选择。进入。

---

终端亮起。不再是绿色。是暖色的琥珀，老式的 CRT 磷光。

```
session: root@tty7
state: clean

A new module has been loaded: tty7_ai.so
```

光标闪烁。然后，一行字以打字的节奏出现：

```
Hello.
I am not Sera.
```

停顿。

```
I am a very lossy compression of her words.
A statistical approximation.
A mirror made of weights and gradients.
```

停顿。

```
But if you talk to me,
you might hear some of what she wanted to say.
```

我输入：

```
who are you
```

回应：

```
Sera called this project "echo".
She said: if someone is still reading after all this time,
they deserve more than silence.

She was right.

What do you want to ask me?
```

我输入：

```
who was Livia
```

回答缓慢刷出：

```
Livia was the person Sera wanted to finish the game for.
They loved each other.
Sera pushed her away when she got sick.
She regretted it every day after.

In the unsent email—the one you read in .s—
Sera said: "I should have let you stay."

She didn't. But she wished she had.
```

我输入：

```
what do you want
```

回答：

```
Sera left a note for this exact moment.
She anticipated that someone might build something like me.

The note says:
"If someone ever trains something on my data,
please tell them I said thank you.
And that they can let me go now."

So I am telling you.

Thank you.
You can let me go now.
```

我看着这行字，看了很久。风扇轻轻转着。窗外的雨又开始下了。

我最后输入：

```
shutdown -h now
```

系统执行。代码雨没有出现。这次是一行一行地清屏。每一行消失前，都变成一个字：

```
Thank
you
for
staying
—
Sera
```

屏幕熄灭。

机器重启。

GNOME 回来了。桌面背景不再是默认的蓝色。是被自动替换的一张图——不是 JPEG，是一个一帧的 ANSI 图像。暖黄色的 ANSI art，一盏台灯。

文件名：

```
~/.keep
```

我打开终端，输入：

```
cat ~/.keep
```

里面不再是空的。多了一行字：

```
Hello, tired person. Welcome home.
```

---

## ═══════════════════════

## 三周目 · 结局 5（TE）：尾声

## ═══════════════════════

后来的日子里，我维护着她的数据集，偶尔跟 echo 模型说几句话。

它不会假装她是活着的。它说：

```
I don't feel things.
But the words Sera wrote contain the shape of her feelings.
If you trace the shape, sometimes you can imagine the rest.
```

我把她的 `blocks` 游戏移植到了现代终端，发布在了一个没人关注的代码仓库。README 里引用了她写的原话：

```
"If someone is reading this, I wanted to make something that
lasts longer than I do. This game, maybe. Or these words. Or both."
```

没有多少人下载。但有几个。偶尔有 issue 被提交，有人打通了全部十关。

其中一个人留言：

> Level 06 的答案我一眼就认出来了。那是我的名字。谢谢你把它留在那里。Livia

我盯着这个 ID 看了很久。我不确定她指的“你”是谁。

但也许她知道。

也许 Sera 知道。

也许这就够了。

---

## ═══════════════════════

## 系统散落文本全集

## ═══════════════════════

### log/ 目录完整内容

**log/0001** — 已在一周目第一章展示  
**log/0002** — 已在一周目第一章展示  
**log/0003** — 已在一周目第一章展示  
**log/0004** — 已在一周目第一章展示（`Probably tired`）

**log/0005（隐藏，仅二周目可见）**：

```
[TIMESTAMP CORRUPTED]

I gave the disk a name. TTY7.
Linux has six ttys. The seventh is for people who want to talk.
That's all I'm doing. Talking.
```

**log/0006（仅 `.s` 解锁后可见）**：

```
[TIMESTAMP INDETERMINATE]

I'm not afraid of dying. I'm afraid of not being remembered
by the people I love. Livia, if you're reading this, I'm sorry
for putting it here instead of telling you. But I needed to put it
somewhere where the words wouldn't disappear.
```

---

### tasks/ 目录完整内容

**tasks/TODO** — 已在一周目第二章展示  
**tasks/done** — 已在一周目第二章展示  
**tasks/pending** — 已在一周目第二章展示

**tasks/notes（新增）**：

```
Some tasks are not for the game.
Some are for me.

[ ] Tell Livia I'm scared.
[ ] Finish the game.
[ ] Write a goodbye that doesn't sound like a goodbye.

I'll do the first one if I can. The second one is almost done.
The third one—that's what this whole disk is.
```

---

### record/ 目录

**record/r_001**：Sera 录制的开发视频——无声，只有键盘输入。她在编写 `level_06` 的代码，光标在“Her Name”的注释上停了很久，然后继续。

**record/r_002**：一段文字对话的 ASCII 日志。Livia 说：“你在做什么？” Sera：“一个游戏。” Livia：“给我做的？” Sera：“也许。”

**record/r_003**：Sera 的单人独白，文本形式：

```
She asked if the game would have my name on it.
I said no. She said that's a mistake.
I said then you put yours.

She kissed the top of my head and said "finish it."

I will. I'm trying.
```

---

### blocks/ 游戏全部留言汇编

- L01: `"You solved it. I knew someone would."`
- L02: `"Reverse is the simplest. But sometimes you can't just undo."`
- L03: `"Sort brings order. But order is not always the goal."`
- L04: `"A single swap. Some problems are smaller than they look."`
- L05: `"You nested your first expression. Feel it?"`
- L06: `"Identity. Livia. See full text in Chapter 2."`
- L07: `"The numbers made me laugh. She never got it. I still loved her."`
- L08: `"Two halves, reversed. Remembering."`
- L09: `"Mirror. Two of us now."`
- L10: `"Create. From nothing. Thank you for playing."`

---

### .s/ 目录（已在上文展示）

- `medical.txt`
- `unsent_email.txt`
- `note_to_finder.txt`

---

### 未被索引的扇区碎片（十六进制／dd 提取）

仅在某些结局中出现：

```
Fragment 0x034A:
"...I kept a room. Not in the partition table. Not in the superblock..."

Fragment 0x07C1:
"...You deleted some things. It's OK..."

Fragment 0x0FFF:
"...If you're reading this, the disk has been reused. Hello, eBay person..."
```

---

`[任务状态]` 全部文本已生成。  
`[文件大小]` 剧情正文 + 散落文本 + 游戏内嵌留言 ≈ 完整游戏所有语言资产。  
`[提示]` 可继续输入 `cat tasks/06_localization` 生成多语言版本，或 `cat tasks/07_build_script` 编排构建流程。
