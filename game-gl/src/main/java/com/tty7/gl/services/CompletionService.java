package com.tty7.gl.services;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.core.engine.BlockRegistry;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class CompletionService {
    private final BlockRegistry registry;

    public CompletionService(BlockRegistry registry) {
        this.registry = registry;
    }

    public List<String> complete(String prefix, Set<String> availableBlocks) {
        return registry.allMeta().stream()
                .filter(meta -> meta.name().startsWith(prefix))
                .filter(meta -> availableBlocks == null || availableBlocks.isEmpty()
                        || availableBlocks.contains(meta.name()))
                .sorted(Comparator.comparing(BlockMeta::name))
                .map(meta -> meta.name() + (meta.arity() > 0 ? "<>" : ""))
                .toList();
    }
}
