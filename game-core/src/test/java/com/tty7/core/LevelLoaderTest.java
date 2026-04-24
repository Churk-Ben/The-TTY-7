package com.tty7.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;

import com.tty7.core.levels.Level;
import com.tty7.core.levels.LevelLoader;

class LevelLoaderTest {
    @Test
    void shouldLoadFirstFourLevels() {
        LevelLoader loader = new LevelLoader();
        List<Level> levels = loader.loadRange(1, 4);
        assertEquals(4, levels.size());
        assertEquals("Hello Sort", levels.get(0).title());
        assertFalse(levels.get(1).availableBlocks().isEmpty());
    }
}
