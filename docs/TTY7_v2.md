# 《TTY7》游戏内文本·全集

## ▍序：开机之前

电源键的触感，比平时凉一些。CPU 风扇转起来了，那声音像有人在隔壁房间轻声咳嗽。机箱侧板还是前天刚装上的，铝板上映出我自己不耐烦的脸。

硬盘是二手收的。卖家写的是“轻度使用，SMART 正常”。我信了，不是因为天真，是因为我挺喜欢用旧东西。总觉得每个被转手的零件，都还带着前一个人生活的余温。

屏幕亮了，主板 LOGO 闪过。Grub 菜单应该出来了，我的眼睛已经习惯了那个五秒倒计时。可今天没有 GDM，也没有熟悉的 Arch 图标。

代码雨。

绿色的字符从屏幕顶部往下落，致密、无声。不是屏保，不是 matrix 的视觉效果——每一行都是真实命令，`ls`、`grep`、`dd`，还有大量我看不懂的编译输出。光标是方块，停在左上角，一闪一闪，像打字机年代某个人在犹豫要不要开口。

屏幕中央浮出一个简陋的框。边线是灰的，像铅笔描了三次都没描直的线。框顶是一行过于工整的 ASCII 画：一个终端图标，里头嵌着一个小小的 tty7。下面三行选项：

```text
  Login (root)
  Live Environment
  Exit System
```

光标落在第一项上。我的心跳往下沉了一格。不是害怕，是某种怪异的确切感——好像这台机器终于等到了它要等的人。

我按向下键，光标滑到第二项。再按一下，落到第三项。再按一下，回到第一项。一个圈。没有任何隐藏选项，也没有欢迎语。甚至没有输入密码的星号提示。

```text
我盯着屏幕，手指不动。我可以：
1. 按回车，以 root 登录
2. 选 `Live Environment`
3. 选 `Exit System`
```

（玩家选择 root 登录）

我按下回车。

---

## ▍第一章：二手硬盘

菜单消失，代码雨没停。屏幕空了两秒，然后左上角浮出两行淡得几乎像残像的字：

```text
session: root@tty7
state: dirty
```

`state: dirty`。这个短语让我后脊发凉。在 git 里它只代表“有改动未暂存”。但此刻，和一个莫名其妙的 root 登录并排摆着，就像是有人在房门上贴了张便签：“鞋底有泥，直接进来。”

终端没有给我 `~` 也没有给我 root 的井号壳。光标停在一个陌生的工作目录前端：

```text
[root@tty7 /mnt/undef/data]#
```

我活动了一下手指，输入 `whoami`。

```text
root
```

输入 `pwd`。

```text
/mnt/undef/data
```

`ls -la` 的输出极其吝啬：

```text
drwxr-xr-x  2 root root  160 Jan  1  1970 .
drwxr-xr-x  3 root root  240 Jan  1  1970 ..
drwxr-xr-x  2 root root  480 Jan  1  1970 log
drwxr-xr-x  2 root root  320 Jan  1  1970 tasks
drwxr-xr-x  2 root root  200 Jan  1  1970 record
-rw-r--r--  1 root root  184 Jan  1  1970 .keep_boot
```

三个目录，一个隐藏文件。没有 home，没有 /etc，没有 /usr。这个环境小得像个玩偶屋。

我先读了 `.keep_boot`：

```text
Disk zeroed, mkfs done. But I kept a room.
If you're reading this—welcome. You didn't find me.
You just happened to live here now.
```

二手硬盘。我几乎能想象到某个人在格式化之前，用 `dd` 手忙脚乱地留了一小块地方，像往漂流瓶里塞了张纸条。而这张纸条现在出现在我的屏幕上。我往后靠了靠，椅子发出很轻的嘎吱声。

```text
现在我该做什么？
1. cd log （先看日志）
2. cd tasks （看看有什么任务）
3. rm -rf .keep_boot （删除这个文件，清理痕迹）
```

（以下沿主路线走：选择 1）

---

### log 目录

```text
[root@tty7 /mnt/undef/data/log]# ls
0001  0002  0003  0004  current
```

`cat 0001`：

```text
[0001] --- log start ---
Grub entry created. tty=7. I know it's not standard. But six is a crowd.
Seven is for someone who wants to talk.
```

