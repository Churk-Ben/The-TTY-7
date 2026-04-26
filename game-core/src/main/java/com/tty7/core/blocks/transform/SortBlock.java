package com.tty7.core.blocks.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.UnaryBlock;

@BlockMeta(name = "Sort", signature = "Collection<T> -> List<T>", description = "自然序排序", arity = 1)
public class SortBlock extends UnaryBlock<Object, List<?>> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object value = child.evaluate(ctx);
        if (!(value instanceof Collection<?> collection)) {
            return List.of(value);
        }
        List list = new ArrayList<>(collection);
        if (!list.isEmpty() && !(list.get(0) instanceof Comparable)) {
            throw new RuntimeException(
                    "Sort requires Comparable elements, but got " + list.get(0).getClass().getSimpleName());
        }
        try {
            list.sort(Comparator.naturalOrder());
        } catch (ClassCastException e) {
            throw new RuntimeException("Sort requires mutually Comparable elements");
        }
        return list;
    }
}
