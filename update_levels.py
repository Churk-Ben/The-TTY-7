import json
import os

base_path = r'e:\The-TTY-7\game-core\src\main\resources\levels'

levels = [
    {
        'id': 1, 'title': 'First Step',
        'input': [3, 1, 2], 'output': [1, 2, 3],
        'available_blocks': ['_INPUT_', 'Sort'],
        'forced_blocks': [],
        'post_story': 'You solved it. I knew someone would.'
    },
    {
        'id': 2, 'title': 'Undo',
        'input': [5, 4, 3, 2, 1], 'output': [1, 2, 3, 4, 5],
        'available_blocks': ['_INPUT_', 'Reverse'],
        'forced_blocks': [],
        'post_story': "Reverse is the simplest. But sometimes you can't just undo."
    },
    {
        'id': 3, 'title': 'Order in Chaos',
        'input': [7, 2, 5, 1], 'output': [1, 2, 5, 7],
        'available_blocks': ['_INPUT_', 'Sort', 'Reverse'],
        'forced_blocks': [],
        'post_story': 'Sort brings order. But order is not always the goal. Wait for later levels.'
    },
    {
        'id': 4, 'title': 'Swap Meet',
        'input': [1, 3, 2, 4], 'output': [1, 2, 3, 4],
        'available_blocks': ['_INPUT_', 'Swap'],
        'forced_blocks': [],
        'post_story': 'A single swap. Some problems are smaller than they look.'
    },
    {
        'id': 5, 'title': 'First Nest',
        'input': [3, 1, 2], 'output': [3, 2, 1],
        'available_blocks': ['_INPUT_', 'Reverse', 'DropFirst', 'Prepend'],
        'forced_blocks': [],
        'post_story': "You nested your first expression. Feels different, doesn't it.\nLike building a sentence instead of saying a word."
    },
    {
        'id': 6, 'title': 'Her Name',
        'input': [12, 9, 22, 9, 1], 'output': [12, 9, 22, 9, 1],
        'available_blocks': ['_INPUT_', 'Ident'],
        'forced_blocks': [],
        'post_story': "The input is the same as the target.\nThe solution is identity.\n\nI know this looks like a trick. It's not.\nThis level is for L.\n\nL—those numbers are your name. A=1 Z=26.\nL=12, I=9, V=22, I=9, A=1. LIVIA.\n\nThat's you.\n\nYou don't need to change anything to be the answer.\nYou just need to be.\n\nI wanted to put this in a game so it would last.\nEven if I don't."
    },
    {
        'id': 7, 'title': 'Recursion',
        'input': [4, 8, 15, 16, 23, 42], 'output': [4, 8, 15, 16, 23, 42],
        'available_blocks': ['_INPUT_', 'AddConst', 'Ident'],
        'forced_blocks': [],
        'post_story': 'I put this sequence in because it made me laugh.\nLivia never got it. She said "what is that, your hard drive serial number?"\n\nI still loved her. I love her even now. In whatever tense applies.'
    },
    {
        'id': 8, 'title': 'Two Halves',
        'input': [1, 2, 3, 4], 'output': [4, 3, 2, 1],
        'available_blocks': ['_INPUT_', 'Concat', 'TakeHalf', 'DropHalf', 'Reverse'],
        'forced_blocks': [],
        'post_story': "Two halves. Each reversed. Then put back together.\n\nThat's what remembering is. You take the pieces of someone,\nrun them backward in time, and fit them next to each other.\nIt's not perfect. But it's something."
    },
    {
        'id': 9, 'title': 'Mirror',
        'input': [1], 'output': [1, 1],
        'available_blocks': ['_INPUT_', 'Concat', 'Mirror'],
        'forced_blocks': [],
        'post_story': "Everything is just itself, doubled.\nIf you're reading this, there are two of us here now.\nOne that wrote. One that read.\n\nThat's not nothing."
    },
    {
        'id': 10, 'title': 'Final Block',
        'input': [], 'output': [1],
        'available_blocks': ['_INPUT_', 'Create'],
        'forced_blocks': [],
        'post_story': "An empty sequence. The target is just [1].\n\nCreation from nothing.\n\nThat's what I've been trying to do this whole time.\nA game, a disk, a message, a .keep file.\nSomething where there was nothing.\n\nI wanted to make something that would last longer than I would.\n\nIf you're reading this, I did.\n\nThank you for playing.\n— Sera\n\nP.S. Livia—if you ever find this by some miracle:\nI finished it. I finally finished something."
    }
]

for level in levels:
    fpath = os.path.join(base_path, f'level-{level["id"]}.json')
    if os.path.exists(fpath):
        with open(fpath, 'r', encoding='utf-8') as f:
            data = json.load(f)
    else:
        data = {'schema_version': 1}
    
    data['id'] = level['id']
    data['title'] = level['title']
    data['input'] = level['input']
    data['output'] = level['output']
    data['available_blocks'] = level['available_blocks']
    data['forced_blocks'] = level['forced_blocks']
    data['optimal_size'] = 5
    data['time_par'] = 30
    data['step_budget'] = 50000
    
    if 'narrative' not in data:
        data['narrative'] = {
            'pre_story': [],
            'post_story': [],
            'choices': [],
            'grant_flags': [],
            'require_flags': []
        }
    
    post_lines = level['post_story'].split('\n')
    data['narrative']['post_story'] = [
        {'speaker': 'S.', 'text': line, 'effect': 'typewriter', 'sfx': ''} for line in post_lines if line.strip()
    ]
    
    with open(fpath, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

print("done")