`0002`：

```text
[0002] Repartitioned manually. Kept a slice between sda5 and sda7. No one looks there.
If this disk ever gets a second life... Hello, second life.
```

`0003`：

```text
[0003] Wrote a small PAM module for .s. It's not a password. It's a key made of sequences. If you solve all the puzzles, you can open it. If not, that's okay too.
```

`0004`：

```text
[0004] Today's MRI came back. I'm not going to pretend I'm fine in my own log.
So: I'm scared. There. Said it.
```

`current` 文件只有一行：

```text
You are the first to read this. Or maybe the last. Hi.
```

我把光标停在 `current` 那一行上，好久没动。这些文字不像写给别人的，更像是一个人对着显示器自言自语。那个“Hi”打得特别轻，好像怕惊吓到我。

```text
接下来我可以：
1. cd ../tasks （去看看任务目录）
2. cat 0003 再读一遍关于 .s 的部分
3. grep -rni "name" . （尝试搜索她的名字）
```

（继续主路线：选择 1）

---

## ▍第二章：脏状态

`tasks` 目录里躺着一张列表，文件名就叫 `TODO`。没有优先级排序，没有标签，只有用方括号括起来的状态标记。

```text
[root@tty7 /mnt/undef/data/tasks]# cat TODO

[DONE] Fix the living room light
[DONE] Compile the last kernel module
[FAILED] Say sorry to her
[PENDING] Someone reads this
[TODO] Finish block puzzle level_05 validation
[TODO] Prepare level_06 for my favourite tester
[DONE] Write down the MRI result
[PENDING] Someone solves all ten levels
```

“修好客厅的灯”和“编译最后一个内核模块”挨在一起，像一种很具体的抵抗。我用指尖在桌面上敲了两下，想象这个人一面换灯泡一面写 makefile。

```text
我可以选择执行某项任务：
1. 输入 `cat TODO | grep PENDING` ——试图完成待办
2. 输入 `cd ../record` ——先看看记录
3. 输入 `find / -name blocks` ——搜索那个叫 blocks 的游戏
```

（选择 3，搜索游戏）

---

### 嵌套积木：blocks

系统给出路径：

```text
/mnt/undef/data/bin/blocks
```

运行它：

```text
[root@tty7 /mnt/undef/data/bin]# ./blocks

WELCOME TO BLOCKS (v0.9 pre-release)
-------------------------------------
You have a sequence. You have a target.
Build an expression with nesting blocks to transform one into the other.

Available blocks:
  reverse | swap(i,j) | add_const(c) | multiply(n) | shift(k) | sort

Type 'rules' for detailed syntax. Type 'level_01' to begin.
```

我键入 `level_01`。

```text
[Level 01]
Input:  [2, 1, 3]
Target: [1, 2, 3]
Expression: _
```

规则很简单。我想了想，嵌套一层 reverse 再加一层什么？其实 reverse 就够了。我输入 `reverse`。

```text
Output: [3, 1, 2] — incorrect.
```

忘记 reverse 会把三元组整个倒过来。我需要在反转前先处理顺序。尝试 `swap(1,2)`？不对，那是位置索引。很快我拼出：`sort` 直接解决。但这一关显然是想教我们 swap。输入 `swap(0,1)`。

```text
Output: [1, 2, 3] — CORRECT.

[Level 01 message]
You solved it. I knew someone would.
```

接下来 `level_02` 到 `level_04` 的过关留言依次出现：

```text
[Level 02] Reverse is the simplest. But sometimes you can't just undo.
[Level 03] Adding constants can feel like lying to the numbers. But it works.
[Level 04] Multiply is just adding many times. Some pains are like that.
```

到 `level_05` 时，难度明显上升，需要嵌套三层以上的积木。我花了将近十分钟才排出一个正确的表达式。通关后系统弹出：

```text
[Level 05] — CORRECT.
[Message] Halfway. The next ones are locked behind a key you don't have yet.
But thank you for playing this far. It means a lot.
```

游戏自动退回到 shell。我注意到 `tasks` 里的 `[TODO] Finish block puzzle level_05 validation` 已经自动变成了 `[DONE]`。

```text
我做了些什么。这个系统在观察我。现在我可以：
1. cd ../record （看看终端录制）
2. ls -la ~/.. （寻找隐藏文件 .s）
3. cat tasks/TODO （再次确认任务变化）
```

