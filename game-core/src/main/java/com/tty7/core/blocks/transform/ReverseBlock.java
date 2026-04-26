package com.tty7.core.blocks.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.UnaryBlock;

@BlockMeta(name = "Reverse", signature = "Collection<T> -> List<T>", description = "反转序列", arity = 1)
public class ReverseBlock extends UnaryBlock<Object, List<?>> {
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object value = child.evaluate(ctx);
        if (!(value instanceof Collection<?> collection)) {
            return List.of(value);
        }
        List<Object> list = new ArrayList<>(collection);
        Collections.reverse(list);
        return list;
    }
}
