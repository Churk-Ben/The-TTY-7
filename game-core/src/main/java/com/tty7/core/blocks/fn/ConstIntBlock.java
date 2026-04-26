package com.tty7.core.blocks.fn;

import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.NullaryBlock;

public class ConstIntBlock extends NullaryBlock<Integer> {
    private final int value;

    public ConstIntBlock(int value) {
        this.value = value;
    }

    @Override
    public Integer evaluate(EvalContext ctx) {
        ctx.consumeStep();
        return value;
    }
}
