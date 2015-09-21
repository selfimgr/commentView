package com.chaowen.commentlibrary.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 *
 * LogUtil
 * chaowen
 * 2015-6-27 下午3:41:50
 * @version 1.0.0
 *
 */
public class LogUtil {

    public static boolean isLog = true;
//    public static boolean isLog = false;

    public static void v(String tag, String msg, Throwable tr) {
        if (isLog){
            Log.v(tag, msg, tr);
        }
    }

    public static void v(String tag, String msg) {
        if (isLog){
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (isLog){
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (isLog){
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isLog){
            Log.e(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (isLog){
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isLog){
            Log.d(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (isLog){
            Log.d(tag, msg);
        }
    }

    public static void systemOut(String tag, Object msg) {
        if (isLog){
            System.out.println(tag + ": " + msg);
        }
    }

    //主要用于打印请求URL
    public static void debugUrl(String tag,String url,HashMap params){
        if (isLog){
            StringBuffer sb = new StringBuffer();
            sb.append(url);
            Iterator iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                sb.append("&"+key+"=");
                sb.append(val);
            }
            Log.d(tag, sb.toString());
        }
    }
}
