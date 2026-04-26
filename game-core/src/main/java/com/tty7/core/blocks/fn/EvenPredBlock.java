package com.tty7.core.blocks.fn;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.NullaryBlock;

@BlockMeta(name = "EvenPred", signature = "T -> Boolean", description = "是否偶数", arity = 0)
public class EvenPredBlock extends NullaryBlock<Boolean> {
    @Override
    public Boolean evaluate(EvalContext ctx) {
        ctx.consumeStep();
        Object it = ctx.getVar("it");
        if (it instanceof Number number) {
            return number.intValue() % 2 == 0;
        }
        return false;
    }
}
