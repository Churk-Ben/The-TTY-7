package com.tty7.core.story;

import java.util.HashMap;
import java.util.Map;

public class StoryDatabase {
    private final Map<String, StoryNode> nodes = new HashMap<>();

    public StoryDatabase(NarrativeLoader loader) {
        loadAndPutAll(loader, "/story/boot/intro.json");
        loadAndPutAll(loader, "/story/loop1/logs.json");
        loadAndPutAll(loader, "/story/loop1/tasks.json");
        loadAndPutAll(loader, "/story/loop1/s.json");
        loadAndPutAll(loader, "/story/loop1/keep.json");
        loadAndPutAll(loader, "/story/loop1/blocks_run.json");
        loadAndPutAll(loader, "/story/loop2/intro.json");
        loadAndPutAll(loader, "/story/loop2/notes.json");
        loadAndPutAll(loader, "/story/loop2/s.json");
        loadAndPutAll(loader, "/story/loop2/export.json");
        loadAndPutAll(loader, "/story/loop3/intro.json");

        loadAndPutAll(loader, "/story/endings/ending1.json");
        loadAndPutAll(loader, "/story/endings/ending2.json");
        loadAndPutAll(loader, "/story/endings/ending3.json");
        loadAndPutAll(loader, "/story/endings/ending4.json");
        loadAndPutAll(loader, "/story/endings/ending5.json");
    }

    private void loadAndPutAll(NarrativeLoader loader, String path) {
        try {
            Map<String, StoryNode> loaded = loader.loadNodes(path);
            nodes.putAll(loaded);
        } catch (Exception e) {
            System.err.println("Warning: failed to load story nodes from " + path + ": " + e.getMessage());
        }
    }

    public StoryNode getNode(String id) {
        return nodes.get(id);
    }
}
