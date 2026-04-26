package com.tty7.core.blocks.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.UnaryBlock;

@BlockMeta(name = "PopEach", signature = "Collection<T> -> List<T>", description = "逐一弹出为列表", arity = 1)
public class PopEachBlock extends UnaryBlock<Object, List<?>> {
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object value = child.evaluate(ctx);
        if (value instanceof Collection<?> collection) {
            return new ArrayList<>(collection);
        }
        return List.of(value);
    }
}
