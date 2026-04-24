package com.tty7.core.blocks.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tty7.api.BlockMeta;
import com.tty7.api.EvalContext;
import com.tty7.api.UnaryBlock;

@BlockMeta(name = "Array", signature = "Collection<T> -> List<T>", description = "包装为数组列表", arity = 1)
public class ArrayBlock extends UnaryBlock<Object, List<?>> {
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
