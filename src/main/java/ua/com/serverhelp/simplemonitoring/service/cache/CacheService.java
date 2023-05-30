package ua.com.serverhelp.simplemonitoring.service.cache;

public interface CacheService {
    <T> T getItem(String nameSpace, String key);

    <T> void setItem(String nameSpace, String key, T object);

    void deleteItem(String nameSpace, String key);

    void clearNameSpace(String nameSpace);

    void clear();
}
