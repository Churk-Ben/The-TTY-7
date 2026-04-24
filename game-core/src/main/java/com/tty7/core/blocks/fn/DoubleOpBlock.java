package com.tty7.core.blocks.fn;

import com.tty7.api.BlockMeta;
import com.tty7.api.EvalContext;
import com.tty7.api.NullaryBlock;

@BlockMeta(name = "DoubleOp", signature = "T -> T", description = "整数乘2", arity = 0)
public class DoubleOpBlock extends NullaryBlock<Object> {
    @Override
    public Object evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object it = ctx.getVar("it");
        if (it instanceof Number number) {
            return number.intValue() * 2;
        }
        return it;
    }
}
