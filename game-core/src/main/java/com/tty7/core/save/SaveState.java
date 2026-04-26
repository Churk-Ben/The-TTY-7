package com.tty7.core.save;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record SaveState(
        int loop,
        Set<String> flags,
        Set<String> seenNodes,
        Map<Integer, String> solvedExpressions,
        String endingId) {

    public SaveState {
        loop = Math.max(1, loop);
        flags = Set.copyOf(flags == null ? Set.<String>of() : new LinkedHashSet<>(flags));
        seenNodes = Set.copyOf(seenNodes == null ? Set.<String>of() : new LinkedHashSet<>(seenNodes));
        solvedExpressions = Map.copyOf(
                solvedExpressions == null ? Map.<Integer, String>of() : new LinkedHashMap<>(solvedExpressions));
        endingId = endingId == null || endingId.isBlank() ? null : endingId;
    }

    public static SaveState empty() {
        return new SaveState(1, Set.of(), Set.of(), Map.of(), null);
    }

    public SaveState withFlag(String flag) {
        LinkedHashSet<String> nextFlags = new LinkedHashSet<>(flags);
        nextFlags.add(flag);
        return new SaveState(loop, nextFlags, seenNodes, solvedExpressions, endingId);
    }

    public SaveState withFlags(List<String> nextFlags) {
        LinkedHashSet<String> mergedFlags = new LinkedHashSet<>(flags);
        mergedFlags.addAll(nextFlags);
        return new SaveState(loop, mergedFlags, seenNodes, solvedExpressions, endingId);
    }

    public SaveState withSeenNode(String nodeId) {
        LinkedHashSet<String> nextSeenNodes = new LinkedHashSet<>(seenNodes);
        nextSeenNodes.add(nodeId);
        return new SaveState(loop, flags, nextSeenNodes, solvedExpressions, endingId);
    }

    public SaveState withSeenNodes(List<String> nextNodeIds) {
        LinkedHashSet<String> nextSeenNodes = new LinkedHashSet<>(seenNodes);
        nextSeenNodes.addAll(nextNodeIds);
        return new SaveState(loop, flags, nextSeenNodes, solvedExpressions, endingId);
    }

    public SaveState withSolvedExpression(int levelId, String expression) {
        LinkedHashMap<Integer, String> nextSolvedExpressions = new LinkedHashMap<>(solvedExpressions);
        nextSolvedExpressions.put(levelId, expression);
        return new SaveState(loop, flags, seenNodes, nextSolvedExpressions, endingId);
    }

    public SaveState withLoop(int nextLoop) {
        return new SaveState(nextLoop, flags, seenNodes, solvedExpressions, endingId);
    }

    public SaveState withEnding(String nextEndingId) {
        return new SaveState(loop, flags, seenNodes, solvedExpressions, nextEndingId);
    }

    public SaveState removeFlag(String flag) {
        LinkedHashSet<String> nextFlags = new LinkedHashSet<>(flags);
        nextFlags.remove(flag);
        return new SaveState(loop, nextFlags, seenNodes, solvedExpressions, endingId);
    }

    public SaveState removeFlags(List<String> flagsToRemove) {
        LinkedHashSet<String> nextFlags = new LinkedHashSet<>(flags);
        nextFlags.removeAll(flagsToRemove);
        return new SaveState(loop, nextFlags, seenNodes, solvedExpressions, endingId);
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public boolean hasSeenNode(String nodeId) {
        return seenNodes.contains(nodeId);
    }

    public boolean hasSolvedLevel(int levelId) {
        return solvedExpressions.containsKey(levelId);
    }
}
