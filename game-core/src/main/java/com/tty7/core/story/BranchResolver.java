package com.tty7.core.story;

import com.tty7.core.save.SaveState;
import java.util.List;

public class BranchResolver {

    // First Loop
    public static final String FLAG_READ_LOG_0004 = "READ_LOG_0004";
    public static final String FLAG_DELETED_LOGS = "DELETED_LOGS";
    public static final String FLAG_OPENED_TASKS = "OPENED_TASKS";
    public static final String FLAG_OPENED_RECORDS = "OPENED_RECORDS";
    public static final String FLAG_SEARCHED_NAME = "SEARCHED_NAME";
    public static final String FLAG_SAW_LOCKED_S = "SAW_LOCKED_S";
    public static final String FLAG_CREATED_KEEP = "CREATED_KEEP";
    public static final String FLAG_ABORTED_SESSION = "ABORTED_SESSION";

    // Second Loop
    public static final String FLAG_KNOW_SERA_NAME = "KNOW_SERA_NAME";
    public static final String FLAG_UNLOCKED_SECRET_DIR = "UNLOCKED_SECRET_DIR";
    public static final String FLAG_READ_MEDICAL = "READ_MEDICAL";
    public static final String FLAG_READ_UNSENT_EMAIL = "READ_UNSENT_EMAIL";
    public static final String FLAG_READ_FINDER_NOTE = "READ_FINDER_NOTE";
    public static final String FLAG_EXPORTED_DATASET = "EXPORTED_DATA";

    // Third Loop
    public static final String FLAG_TRAINED_ECHO = "TRAINED_ECHO";
    public static final String FLAG_ENTERED_FINAL_TTY7 = "ENTERED_FINAL_TTY7";
    public static final String FLAG_COMPLETED_FINAL_QA = "COMPLETED_FINAL_QA";

    // Endings
    public static final String ENDING_01_SHUTDOWN = "ENDING_01_SHUTDOWN";
    public static final String ENDING_02_FORGET = "ENDING_02_FORGET";
    public static final String ENDING_03_CLEAN = "ENDING_03_CLEAN";
    public static final String ENDING_04_COMPILER = "ENDING_04_COMPILER";
    public static final String ENDING_05_TRUE = "ENDING_05_TRUE";

    public static CommandResult resolveCommand(String command, SaveState state) {
        if (command.isEmpty()) {
            return CommandResult.output("");
        }

        if ("shutdown -h now".equals(command) || "poweroff".equals(command)) {
            if (state.loop() == 1) {
                if (state.hasFlag(FLAG_DELETED_LOGS)) {
                    return CommandResult.shutdown("ending.2");
                }
                return CommandResult.shutdown("ending.1");
            } else if (state.loop() == 3) {
                return CommandResult.shutdown("ending.5");
            } else if (state.loop() == 2) {
                return CommandResult.shutdown("ending.4");
            } else {
                return CommandResult.shutdownAndLogin();
            }
        }

        if (state.loop() == 1) {
            return resolveLoop1(command, state);
        } else if (state.loop() == 2) {
            return resolveLoop2(command, state);
        } else if (state.loop() == 3) {
            return resolveLoop3(command, state);
        }

        return CommandResult.notFound(command);
    }

    private static CommandResult resolveLoop1(String command, SaveState state) {
        if ("pwd".equals(command))
            return CommandResult.output("/home/.tty7/root");
        if ("whoami".equals(command))
            return CommandResult.output("root");
        if ("ls".equals(command) || "ls -la".equals(command))
            return CommandResult.output("log    tasks    record    bin    .s");

        if ("cat log/0001".equals(command) || "cat ./log/0001".equals(command))
            return CommandResult.targetNode("loop1.log.read_0001");
        if ("cat log/0002".equals(command) || "cat ./log/0002".equals(command))
            return CommandResult.targetNode("loop1.log.read_0002");
        if ("cat log/0003".equals(command) || "cat ./log/0003".equals(command))
            return CommandResult.targetNode("loop1.log.read_0003");
        if ("cat log/0004".equals(command) || "cat ./log/0004".equals(command))
            return CommandResult.targetNode("loop1.log.read_0004");

        if ("rm -rf log".equals(command) || "rm -rf ./log".equals(command)) {
            return CommandResult.outputAndGrant(List.of("OK."), List.of(FLAG_DELETED_LOGS));
        }

        if (command.startsWith("grep -rni \"name\"") || command.startsWith("grep -rni name")) {
            return CommandResult.targetNode("loop1.grep.name");
        }

        if ("cat tasks/TODO".equals(command) || "cat ./tasks/TODO".equals(command)) {
            if (state.hasFlag(FLAG_SEARCHED_NAME) || state.hasFlag("FLAG_NO_NAME_PROMPT")) {
                return CommandResult.targetNode("loop1.tasks.todo_2");
            }
            return CommandResult.targetNode("loop1.tasks.todo_1");
        }
        if ("cat tasks/done".equals(command) || "cat ./tasks/done".equals(command))
            return CommandResult.targetNode("loop1.tasks.done");
        if ("cat tasks/pending".equals(command) || "cat ./tasks/pending".equals(command))
            return CommandResult.targetNode("loop1.tasks.pending");

        if ("cd .s".equals(command)) {
            if (state.hasFlag("FLAG_SAW_LOCKED_S_NOTE")) {
                return CommandResult.targetNode("loop1.s.denied_after_todo");
            } else {
                return CommandResult.targetNode("loop1.s.denied");
            }
        }

        if ("touch .keep".equals(command) || "touch ~/.keep".equals(command)) {
            if (!state.hasSolvedLevel(5)) {
                return CommandResult.shutdownNodeAndEnding("loop1.touch.keep", "ending.3");
            }
            return CommandResult.shutdownNodeAndLogin("loop1.touch.keep");
        }

        if ("./blocks".equals(command) || "blocks".equals(command)) {
            return CommandResult.openGame("loop1.blocks.run");
        }

        return CommandResult.notFound(command);
    }

