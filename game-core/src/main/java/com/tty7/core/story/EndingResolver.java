package com.tty7.core.story;

import com.tty7.core.save.SaveState;

public class EndingResolver {

    public static String determineEnding(SaveState saveState) {
        if (saveState.endingId() != null) {
            return saveState.endingId();
        }

        // Loop 1 endings
        if (saveState.loop() == 1) {
            if (saveState.hasFlag(BranchResolver.FLAG_DELETED_LOGS)) {
                return BranchResolver.ENDING_02_FORGET;
            }
            if (saveState.hasFlag(BranchResolver.FLAG_ABORTED_SESSION)) {
                return BranchResolver.ENDING_01_SHUTDOWN;
            }
            if (saveState.hasFlag(BranchResolver.FLAG_CREATED_KEEP)) {
                // By default, just waiting. If loop 2 is never entered...
                return BranchResolver.ENDING_03_CLEAN;
            }
        }

        // Loop 2 endings
        if (saveState.loop() == 2) {
            if (!saveState.hasFlag(BranchResolver.FLAG_EXPORTED_DATASET)
                    && saveState.hasFlag(BranchResolver.FLAG_UNLOCKED_SECRET_DIR)) {
                return BranchResolver.ENDING_04_COMPILER;
            }
        }

        // Loop 3 endings
        if (saveState.loop() == 3) {
            if (saveState.hasFlag(BranchResolver.FLAG_COMPLETED_FINAL_QA)) {
                return BranchResolver.ENDING_05_TRUE;
            }
        }

        return null;
    }
}