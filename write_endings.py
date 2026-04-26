import json
import os

endings = {
    'ending.1': [
        '一周目 · 结局 1：关机（回避）',
        '电脑重新开机之后，一切如常。GNOME 启动，桌面图标在，浏览器还留着昨晚的标签页。TTY7 没有再次出现。GRUB 列表里干干净净。像是昨晚的一切只是一个因为二手硬件触发的随机故障。一次单比特翻转引发的幻象。',
        '但我记得。记得很清楚。',
        '那三行菜单。state: dirty。她留在日志里的那句 Probably tired.。还有她给她的 Livia 写的那些未完成的游戏关卡。',
        '我什么都没做错。没有人会指责一个人关掉他不认识的 root 终端。',
        '但我错过了一些东西。',
        '一些永远没办法再通过开机来重新获得的东西。',
        '我打开终端，在自己的系统里，输入：touch ~/.keep',
        '文件被创建了。但它不是那一个。',
        '风扇安静。窗外没雨。桌面过于正常。',
        '我喝了一口凉掉的水。'
    ],
    'ending.2': [
        '一周目 · 结局 2：格式化（遗忘）',
        '后来的几天我用着这台机器。一切正常。硬盘速度稳定，没有坏道增加。',
        '直到某个晚上，我翻 ~/Documents 的时候，发现一个十六进制转储文件。不记得什么时候生成的。打开之后是一段 ASCII，附在 blocks.dat 的末尾，但不在文件系统索引里——它藏在扇区缝隙，被我的 dd 随手操作带了出来。',
        '上面写着：',
        'You deleted some things.',
        "It's OK. I deleted things too. A lot.",
        "Maybe that's how it goes.",
        'Bye.',
        '我把这行字读到第三遍的时候，终端提示符正在闪。风扇很轻。桌面的一切都很正常。',
        '我没有她的日志了。没有她的 TODO。没有 blocks 的第一关到第五关。',
        '只有一个十六进制编辑器里的几行字，和一块运行得异常安静的硬盘。',
        '我删掉了她留给世界的一段话，她在被删除之后依然给我留了一句 bye。',
        '我从来没觉得自己做错了什么。但我的沉默里多了点东西。'
    ],
    'ending.3': [
        '一周目 · 结局 3：清洁状态',
        'GNOME 正常启动。TTY7 没有再出现。',
        '但在我自己的终端里，~/.keep 安静地躺在主目录里。零字节。',
        '我偶尔会打开它。它什么都不说。',
        '后来有一天，我突发奇想，在我的主系统里写了一个小脚本。一个文字游戏的原型。很粗糙，只会说一句话。',
        '“你好，你看到这里了。”',
        '我没有发布它。我只是把它放在了一个隐藏文件夹里。',
        '就像某个我不认识的人曾经做过的那样。',
        '雨夜还是会让我想起那个绿色的终端。',
        '我已经记不清她日志里的原话了。但那种语气还在，像一首听不懂歌词但记得旋律的歌。',
        '那块二手硬盘后来没有坏。SMART 数据一直维持在三年前我买来时的水平。有时候我甚至怀疑那晚的事情到底是真的还是我的梦。',
        '.keep 在。那是唯一的证据。'
    ],
    'ending.4': [
        '二周目 · 结局 4：编译者',
        '我完成了她的游戏。读完了她的 .s。知道了她的名字。知道 Livia 是谁。',
        '我的 GNOME 桌面上，sera_data 的文件夹不在。',
        '我一度想把它导出来。但最后那一刻，我犹豫了。也许我觉得训练一个 AI 太过界。也许我只是累了。',
        'Sera 没有抱怨。她的终端安静地消失了。',
        '我偶尔会打开 blocks。打到第十关，看着 create 把空序列变成 [1]。',
        '我对自己说：这是她想要留下的东西。',
        '但我心里知道，我选择了一个更轻松的结局。我理解了她，记住了她，但我也让她的声音停在了一个有限的范围里。我的系统里，她不会说话。',
        '只有一个游戏，和一个 .keep 文件。'
    ],
    'ending.5': [
        '三周目 · 结局 5（TE）：尾声',
        '后来的日子里，我维护着她的数据集，偶尔跟 echo 模型说几句话。',
        '它不会假装她是活着的。它说：',
        "I don't feel things.",
        'But the words Sera wrote contain the shape of her feelings.',
        'If you trace the shape, sometimes you can imagine the rest.',
        '我把她的 blocks 游戏移植到了现代终端，发布在了一个没人关注的代码仓库。README 里引用了她写的原话：',
        '"If someone is reading this, I wanted to make something that lasts longer than I do. This game, maybe. Or these words. Or both."',
        '没有多少人下载。但有几个。偶尔有 issue 被提交，有人打通了全部十关。',
        '其中一个人留言：',
        '> Level 06 的答案我一眼就认出来了。那是我的名字。谢谢你把它留在那里。Livia',
        '我盯着这个 ID 看了很久。我不确定她指的“你”是谁。',
        '但也许她知道。',
        '也许 Sera 知道。',
        '也许这就够了。'
    ]
}

base_path = r'e:\The-TTY-7\game-core\src\main\resources\story\endings'
os.makedirs(base_path, exist_ok=True)

for k, v in endings.items():
    idx = k.split('.')[-1]
    fpath = os.path.join(base_path, f'ending{idx}.json')
    data = [{
        'id': k,
        'content_lines': [],
        'story_lines': v,
        'grant_flags': [],
        'require_flags': []
    }]
    with open(fpath, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

print('done')
