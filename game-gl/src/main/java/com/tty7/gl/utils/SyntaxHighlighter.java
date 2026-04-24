package com.tty7.gl.utils;

import com.tty7.gl.renderer.core.TerminalBuffer;

public class SyntaxHighlighter {
    public void highlight(TerminalBuffer buffer, int startCol, int row, String source) {
        int cursor = startCol;
        for (char c : source.toCharArray()) {
            int fg = Character.isLetter(c) || c == '_' ? 0x79C0FF : 0xCDD9E5;
            buffer.set(cursor, row, c, fg, 0x0D1117);
            cursor++;
            if (TextUtil.isWideCodePoint(c)) {
                buffer.set(cursor, row, '\0', fg, 0x0D1117);
                cursor++;
            }
        }
    }
}
