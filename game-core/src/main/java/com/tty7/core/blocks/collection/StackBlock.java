package com.tty7.core.blocks.collection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.UnaryBlock;

@BlockMeta(name = "Stack", signature = "Collection<T> -> List<T>", description = "LIFO 反转输出", arity = 1)
public class StackBlock extends UnaryBlock<Object, List<?>> {
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object value = child.evaluate(ctx);
        if (!(value instanceof Collection<?> collection)) {
            return List.of(value);
        }
        ArrayDeque<Object> stack = new ArrayDeque<>();
        for (Object v : collection) {
            stack.push(v);
        }
        List<Object> out = new ArrayList<>(collection.size());
        while (!stack.isEmpty()) {
            out.add(stack.pop());
        }
        return out;
    }
}
