package com.github.imishx.activityrouter.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Stack;

/**
 * @author 李博
 * @date 2016年12月09日
 * @desc
 */
public class Router implements InterceptorChain.Listener {

    public static final int MSG_GO = 0;
    public static final int MSG_FORWARD = 1;
    public static final int MSG_REDIRECT = 2;
    public static final String URL = "original_url";
    private static Stack<Intent> openTaskStack = new Stack<>();

    private Context context;
    private OpenTask openTask;
    private InterceptorChain chain;

    private Intent intent;
    private int requestCode;


    public Router(Context context, OpenTask openTask) {
        this.context = context;
        this.openTask = openTask;
        if (null != openTask) {
            this.chain = openTask.getInterceptorChain();
            this.chain.setChainStateChangeListener(this);
            this.intent = new Intent(context, openTask.getActivity());
            Bundle b = openTask.getUrlParamsBundle();
            String originalUrl = openTask.getUrl();
            if (null != b) {
                this.intent.putExtras(b);
            }
            if (!TextUtils.isEmpty(originalUrl)) {
                this.intent.putExtra(URL, openTask.getUrl());
            }
        }
    }

    public void open() {
        openForResult(-1);
    }

    public void openForResult(final int requestCode) {
        if (openTask == null) {
            return;
        }
        this.requestCode = requestCode;
        chain.doIntercept(context, openTask.getUrl(), chain);
    }


    @Override
    public void onChainStateChange(RouterMsg msg) {
        chain.reset();
        switch (msg.cmd) {
            case MSG_GO:
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (requestCode >= 0) {
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    } else {
                        throw new RuntimeException("can not startActivityForResult context " + context);
                    }
                } else {
                    context.startActivity(intent);
                }
                break;
            case MSG_FORWARD:
                openTaskStack.push(intent);
                Routers.getRouter(context, msg.extra).open();
                break;
            case MSG_REDIRECT:
                Routers.getRouter(context, msg.extra).open();
                break;
            default:
                break;
        }
    }


    public static void forward(Context context) {
        if (openTaskStack.isEmpty()) {
            return;
        }
        context.startActivity(openTaskStack.pop());
    }

    public static void back(Context context) {
        if (!openTaskStack.isEmpty()) {
            openTaskStack.pop();
        }
    }


    public Router withParam(String name, short value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, short[] value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, int value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, int[] value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, long value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, long[] value) {
        intent.putExtra(name, value);
        return this;
    }


    public Router withParam(String name, float value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, float[] value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, double value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, double[] value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, boolean value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, boolean[] value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, char value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, char[] value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, String value) {
        intent.putExtra(name, value);
        return this;
    }


    public Router withParam(String name, String[] value) {
        intent.putExtra(name, value);
        return this;
    }


    public Router withParam(String name, Parcelable value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withParam(String name, Parcelable[] value) {
        intent.putExtra(name, value);
        return this;
    }

    public Router withFlags(int flags) {
        intent.addFlags(flags);
        return this;
    }

    public Router withCategory(String category) {
        intent.addCategory(category);
        return this;
    }

}
