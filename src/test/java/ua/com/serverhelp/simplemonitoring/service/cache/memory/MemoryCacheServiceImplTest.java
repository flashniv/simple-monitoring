package ua.com.serverhelp.simplemonitoring.service.cache.memory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MemoryCacheServiceImplTest {
    private final MemoryCacheServiceImpl memoryCacheService=new MemoryCacheServiceImpl();

    @Test
    void setItem() {
        memoryCacheService.setItem("doubles", "val1", 10.1);
        Assertions.assertEquals(10.1, memoryCacheService.getItem("doubles", "val1"));
        Assertions.assertNull(memoryCacheService.getItem("doubles", "val2"));
        Assertions.assertNull(memoryCacheService.getItem("doubles1", "val1"));

        memoryCacheService.setItem("strings", "val1", "value");
        memoryCacheService.setItem("strings", "val2", "value1");
        Assertions.assertEquals("value", memoryCacheService.getItem("strings", "val1"));
        Assertions.assertEquals("value1", memoryCacheService.getItem("strings", "val2"));

        memoryCacheService.deleteItem("strings", "val2");
        Assertions.assertNull(memoryCacheService.getItem("strings", "val2"));

        memoryCacheService.clearNameSpace("strings");
        Assertions.assertNull(memoryCacheService.getItem("strings", "val1"));
    }

}