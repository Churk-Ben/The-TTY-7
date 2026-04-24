package com.algoblock.gl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import javazoom.jl.player.Player;

class AudioPlaybackSmokeTest {
    @Test
    void titleBgmResourceCanBeOpenedAndDecoded() throws Exception {
        try (InputStream is = AudioPlaybackSmokeTest.class.getResourceAsStream(
                "/assets/audio/bgm/nickpanekaiassets-stealth-breacher-tense-title-screen-218087.mp3")) {
            assertNotNull(is, "title bgm resource should exist");
            Player player = new Player(is);
            assertTrue(player.play(1), "title bgm should decode at least one frame");
            player.close();
        }
    }
}
