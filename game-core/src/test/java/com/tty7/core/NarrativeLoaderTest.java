package com.tty7.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tty7.core.story.NarrativeLoader;
import com.tty7.core.story.StoryNode;

import java.util.Map;

import org.junit.jupiter.api.Test;

class NarrativeLoaderTest {
    @Test
    void shouldLoadLoopOneStoryResources() {
        NarrativeLoader loader = new NarrativeLoader();

        Map<String, StoryNode> bootNodes = loader.loadNodes("/story/boot/intro.json");
        Map<String, StoryNode> logNodes = loader.loadNodes("/story/loop1/logs.json");
        Map<String, StoryNode> tasksNodes = loader.loadNodes("/story/loop1/tasks.json");
        Map<String, StoryNode> keepNodes = loader.loadNodes("/story/loop1/keep.json");

        StoryNode bootIntro = bootNodes.get("boot.intro");

        assertEquals("boot.intro", bootIntro.id());
        assertFalse(bootIntro.storyLines().isEmpty());
        assertTrue(logNodes.containsKey("loop1.log.read_0004"));
        assertTrue(logNodes.get("loop1.log.read_0004").grantFlags().contains("FLAG_READ_LOG_0004"));
        assertTrue(tasksNodes.containsKey("loop1.tasks.todo_1"));
        assertTrue(keepNodes.containsKey("loop1.touch.keep"));
    }
}
