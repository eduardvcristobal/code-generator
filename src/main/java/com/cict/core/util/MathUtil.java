package com.cict.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

//    public static Double round( Double val, Integer places ) {
//        if(val == null || places == null)
//            return  null;
//        Double p = Math.pow(10, places);
//        return ( val * p ) / p;
//    }

    public static double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        if (value == null) return 0;

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean isNumber(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String ordinal(int i) {
        return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"}[i % 10];
    }
}