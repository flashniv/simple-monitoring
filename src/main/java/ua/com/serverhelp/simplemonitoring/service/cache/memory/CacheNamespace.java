package ua.com.serverhelp.simplemonitoring.service.cache.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheNamespace<T> {
    private final Map<String, T> map = new ConcurrentHashMap<>();

    public void setItem(String key, T obj) {
        map.put(key, obj);
    }

    public T getItem(String key) {
        return map.get(key);
    }

    public void deleteItem(String key) {
        map.remove(key);
    }
}