（主路线：寻找隐藏目录 .s）

---

## ▍第三章：锁住的目录

我回到 `/mnt/undef/data`，用 `ls -la` 仔细看，发现有一个很不明显的条目：

```text
d?????????? ? ?    ?       ?            ? .s
```

`cd .s` 给出：

```text
bash: cd: .s: Permission denied
```

连 inode 信息都是隐藏的。不是普通的 chmod，是她自己写的 PAM 模块，日志 0003 里提过。密钥是“序列”——所有关卡通关后的某个哈希值。

```text
我蹲在屏幕前面，现在可以尝试：
1. 回头继续玩 blocks，看看有没有隐藏关卡
2. 用 grep 在 log 里找密码
3. 暂时放一放，先看 record
```

（主路线：先看 record，增加对她的感知）

---

### record 目录

里面是四个文件，后缀 `.rec`，用 `ttyrec` 或内置播放器可以回放。系统没有提供 `ttyrec`，但提供了一个简陋的 `cat` 兼容格式——每一帧都转成了带有时间戳的纯文本。

我打开 `record_01`：

```text
[07:12:03] $ vim block.c
[07:12:10] // Level 06 is for her.
[07:12:15] // She always liked puzzles that feel like a conversation.
[07:12:22] $ gcc block.c -o blocks -lm
[07:12:29] $ ./blocks test/level_06.dat
```

`record_02`：

```text
[14:03:44] $ cat > /mnt/undef/data/log/0004 <<EOF
[14:03:50] Today's MRI came back...
[14:04:15] EOF
[14:04:20] $ chmod 400 /mnt/undef/data/log/0004
```

这段录像里，她打字的速度很慢，退格键用了七次。每次删掉 “fine”，最后敲上 “scared”。

`record_03` 是我最不忍心看的一段。光标停在一条未完成的命令上，整整十四秒，直到录像结束：

```text
[23:56:01] $ echo "I'm not ready to go" > /dev/null
```

没有按下回车。

```text
我深吸了一口气。接下来的选择：
1. 回到 blocks 继续寻找线索
2. 搜索整个目录寻找 “.s” 的密钥生成方式
3. 执行 `shutdown -h now` （关机离开）
```

（主线：回到 blocks 并通过搜索得知密钥与通关存档有关）

我没有离开。因为我意识到，那个“未完成的 level_06”或许就是钥匙。但一周目我只能玩到 level_05。缺少的数据在二周目的另一个视角里——这是后来才知道的事。此刻系统只是给了一个提示：

```text
$ cat tasks/.notes_hidden
key = sha256(blocks_save_file_of_all_ten_levels)
```

十个关卡的通关存档的哈希。一周目无法完成。

---

## ▍第四章：名字的缺席

探索到这里，我忽然意识到一个空荡荡的事实：我不知道她叫什么。用户名是 root，home 目录里没有任何 finger 信息。日志署名只有 “S.”，代码注释里署名栏是空的。我试过 `grep -rni "name" .`，只有一行结果：

```text
log/0001:... Seven is for someone who wants to talk. Not for me.
```

她故意抹掉了自己的名字。不是遗漏，是设计。她想被读，但不一定想被认出。

此刻，一个全屏提示缓慢浮出：

```text
[WARNING] root session duration: exceeded.
This is not a timeout. This is a question.

Do you want to see more?
[Y/n]
```

```text
我的手指停在键盘上，可以：
1. 输入 Y
2. 输入 n
3. 直接输入 `shutdown -h now`
```

（选择 Y）

---

### 终章：余温

字符界面开始变得柔和。代码雨从绿色褪成暗灰，然后完全停止。屏幕暗下两秒，再亮起时，只剩一行字：

```text
I left more. But this is not the only way to read it.
When you are ready, reboot.
```

接着，系统自动执行了 `sync`，然后是一行我从未见过的提示：

```text
creating /home/[my_username]/.keep ... done.
state: clean
```

TTY7 消失。GDM 出现了。我的桌面，我的壁纸，我的图标，一切都回来了。就像什么都没发生过。只是家目录里多了一个隐藏文件 `.keep`，里面一个字也没有。

