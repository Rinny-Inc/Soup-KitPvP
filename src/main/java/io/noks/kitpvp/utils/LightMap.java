package io.noks.kitpvp.utils;

import java.util.HashMap;
import java.util.Map;

public class LightMap<K, V> {
    private final Map<K, V> map = new HashMap<>();
    private boolean hasChanged = true;
    
    public V get(K key) {
        return map.get(key);
    }
    
    public void put(K key, V value) {
        map.put(key, value);
        hasChanged = true;
    }
    
    public void remove(K key) {
        map.remove(key);
        hasChanged = true;
    }
    
    public boolean hasChanged() {
        final boolean result = hasChanged;
        hasChanged = false;
        return result;
    }
}
