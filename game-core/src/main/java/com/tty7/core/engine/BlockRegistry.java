package com.tty7.core.engine;

import com.tty7.api.algoblock.BinaryBlock;
import com.tty7.api.algoblock.Block;
import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.UnaryBlock;
import com.tty7.core.blocks.basic.ArrayBlock;
import com.tty7.core.blocks.basic.IdentityBlock;
import com.tty7.core.blocks.collection.PrioQueueBlock;
import com.tty7.core.blocks.collection.QueueBlock;
import com.tty7.core.blocks.collection.StackBlock;
import com.tty7.core.blocks.fn.DoubleOpBlock;
import com.tty7.core.blocks.fn.EvenPredBlock;
import com.tty7.core.blocks.io.InputBlock;
import com.tty7.core.blocks.transform.FilterBlock;
import com.tty7.core.blocks.transform.FlatBlock;
import com.tty7.core.blocks.transform.MapBlock;
import com.tty7.core.blocks.transform.PopEachBlock;
import com.tty7.core.blocks.transform.ReverseBlock;
import com.tty7.core.blocks.transform.SortBlock;
import com.tty7.core.blocks.transform.ZipBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlockRegistry {
    private final Map<String, Class<? extends Block<?>>> map = new HashMap<>();

    public BlockRegistry() {
        registerBuiltins();
    }

    public void registerBuiltins() {
        register("Identity", IdentityBlock.class);
        register("Array", ArrayBlock.class);
        register("Stack", StackBlock.class);
        register("Queue", QueueBlock.class);
        register("PrioQueue", PrioQueueBlock.class);
        register("Sort", SortBlock.class);
        register("Reverse", ReverseBlock.class);
        register("Map", MapBlock.class);
        register("Filter", FilterBlock.class);
        register("Zip", ZipBlock.class);
        register("Flat", FlatBlock.class);
        register("PopEach", PopEachBlock.class);
        register("_INPUT_", InputBlock.class);
        register("DoubleOp", DoubleOpBlock.class);
        register("EvenPred", EvenPredBlock.class);
    }

    public void register(String name, Class<? extends Block<?>> clazz) {
        map.put(name, clazz);
    }

    public Block<?> instantiate(String name) {
        Class<? extends Block<?>> clazz = map.get(name);
        if (clazz == null) {
            throw new UnknownBlockException(name);
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate block: " + name, e);
        }
    }

    public int arity(String name, Block<?> block) {
        BlockMeta meta = block.getClass().getAnnotation(BlockMeta.class);
        if (meta != null) {
            return meta.arity();
        }
        if (block instanceof BinaryBlock<?, ?, ?>) {
            return 2;
        }
        if (block instanceof UnaryBlock<?, ?>) {
            return 1;
        }
        return 0;
    }

    public Collection<BlockMeta> allMeta() {
        return map.values().stream()
                .map(c -> c.getAnnotation(BlockMeta.class))
                .filter(Objects::nonNull)
                .toList();
    }
}
