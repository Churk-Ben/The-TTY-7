package com.tty7.core.blocks.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tty7.api.BinaryBlock;
import com.tty7.api.Block;
import com.tty7.api.BlockMeta;
import com.tty7.api.EvalContext;

@BlockMeta(name = "Map", signature = "Collection<T> x Func -> List<R>", description = "逐元素映射", arity = 2)
public class MapBlock extends BinaryBlock<Object, Object, List<?>> {
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object rawLeft = left.evaluate(ctx);
        if (!(rawLeft instanceof Collection<?> collection)) {
            return List.of(rawLeft);
        }
        Block<?> mapper = right;
        List<Object> out = new ArrayList<>(collection.size());
        for (Object item : collection) {
            ctx.putVar("it", item);
            out.add(mapper.evaluate(ctx));
        }
        return out;
    }
}
