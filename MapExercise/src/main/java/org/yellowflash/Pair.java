package org.yellowflash;

import java.util.Objects;

public class Pair<K1, K2> {
    K1 key1;
    K2 key2;

    public Pair(K1 key1, K2 key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key1=" + key1 +
                ", key2=" + key2 +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) object;
        return Objects.equals(key1, pair.key1) && Objects.equals(key2, pair.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }
}
