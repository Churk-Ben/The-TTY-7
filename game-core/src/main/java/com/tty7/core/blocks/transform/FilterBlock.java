package com.tty7.core.blocks.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tty7.api.algoblock.BinaryBlock;
import com.tty7.api.algoblock.Block;
import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;

@BlockMeta(name = "Filter", signature = "Collection<T> x Pred -> List<T>", description = "逐元素过滤", arity = 2)
public class FilterBlock extends BinaryBlock<Object, Object, List<?>> {
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object rawLeft = left.evaluate(ctx);
        if (!(rawLeft instanceof Collection<?> collection)) {
            return List.of(rawLeft);
        }
        Block<?> predicate = right;
        List<Object> out = new ArrayList<>();
        for (Object item : collection) {
            ctx.putVar("it", item);
            Object keep = predicate.evaluate(ctx);
            if (keep instanceof Boolean b && b) {
                out.add(item);
            }
        }
        return out;
    }
}