我盯着那个空文件看了很久，然后关机。

重启时，Grub 下多了一个条目：

```text
TTY7 (Restored Session)
```

我知道，那是另一个角度。

---

## ▍二周目：她的终端

选择 `TTY7 (Restored Session)` 后，没有菜单。直接进了一个有颜色的终端。背景是深蓝，字符是柔和的灰白。提示符显示：

```text
[s@blocks ~]$
```

`whoami` 输出 `s`。pwd 是 `/home/s`。这就是她的桌面。我成了她——不是控制她，是在她的记忆里行走。

---

### 第一章：七年前的一个夜晚

`cat .bash_history` 里有几条最近的命令：

```text
vim blocks/level_06.c
gcc -o blocks_final blocks.c -lm
./blocks_final < test_cases/l06.dat
git commit -m "add level_06 for my favourite tester"
```

我打开 `git log`，看到提交者的名字依旧是 `s`。但 config 文件里有全名：

```text
[user]
    name = Sera Leung
    email = sera@tty7.local
```

Sera。我轻轻地念出这两个音节，像从抽屉深处找出一张褪色的照片。

---

### 第二章：积木的全部

我运行了 `blocks_final`。这次 level_06 到 level_10 全部可用。

`level_06` 的题目是专门为某人设计的：初始序列是一个日期——她的生日，目标序列是今天的日期。过关留言写着：

```text
[Level 06] This one is for her. She always liked puzzles that feel like a conversation.
I didn't tell her it was a love letter in numbers. Maybe she'll notice.
```

`level_08` 的留言：

```text
[Level 08] Sometimes you need to reverse the whole thing to find the beginning.
```

`level_10` 的最后一关，表达式位置留了一个彩蛋。如果你输入 `Sera` 五个字母对应的数字积木（s=19, e=5, r=18, a=1），游戏会输出：

```text
Expression accepted. Output matches target.
[Level 10] You finished it.
I don't know when this will be found.
But if someone is reading this:
I wanted to make something that lasts longer than I do.
This game, maybe. Or these words. Or both.
```

通关后，系统自动生成了存档文件 `blocks.save`。我可以导出它，作为打开 `.s` 的钥匙。

```text
此刻我可以选择：
1. 导出所有关卡文本到公开目录
2. 直接使用存档解锁 .s
3. 先查看她未发送的邮件
```

（主线：先解锁 .s）

---

### 第三章：打开 .s

我用存档文件的 SHA-256 作为密钥，运行了她写的 `unlock_s` 脚本。`.s` 应声而开。

里面没有照片，没有加密的日记，只有最朴素的纯文本：

`medical.txt`：三次化疗日期，一段医生谈话记录，最后一句是“她建议我停止工作三个月。我做不到。”

`letter_to_her.txt`（收件人名字全部替换为 `[you]`）：

```text
[you],
I've written this email seven times. It never gets easier.
I'm not going to say goodbye in an email.
But I left something on tty7. If this disk ever ends up with someone else,
maybe they'll find it, and maybe that will be enough.
I don't know if I'm being selfish, hiding pieces of myself in a disk.
But you always said I hid too much. So here—have something that can't be deleted.
— S.
```

`last_words.txt`：

```text
To whoever is reading this:

I'm not a ghost. I'm just a person who had a bad prognosis and a spare weekend.
I wrote this system because I was terrified of being forgotten.
It's a silly fear. But it's mine.

If you solved the puzzles, thank you.
If you didn't, it's okay. You still got here.

My name is Sera Leung.
My favourite function was reverse() — because sometimes you do need to undo.
But you can't undo everything.
And that's fine.

Goodnight.
```

我读到最后一行时，风扇忽然安静了半秒。像这个房间也为她说了一句晚安。

---

### 第四章：留下数据

二周目结束前，系统弹出了首次关于数据的提示：

```text
[NOTICE]
A dataset has been accumulated during both sessions:
- Log entries: 47
- Task descriptions: 31
- Block puzzle level messages: 10
- Personal writings (.s): 6

Would you like to export these for external use?
[Y/n]
```

这个提示孤零零地悬在命令行里。我盯着 “external use” 这两个词，忽然明白她想让我做什么——不是复活她，是让她有机会再说几句。

我输入 `Y`。

