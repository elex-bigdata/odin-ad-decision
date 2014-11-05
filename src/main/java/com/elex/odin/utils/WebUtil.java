package com.elex.odin.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author: liqiang
 * Date: 14-10-23
 * Time: 下午3:16
 */
public class WebUtil {

    public static void writeJson(Object result, HttpServletResponse resp){
        try {
            PrintWriter pw = new PrintWriter(resp.getOutputStream());
            pw.write(Constant.gson.toJson(result));
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeStr(String message, HttpServletResponse resp){
        try {
            PrintWriter pw = new PrintWriter(resp.getOutputStream());
            pw.write(message);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
