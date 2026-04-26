package com.tty7.core.story;

import java.util.List;

public record CommandResult(
        List<String> outputLines,
        String targetNodeId,
        List<String> grantFlags,
        boolean openGame,
        boolean isShutdown,
        String openEndingId,
        boolean returnToLogin
) {
    public static CommandResult output(String... lines) {
        return new CommandResult(List.of(lines), null, List.of(), false, false, null, false);
    }

    public static CommandResult outputAndGrant(List<String> lines, List<String> flags) {
        return new CommandResult(lines, null, flags, false, false, null, false);
    }

    public static CommandResult targetNode(String targetNodeId) {
        return new CommandResult(List.of(), targetNodeId, List.of(), false, false, null, false);
    }

    public static CommandResult openGame(String targetNodeId) {
        return new CommandResult(List.of(), targetNodeId, List.of(), true, false, null, false);
    }

    public static CommandResult notFound(String command) {
        return new CommandResult(List.of(command + ": command not found"), null, List.of(), false, false, null, false);
    }

    public static CommandResult shutdown(String targetNodeId) {
        return new CommandResult(List.of(), targetNodeId, List.of(), false, true, null, false);
    }

    public static CommandResult shutdownNodeAndLogin(String targetNodeId) {
        return new CommandResult(List.of(), targetNodeId, List.of(), false, true, null, true);
    }

    public static CommandResult shutdownNodeAndEnding(String targetNodeId, String endingId) {
        return new CommandResult(List.of(), targetNodeId, List.of(), false, true, endingId, false);
    }

    public static CommandResult shutdownAndLogin() {
        return new CommandResult(List.of(), null, List.of(), false, true, null, true);
    }

    public static CommandResult shutdownAndEnding(String endingId) {
        return new CommandResult(List.of(), null, List.of(), false, true, endingId, false);
    }
}