package com.tty7.core.blocks.basic;

import com.tty7.api.BlockMeta;
import com.tty7.api.EvalContext;
import com.tty7.api.UnaryBlock;

@BlockMeta(name = "Identity", signature = "T -> T", description = "原样返回", arity = 1)
public class IdentityBlock extends UnaryBlock<Object, Object> {
    @Override
    public Object evaluate(EvalContext ctx) {
        ctx.consumeStep();
        return child.evaluate(ctx);
    }
}
