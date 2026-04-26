package com.tty7.core.blocks.io;

import java.util.ArrayList;
import java.util.List;

import com.tty7.api.algoblock.BlockMeta;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.api.algoblock.NullaryBlock;

@BlockMeta(name = "_INPUT_", signature = "_INPUT_ -> List<?>", description = "输入序列", arity = 0)
public class InputBlock extends NullaryBlock<List<?>> {
    @Override
    public List<?> evaluate(EvalContext ctx) {
        ctx.consumeStep();
        return new ArrayList<>(ctx.getInput());
    }
}
