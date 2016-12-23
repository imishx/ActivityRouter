package com.github.imishx.activityrouter.router;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 李博
 * @date 2016年12月09日
 * @desc
 */
public class Routers {

    private static String schema = "router";
    private static HashMap<String, OpenTask> mappings = new HashMap<>();

    public static void init(String schema) {
        Routers.schema = schema;
        try {
            Class<?> cls = Class.forName("com.github.imishx.activityrouter.router.RouterInit");
            Method method = cls.getMethod("init");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void map(String format, Class<? extends Activity> activity, HashMap<String, Class> paramTypes, List<Interceptor> interceptors) {
        mappings.put(format, new OpenTask(activity, paramTypes, interceptors));
    }

    public static Router getRouter(Context context, String url) {
        return new Router(context, getOpenTaskFromUrl(url));
    }

    private static OpenTask getOpenTaskFromUrl(String url) {
        Uri uri = Uri.parse(url);
        OpenTask openTask = null;
        if (TextUtils.equals(schema, uri.getScheme())) {
            openTask = mappings.get(uri.getHost().concat(uri.getPath()));
            if (null != openTask) {
                openTask.setUrlParamsBundle(parseUriParam(uri, openTask.getParamTypes()));
            }
        } else {
            openTask = mappings.get(uri.getScheme());
            if (null != openTask) {
                openTask.setUrl(url);
            }
        }
        return openTask;
    }

    public static Bundle parseUriParam(Uri uri, HashMap<String, Class> paramTypes) {
        Bundle bundle = new Bundle();
        Set<Map.Entry<String, Class>> entrySet = paramTypes.entrySet();

        for (Map.Entry<String, Class> entry : entrySet) {
            String paramName = entry.getKey();
            Class paramType = entry.getValue();
            String paramValue = uri.getQueryParameter(paramName);
            if (TextUtils.isEmpty(paramValue)) {
                continue;
            }
            if (paramType == byte.class) {
                bundle.putByte(paramName, Byte.parseByte(paramValue));
            } else if (paramType == short.class) {
                bundle.putShort(paramName, Short.parseShort(paramValue));
            } else if (paramType == int.class) {
                bundle.putInt(paramName, Integer.parseInt(paramValue));
            } else if (paramType == long.class) {
                bundle.putLong(paramName, Long.parseLong(paramValue));
            } else if (paramType == float.class) {
                bundle.putFloat(paramName, Float.parseFloat(paramValue));
            } else if (paramType == double.class) {
                bundle.putDouble(paramName, Double.parseDouble(paramValue));
            } else if (paramType == boolean.class) {
                bundle.putBoolean(paramName, Boolean.parseBoolean(paramValue));
            } else if (paramType == char.class) {
                bundle.putChar(paramName, paramValue.toCharArray()[0]);
            } else if (paramType == String.class) {
                bundle.putString(paramName, paramValue);
            }
        }
        return bundle;
    }


}
