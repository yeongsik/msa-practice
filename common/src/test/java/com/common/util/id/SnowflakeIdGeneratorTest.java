package com.common.util.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SnowflakeIdGeneratorTest {

    @Test
    void testNextIdUnique() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            long id = generator.nextId();
            assertTrue(ids.add(id), "ID should be unique: " + id);
        }
    }

    @Test
    void testNextIdIncreasing() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        long lastId = 0L;
        for (int i = 0; i < 1000; i++) {
            long id = generator.nextId();
            assertTrue(id > lastId, "ID should be increasing");
            lastId = id;
        }
    }
}
