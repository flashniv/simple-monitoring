package ua.com.serverhelp.simplemonitoring.utils;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class StringFormatTest {
    @Test
    void getTimeAgo() {
        /*assertEquals("just now", StringFormat.getTimeAgo(new Date()));
        assertEquals("just now", StringFormat.getTimeAgo(new Date(new Date().getTime()-30000)));
        assertEquals("just now", StringFormat.getTimeAgo(new Date(new Date().getTime()-59000)));
        assertEquals("a minute ago", StringFormat.getTimeAgo(new Date(new Date().getTime()-90000)));
        assertEquals("3 minutes ago", StringFormat.getTimeAgo(new Date(new Date().getTime()-185000)));
        assertEquals("an hour ago", StringFormat.getTimeAgo(new Date(new Date().getTime()-3000000)));
        assertEquals("2 hours ago", StringFormat.getTimeAgo(new Date(new Date().getTime()-7500000)));
        assertEquals("yesterday", StringFormat.getTimeAgo(new Date(new Date().getTime()-97500000)));
        assertEquals("2 days ago", StringFormat.getTimeAgo(new Date(new Date().getTime()-197500000)));*/
    }

    @Test
    void getClassName() {
        assertEquals("CollectdCPUWaitTrigger", StringFormat.getClassName("ua.com.serverhelp.simplemonitoring.entities.trigger.CollectdCPUWaitTrigger"));
    }

    @Test
    void formatObject() {
        Double val=100.1;
        assertEquals("100", StringFormat.formatObject(100, 0));
        assertEquals("100,10", StringFormat.formatObject(val, 0));
    }

    @Test
    void formatDouble() {
        assertEquals("123,46 K", StringFormat.formatDouble(123456.0, 1));
        assertEquals("1,23 K", StringFormat.formatDouble(1234.5, 1));
        assertEquals("123 456,79 K", StringFormat.formatDouble(123456789.0, 1));
        assertEquals("123,46 M", StringFormat.formatDouble(123456789.589, 2));
    }

    @Test
    void formatDoubleToDouble() {
        assertEquals(123.456, StringFormat.formatDoubleToDouble(123456.0, 1));
        assertEquals(12.345, StringFormat.formatDoubleToDouble(12345.0, 1));
        assertEquals(0.1, StringFormat.formatDoubleToDouble(100.0, 1));
        assertEquals(123.4567895, StringFormat.formatDoubleToDouble(123456789.5, 2));
    }
}