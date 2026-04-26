package com.tty7.core.story;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NarrativeLoader {
    private static final Type STORY_NODE_LIST = new TypeToken<List<StoryNode>>() {
    }.getType();

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public StoryNode loadNode(String resourcePath) {
        try (InputStreamReader reader = open(resourcePath)) {
            StoryNode node = gson.fromJson(reader, StoryNode.class);
            if (node == null) {
                throw new IllegalArgumentException("Story node resource is empty: " + resourcePath);
            }
            return node;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, StoryNode> loadNodes(String resourcePath) {
        try (InputStreamReader reader = open(resourcePath)) {
            List<StoryNode> nodes = gson.fromJson(reader, STORY_NODE_LIST);
            if (nodes == null) {
                throw new IllegalArgumentException("Story node resource is empty: " + resourcePath);
            }

            Map<String, StoryNode> nodeById = new LinkedHashMap<>();
            for (StoryNode node : nodes) {
                nodeById.put(node.id(), node);
            }
            return Map.copyOf(nodeById);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStreamReader open(String resourcePath) {
        InputStream stream = NarrativeLoader.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Story resource not found: " + resourcePath);
        }
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
}
