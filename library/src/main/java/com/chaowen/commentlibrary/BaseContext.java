package com.chaowen.commentlibrary;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by chaowen on 2015/9/21.
 */
public class BaseContext extends Application{
    private static String PREF_NAME = "config.pref";
    private static final String KEY_SOFTKEYBOARD_HEIGHT = "KEY_SOFTKEYBOARD_HEIGHT";
    public static BaseContext mContext;
    private static boolean sIsAtLeastGB;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sIsAtLeastGB = true;
        }
    }
    public static BaseContext getInstance() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static BaseContext getInstance(Context context) {
        return context != null ? (BaseContext) context.getApplicationContext() : mContext;
    }

    public static int getSoftKeyboardHeight() {
        return getPreferences().getInt(KEY_SOFTKEYBOARD_HEIGHT, 0);
    }

    public static void setSoftKeyboardHeight(int height) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(KEY_SOFTKEYBOARD_HEIGHT, height);
        apply(editor);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences getPreferences() {
        SharedPreferences pre = mContext.getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void apply(SharedPreferences.Editor editor) {
        if (sIsAtLeastGB) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
