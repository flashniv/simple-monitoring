package ua.com.serverhelp.simplemonitoring.utils;

import java.time.Instant;

public class StringFormat {
    public enum Period {
        LAST_2DAYS(-2880,0,"Last 2 days"),
        LAST_DAY(-1440,0,"Last day"),
        LAST_12H(-720,0,"Last 12h"),
        LAST_6H(-360,0,"Last 6h"),
        LAST_2H(-120,0,"Last 2h"),
        LAST_1H(-60,0,"Last 1h"),
        PREV_DAY(-2880,-1440,"Prev day");

        private final int begin;
        private final int end;
        private final String name;

        Period(int begin, int end, String name) {
            this.begin = begin;
            this.end = end;
            this.name = name;
        }

        public int getBegin() {
            return begin;
        }

        public int getEnd() {
            return end;
        }

        public String getName() {
            return name;
        }
    }
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(Instant date) {
        if(date==null) return "";
        long time=date.getEpochSecond();
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = Instant.now().getEpochSecond()*1000;
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String getClassName(String input){
        String[] packages=input.split("\\.");
        if(packages.length==0){
            return "";
        }
        return packages[packages.length-1];
    }

    public static String formatObject(Object aObj,Integer precision){
        if(aObj.getClass().getSimpleName().equals("Double")){
            return formatDouble((Double) aObj, precision);
        }
        return aObj.toString();
    }

    public static String formatDouble(Double aDouble,Integer precision){
        String multipliers="KMGE";
        Double multiplier=1.0;
        if(precision==0){
            return String.format("%,.2f", aDouble);
        }
        for (int i = 0; i < precision; i++) {
            multiplier*=1000;
        }
        return String.format("%,.2f", aDouble/multiplier)+" "+multipliers.charAt(precision-1);
    }
    public static Double formatDoubleToDouble(Double aDouble, int precision){
        Double multiplier=1.0;
        if(precision==0){
            return aDouble;
        }
        for (int i = 0; i < precision; i++) {
            multiplier*=1000;
        }
        return aDouble/multiplier;
    }
}
