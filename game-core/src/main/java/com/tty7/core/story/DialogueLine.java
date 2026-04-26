package com.tty7.core.story;

public record DialogueLine(
        String speaker,
        String text,
        String effect,
        String sfx) {

    public DialogueLine {
        speaker = speaker == null ? "" : speaker;
        text = text == null ? "" : text;
        effect = effect == null ? "" : effect;
        sfx = sfx == null ? "" : sfx;
    }
}
