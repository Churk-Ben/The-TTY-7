package com.tty7.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tty7.api.algoblock.Block;
import com.tty7.api.algoblock.EvalContext;
import com.tty7.core.blocks.basic.ArrayBlock;
import com.tty7.core.blocks.collection.PrioQueueBlock;
import com.tty7.core.blocks.collection.StackBlock;
import com.tty7.core.blocks.fn.DoubleOpBlock;
import com.tty7.core.blocks.fn.EvenPredBlock;
import com.tty7.core.blocks.io.InputBlock;
import com.tty7.core.blocks.transform.FilterBlock;
import com.tty7.core.blocks.transform.FlatBlock;
import com.tty7.core.blocks.transform.MapBlock;
import com.tty7.core.blocks.transform.ReverseBlock;
import com.tty7.core.blocks.transform.ZipBlock;

import java.util.List;
import org.junit.jupiter.api.Test;

class BlocksTest {
    @SuppressWarnings("unchecked")
    private static Block<Object> block(Block<?> value) {
        return (Block<Object>) value;
    }

    @Test
    void stackShouldReverse() {
        InputBlock input = new InputBlock();
        StackBlock stack = new StackBlock();
        stack.setChild(block(input));
        assertEquals(List.of(3, 2, 1), stack.evaluate(new EvalContext(List.of(1, 2, 3), 100)));
    }

    @Test
    void prioQueueShouldSort() {
        InputBlock input = new InputBlock();
        PrioQueueBlock block = new PrioQueueBlock();
        block.setChild(block(input));
        assertEquals(List.of(1, 2, 3), block.evaluate(new EvalContext(List.of(2, 1, 3), 100)));
    }

    @Test
    void mapAndFilterShouldWork() {
        InputBlock input = new InputBlock();
        MapBlock map = new MapBlock();
        map.setLeft(block(input));
        map.setRight(block(new DoubleOpBlock()));

        FilterBlock filter = new FilterBlock();
        filter.setLeft(block(map));
        filter.setRight(block(new EvenPredBlock()));

        assertEquals(List.of(2, 4, 6), filter.evaluate(new EvalContext(List.of(1, 2, 3), 1000)));
    }

    @Test
    void zipAndFlatShouldWork() {
        InputBlock input = new InputBlock();
        ZipBlock zip = new ZipBlock();
        zip.setLeft(block(input));
        zip.setRight(block(new com.tty7.core.blocks.fn.ConstIntBlock(2)));

        FlatBlock flat = new FlatBlock();
        flat.setChild(block(zip));

        assertEquals(List.of(1, 2, 3, 4), flat.evaluate(new EvalContext(List.of(1, 2, 3, 4), 1000)));
    }

    @Test
    void arrayAndReverseShouldWork() {
        InputBlock input = new InputBlock();
        ArrayBlock array = new ArrayBlock();
        array.setChild(block(input));
        ReverseBlock reverse = new ReverseBlock();
        reverse.setChild(block(array));
        assertEquals(List.of(4, 3, 2, 1), reverse.evaluate(new EvalContext(List.of(1, 2, 3, 4), 100)));
    }
}