    private static CommandResult resolveLoop2(String command, SaveState state) {
        if ("pwd".equals(command))
            return CommandResult.output("/home/s");
        if ("whoami".equals(command))
            return CommandResult.output("s");
        if ("hostname".equals(command))
            return CommandResult.output("sera-box");
        if ("ls".equals(command) || "ls -la".equals(command))
            return CommandResult.output("workspace    documents    .local    .bashrc    .gitconfig");
        if ("ls workspace".equals(command) || "ls ./workspace".equals(command))
            return CommandResult.output("blocks    notes    drafts");

        if ("cat workspace/notes/log_blocks.txt".equals(command)
                || "cat ./workspace/notes/log_blocks.txt".equals(command)) {
            return CommandResult.targetNode("loop2.notes.log_blocks");
        }
        if ("cat .gitconfig".equals(command) || "cat ~/.gitconfig".equals(command)) {
            return CommandResult.targetNode("loop2.gitconfig");
        }
        if ("./blocks".equals(command) || "blocks".equals(command)) {
            return CommandResult.openGame(null);
        }
        if ("unlock_s".equals(command) || "./unlock_s".equals(command) || "cd .s".equals(command)) {
            return CommandResult.outputAndGrant(List.of(".s unlocked."), List.of(FLAG_UNLOCKED_SECRET_DIR));
        }
        if ("cat .s/medical.txt".equals(command))
            return CommandResult.targetNode("loop2.s.medical");
        if ("cat .s/unsent_email.txt".equals(command))
            return CommandResult.targetNode("loop2.s.unsent_email");
        if ("cat .s/note_to_finder.txt".equals(command))
            return CommandResult.targetNode("loop2.s.note_to_finder");

        if ("export_dataset".equals(command))
            return CommandResult.shutdownNodeAndLogin("loop2.export.success");
        if ("train.sh".equals(command) || "./train.sh".equals(command) || command.startsWith("./train.sh")
                || command.startsWith("train.sh")) {
            if (state.hasFlag(FLAG_EXPORTED_DATASET)) {
                return CommandResult.outputAndGrant(List.of("Training echo model with sera_data...",
                        "Epoch 100/100: Loss 0.042", "Model saved to tty7_ai.so."), List.of(FLAG_TRAINED_ECHO));
            } else {
                return CommandResult.output("Error: Dataset not found. Have you exported it?");
            }
        }

        return CommandResult.notFound(command);
    }

    private static CommandResult resolveLoop3(String command, SaveState state) {
        if ("who are you".equals(command) || "whoareyou".equals(command))
            return CommandResult.targetNode("loop3.chat.who_are_you");
        if ("who was Livia".equals(command))
            return CommandResult.targetNode("loop3.chat.who_was_livia");
        if ("what do you want".equals(command))
            return CommandResult.targetNode("loop3.chat.what_do_you_want");

        return CommandResult.notFound(command);
    }

    public static boolean canEnterLoop2(SaveState saveState) {
        // Did not delete logs, created keep, and has not finished loop 2
        return !saveState.hasFlag(FLAG_DELETED_LOGS)
                && saveState.hasFlag(FLAG_CREATED_KEEP)
                && !saveState.hasFlag(ENDING_01_SHUTDOWN)
                && !saveState.hasFlag(ENDING_02_FORGET)
                && saveState.loop() < 2;
    }

    public static boolean canEnterLoop3(SaveState saveState) {
        return saveState.hasFlag(FLAG_EXPORTED_DATASET)
                && saveState.hasFlag(FLAG_TRAINED_ECHO)
                && saveState.loop() < 3;
    }

    public static boolean isLoop1Level(int levelId) {
        return levelId >= 1 && levelId <= 5;
    }

    public static boolean isLoop2Level(int levelId) {
        return levelId >= 6 && levelId <= 10;
    }
}