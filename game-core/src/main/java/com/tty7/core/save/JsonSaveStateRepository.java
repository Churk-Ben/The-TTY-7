package com.tty7.core.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonSaveStateRepository implements SaveStateRepository {
    private final Path savePath;
    private final Gson gson;

    public JsonSaveStateRepository(Path savePath) {
        this.savePath = savePath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    @Override
    public SaveState load() {
        if (!Files.exists(savePath)) {
            return SaveState.empty();
        }

        try (Reader reader = Files.newBufferedReader(savePath, StandardCharsets.UTF_8)) {
            SaveState saveState = gson.fromJson(reader, SaveState.class);
            return saveState == null ? SaveState.empty() : saveState;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load save state from " + savePath, e);
        }
    }

    @Override
    public void save(SaveState saveState) {
        try {
            Path parent = savePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8)) {
                gson.toJson(saveState, writer);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save progress to " + savePath, e);
        }
    }
}
