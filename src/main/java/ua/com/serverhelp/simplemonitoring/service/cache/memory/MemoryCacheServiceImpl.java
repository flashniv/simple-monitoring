package ua.com.serverhelp.simplemonitoring.service.cache.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.service.cache.CacheService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MemoryCacheServiceImpl implements CacheService {
    private final Map<String, CacheNamespace<?>> map = new ConcurrentHashMap<>();

    @Override
    public <T> T getItem(String nameSpace, String key) {
        CacheNamespace<T> space = (CacheNamespace<T>) map.get(nameSpace);
        if (space != null) {
            var item = space.getItem(key);
            if(item!=null){
                log.debug("MemoryCacheServiceImpl::getItem HIT nameSpace="+nameSpace+" key="+key);
            }else{
                log.debug("MemoryCacheServiceImpl::getItem MISS nameSpace="+nameSpace+" key="+key);
            }
            return item;
        }
        log.debug("MemoryCacheServiceImpl::getItem MISS nameSpace="+nameSpace+" key="+key);
        return null;
    }

    @Override
    public <T> void setItem(String nameSpace, String key, T object) {
        CacheNamespace<T> space = (CacheNamespace<T>) map.get(nameSpace);
        if (space == null) {
            space = new CacheNamespace<>();
            map.put(nameSpace, space);
        }
        space.setItem(key, object);
        log.debug("MemoryCacheServiceImpl::setItem nameSpace="+nameSpace+" key="+key);
    }

    @Override
    public void deleteItem(String nameSpace, String key) {
        var space = map.get(nameSpace);
        if (space != null) {
            space.deleteItem(key);
        }
        log.debug("MemoryCacheServiceImpl::delItem nameSpace="+nameSpace+" key="+key);
    }

    @Override
    public void clearNameSpace(String nameSpace) {
        map.remove(nameSpace);
        log.debug("MemoryCacheServiceImpl::clearNameSpace nameSpace="+nameSpace);
    }

    @Override
    public void clear() {
        map.clear();
        log.debug("MemoryCacheServiceImpl::clear");
    }
}
