package com.tty7.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tty7.core.save.JsonSaveStateRepository;
import com.tty7.core.save.SaveState;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonSaveStateRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldRoundTripSaveState() {
        Path savePath = tempDir.resolve("save-state.json");
        JsonSaveStateRepository repository = new JsonSaveStateRepository(savePath);
        SaveState saveState = SaveState.empty()
                .withFlags(List.of("CREATED_KEEP", "OPENED_TASKS"))
                .withSeenNodes(List.of("boot.login_root", "loop1.keep.created"))
                .withSolvedExpression(3, "Map<_INPUT_>")
                .withEnding("ENDING_3_CLEAN");

        repository.save(saveState);
        SaveState loaded = repository.load();

        assertEquals(1, loaded.loop());
        assertEquals("ENDING_3_CLEAN", loaded.endingId());
        assertTrue(loaded.flags().contains("CREATED_KEEP"));
        assertTrue(loaded.flags().contains("OPENED_TASKS"));
        assertTrue(loaded.seenNodes().contains("boot.login_root"));
        assertTrue(loaded.seenNodes().contains("loop1.keep.created"));
        assertEquals(Map.of(3, "Map<_INPUT_>"), loaded.solvedExpressions());
        assertEquals(Set.of("CREATED_KEEP", "OPENED_TASKS"), loaded.flags());
    }
}
