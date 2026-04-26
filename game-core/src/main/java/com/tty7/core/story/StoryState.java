package com.tty7.core.story;

import java.util.List;

public record StoryState(
        String currentSceneId,
        List<StoryChoice> availableChoices,
        boolean isBlockingInput) {

    public static StoryState init() {
        return new StoryState(null, List.of(), false);
    }

    public StoryState withScene(String sceneId, List<StoryChoice> choices, boolean blocking) {
        return new StoryState(sceneId, List.copyOf(choices), blocking);
    }
}