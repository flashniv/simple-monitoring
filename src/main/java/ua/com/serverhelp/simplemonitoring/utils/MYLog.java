package ua.com.serverhelp.simplemonitoring.utils;

import io.sentry.Sentry;
import org.springframework.boot.logging.LogLevel;

import java.util.Date;

public class MYLog {
    /**
     * Debug level
     */
    private static LogLevel debug=LogLevel.WARN;

    public static void printDebug(LogLevel logLevel,String message){
        if (logLevel.compareTo(debug)>=0){
            System.out.println(new Date()+" "+logLevel.name()+": "+message);
        }
    }

    public static void printEmerge(String message){
        printDebug(LogLevel.FATAL,message);

        System.exit(1);
    }
    public static void printEmerge(Exception e){
        printDebug(LogLevel.FATAL,e.getMessage());
        e.printStackTrace(System.err);
        System.exit(1);
    }
    public static void printEmerge(String message,Exception e){
        printDebug(LogLevel.FATAL,message);
        e.printStackTrace(System.err);
        System.exit(1);
    }
    public static void printError(String message,Exception e){
        Sentry.captureException(e);
        printDebug(LogLevel.ERROR,message+" "+e.getMessage());
    }
    public static void printWarn(String message){
        printDebug(LogLevel.WARN,message);
    }
    public static void printInfo(String message){
        printDebug(LogLevel.INFO,message);
    }
    public static void printDebug1(String message){
        printDebug(LogLevel.DEBUG,message);
    }
    public static void printDebug2(String message){
        printDebug(LogLevel.TRACE,message);
    }

    public static void printAnywhere(String message){
        System.out.println(new Date().getTime()+": "+message);
    }
    public static void setDebug(LogLevel debug) {
        MYLog.debug = debug;
    }
}
