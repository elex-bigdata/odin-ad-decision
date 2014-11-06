package com.elex.odin.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Author: liqiang
 * Date: 14-11-6
 * Time: 下午1:33
 */
public class DateUtil {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static String yesterday(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return sdf.format(cal.getTime());
    }
}
