package com.zchlon.xiaomipush;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExampleUtil {

    // user your appid the key.
    private static final String KEY_APP_ID = "XIAOMI_APPID";
    // user your appid the key.
    private static final String KEY_APP_KEY = "XIAOMI_APPKEY";

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    // 取得AppId
    public static String getAppId(Context context) {
        Bundle metaData = null;
        String appId = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != appInfo) {
                metaData = appInfo.metaData;
            }
            if (null != metaData) {
                appId = metaData.getString(KEY_APP_ID);
                appId = appId.split("=")[1];
            }
            Log.i("MipushLog", "appId =" + appId);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("Mipush", e.getMessage());
        }
        return appId;
    }

    // 取得AppKey
    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                appKey = appKey.split("=")[1];
            }
            Log.i("MipushLog", "appKey =" + appKey);
        } catch (PackageManager.NameNotFoundException e) {}
        return appKey;
    }

}
