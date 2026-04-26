package com.tty7.core.story;

import java.util.List;

public record StoryNode(
        String id,
        List<String> contentLines,
        List<String> storyLines,
        List<StoryChoice> choices,
        List<String> grantFlags,
        List<String> requireFlags) {

    public StoryNode {
        id = id == null ? "" : id;
        contentLines = List.copyOf(contentLines == null ? List.<String>of() : contentLines);
        storyLines = List.copyOf(storyLines == null ? List.<String>of() : storyLines);
        choices = List.copyOf(choices == null ? List.<StoryChoice>of() : choices);
        grantFlags = List.copyOf(grantFlags == null ? List.<String>of() : grantFlags);
        requireFlags = List.copyOf(requireFlags == null ? List.<String>of() : requireFlags);
    }
}
