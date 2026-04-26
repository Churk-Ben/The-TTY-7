package com.tty7.core.story;

import java.util.List;

public record StoryChoice(
        String id,
        String text,
        String targetNodeId,
        List<String> grantFlags,
        List<String> requireFlags) {

    public StoryChoice {
        id = id == null ? "" : id;
        text = text == null ? "" : text;
        targetNodeId = targetNodeId == null ? "" : targetNodeId;
        grantFlags = List.copyOf(grantFlags == null ? List.<String>of() : grantFlags);
        requireFlags = List.copyOf(requireFlags == null ? List.<String>of() : requireFlags);
    }
}
