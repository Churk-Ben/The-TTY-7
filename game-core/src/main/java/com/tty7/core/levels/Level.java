package com.tty7.core.levels;

import java.util.List;

import com.tty7.core.story.Narrative;

public record Level(
        int schemaVersion,
        int id,
        String title,
        String story,
        Narrative narrative,
        List<Integer> input,
        List<Integer> output,
        List<String> availableBlocks,
        List<String> forcedBlocks,
        List<List<String>> bonusCombos,
        int optimalSize,
        int timePar,
        int stepBudget,
        String discoveryHint) {

    public String briefingText() {
        if (story != null && !story.isBlank()) {
            return story;
        }
        if (narrative != null && !narrative.preStory().isEmpty()) {
            return narrative.preStory().getFirst().text();
        }
        return "";
    }
}
