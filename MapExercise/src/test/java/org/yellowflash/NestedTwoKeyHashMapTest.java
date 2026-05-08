package org.yellowflash;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NestedTwoKeyHashMapTest  {
    NestedTwoKeyHashMap<String,String,Integer> testMap = new NestedTwoKeyHashMap<>();

    @Test
    public void testPutAndGet() {
        testMap.put("Jan","Math",5);
        assertThat(testMap.get("Jan","Math")).isEqualTo(5);
    }
    @Test
    public void testRemove() {
        testMap.put("Jan","Math",5);
        testMap.remove("Jan","Math");
        assertThat(testMap.get("Jan","Math")).isNull();
    }
    @Test
    public void testContainsKeys() {
        testMap.put("Jan","Math",5);
        assertThat(testMap.containsKeys("Jan","Math")).isTrue();
        testMap.remove("Jan","Math");
        assertThat(testMap.containsKeys("Jan","Math")).isFalse();
    }
    @Test
    public void testContainsValue() {
        testMap.put("Jan","Math",5);
        assertThat(testMap.containsValue(5)).isTrue();

    }

    public void testSize() {
    }

    public void testIsEmpty() {
    }

    public void testEntrySet() {
    }

    public void testValues() {
    }

    public void testPutAll() {
    }

    public void testClear() {
    }

    public void testRow() {
    }

    public void testColumn() {
    }

    public void testIterator() {
    }
}