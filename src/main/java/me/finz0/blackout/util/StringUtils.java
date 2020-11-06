package me.finz0.blackout.util;

public class StringUtils {
    public static boolean isNumber(String string){
        try{
            Integer.parseInt(string);
            return true;
        } catch(Exception e){
            return false;
        }
    }
}
