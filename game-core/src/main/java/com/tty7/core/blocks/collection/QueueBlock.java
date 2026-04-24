package com.tty7.core.blocks.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tty7.api.BlockMeta;
import com.tty7.api.EvalContext;
import com.tty7.api.UnaryBlock;

@BlockMeta(name = "Queue", signature = "Collection<T> -> List<T>", description = "FIFO 保序输出", arity = 1)
public class QueueBlock extends UnaryBlock<Object, List<?>> {
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
