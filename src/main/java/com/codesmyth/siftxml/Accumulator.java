package com.codesmyth.siftxml;

import java.util.HashMap;
import java.util.Map;

class Accumulator {
    Map<String, String> map = new HashMap<String, String>();

    public String read(String key) {
        String value = "";
        if (map.containsKey(key)) {
            value = map.get(key);
            map.remove(key);
        }
        return value;
    }

    public void discard(String key) {
        map.remove(key);
    }

    public void add(String key, String value) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + value);
        } else {
            map.put(key, value);
        }
    }
}