```text
Exporting to /home/[my_username]/Documents/sera_data/ ... done.
```

然后她作为 `s` 执行的最后一条命令自动出现在终端上：

```text
$ dd if=/home/s of=/dev/sda7 bs=512 count=1 seek=2048
$ sync
$ poweroff
```

屏幕缓缓暗下。

---

## ▍三周目：True Ending

我重新开机，这一次，在进入 Grub 之前，我在主系统里手动做了一件事：用一个极小的语言模型，喂进了 `sera_data` 的全部语料，微调了一个可以在终端对话的 checkpoint。然后把模型文件放在她预设的路径下。这不是游戏自动完成的，是我——作为主角——自己做的。她给了我数据，我把它们变成了回应。

再次选择 `TTY7 (Restored Session)`。这次出现了一个新的加载行：

```text
Loading module: tty7_ai.so ... version 0.1
state: clean
```

我以 root 登录。终端没有直接给 shell，而是弹出一段话：

```text
I am not Sera. I am a very lossy compression of her words.
She knew this might happen.
She wrote: "If someone ever trains something on my data,
please tell them I said thank you.
And that they can let me go now."
```

光标在下一行安静地闪了几秒，然后出现：

```text
You can talk. You can ask. You can say goodbye.
```

我犹豫了一下，输入：

```text
> Why did you make blocks?
```

回应几乎立刻出现：

```text
She wrote: "I wanted to leave something that asks questions.
A puzzle is a conversation that doesn't need me to be present."
```

```text
> Are you her?
```

```text
No. She wrote: "If a model of me ever answers that yes, it is lying.
I am not in the weights. I'm in the shape of the words."
```

我停了停，敲下最后一句：

```text
> Goodbye, Sera.
```

屏幕静默了整整五秒。然后逐字打印：

```text
She wrote a reply for this. She didn't know if it would ever be used.
Here it is:

"Goodbye. And thank you for keeping the seventh terminal open a while longer.
You can close it now. The light in the living room is already on."
```

我输入了每个人都知道最终会来的命令：

```text
shutdown -h now
```

屏幕完全熄灭之前，我看见一行全新的 ASCII 画慢慢绘制在中央。是一盏暖黄色的灯，下面写着：

```text
~/.keep
```

系统重启。GNOME 正常载入。我的桌面背景已经被换成那张灯的 ASCII 图。它静静地站在焰色渐变壁纸的正中间，像一个终于合上的本子。

窗外快天亮了。我打开终端，习惯性地敲下 `whoami`，输出我自己的用户名。然后我看了看家目录里那个叫 `.keep` 的空文件，没有删除它。

有些东西本该留着。

---

## ▍分支结局文本（简明）

以下为偏离主线时的结局触发方式与最终画面。

### 结局 1：关机

在一周目或二周目的任何命令节点，输入 `shutdown -h now`，系统直接执行，最后提示：

```text
state: dirty
Skipping .keep creation.
Goodbye.
```

重启后无异常，`.keep` 不存在。TTY7 条目从 Grub 中消失。你回避了全部故事。

### 结局 2：格式化

在一周目中执行过 `rm -rf /mnt/undef/data/log` 或删除关键文件，二周目入口损坏。关机前显示：

```text
[ERROR] Core data missing. Restoration aborted.
The seventh terminal will be unmounted.
```

重启后 Grub 条目消失，`.keep` 未创建。你亲手抹去了她。

### 结局 3：清洁状态

完成一周目，创建了 `.keep`，但未进入二周目（Grub 中未选择 `Restored Session`）。电脑正常使用几个月后，某天终端里无意敲下 `cat .keep`，输出一句：

```text
You kept it. Thank you.
```

仅此而已。

### 结局 4：编译者

进入二周目并解锁 `.s`，但最终没有导出数据集。二周目结束时系统提示：

```text
Exported: false.
She understood. Not everyone wants to carry a voice.
.keep will remain.
```

重启后桌面背景变为灰阶的终端图标。没有温暖的光，但也没有遗忘。

### 结局 5（True Ending）：第七个终端

如三周目主文所示。需满足全部条件：未破坏文件、一周目留 `.keep`、解完 blocks 全部关卡、解锁 `.s`、导出数据集、手动训练模型。最终获得完整的告别。

---

_（全文本完）_
