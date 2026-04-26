package com.tty7.core.blocks.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.UnaryBlock;

@BlockMeta(name = "Flat", signature = "Collection<Collection<T>> -> List<T>", description = "展平一层", arity = 1)
public class FlatBlock extends UnaryBlock<Object, List<?>> {
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object value = child.evaluate(ctx);
        if (!(value instanceof Collection<?> collection)) {
            return List.of(value);
        }
        List<Object> out = new ArrayList<>();
        for (Object item : collection) {
            if (item instanceof Collection<?> nested) {
                out.addAll(nested);
            } else {
                out.add(item);
            }
        }
        return out;
    }
}
