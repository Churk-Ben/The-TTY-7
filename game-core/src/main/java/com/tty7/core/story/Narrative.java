package com.tty7.core.story;

import java.util.List;

public record Narrative(
        List<DialogueLine> preStory,
        List<DialogueLine> postStory,
        List<StoryChoice> choices,
        List<String> grantFlags,
        List<String> requireFlags) {

    public Narrative {
        preStory = List.copyOf(preStory == null ? List.<DialogueLine>of() : preStory);
        postStory = List.copyOf(postStory == null ? List.<DialogueLine>of() : postStory);
        choices = List.copyOf(choices == null ? List.<StoryChoice>of() : choices);
        grantFlags = List.copyOf(grantFlags == null ? List.<String>of() : grantFlags);
        requireFlags = List.copyOf(requireFlags == null ? List.<String>of() : requireFlags);
    }
}
