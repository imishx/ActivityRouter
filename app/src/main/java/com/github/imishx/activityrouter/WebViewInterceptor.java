package com.github.imishx.activityrouter;

import android.content.Context;
import android.util.Log;

import com.github.imishx.activityrouter.router.Interceptor;
import com.github.imishx.activityrouter.router.InterceptorChain;
import com.github.imishx.activityrouter.router.Router;
import com.github.imishx.activityrouter.router.RouterMsg;

public class WebViewInterceptor implements Interceptor {

    boolean hasLogin;

    @Override
    public void doIntercept(Context context, String url, InterceptorChain interceptorChain) {
        if (url.contains("needlogin=true")) {
            if (hasLogin) {
                interceptorChain.doIntercept(context, url, interceptorChain);
            } else {
                interceptorChain.sendMsg(new RouterMsg(Router.MSG_FORWARD, "app://user/login"));
            }
        } else {
            interceptorChain.doIntercept(context, url, interceptorChain);
        }
    }
}
