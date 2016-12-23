package com.github.imishx.activityrouter.router;

import android.app.Activity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;

/**
 * @author 李博
 * @date 2016年12月09日
 * @desc
 */
public class OpenTask {

    private final Class<? extends Activity> activity;
    private final InterceptorChain mInterceptorChain;
    private List<Interceptor> mInterceptors;
    private HashMap<String,Class> paramTypes;
    private Bundle urlParamsBundle;
    private String url;

    public OpenTask(Class<? extends Activity> activity, HashMap<String,Class> paramTypes, List<Interceptor> interceptors) {
        this.activity = activity;
        this.paramTypes = paramTypes;
        this.mInterceptors = interceptors;

        this.mInterceptorChain = new InterceptorChain();
        if (null != interceptors){
            for (Interceptor interceptor : interceptors){
                this.mInterceptorChain.addInterceptor(interceptor);
            }
        }
    }

    public Bundle getUrlParamsBundle() {
        return urlParamsBundle;
    }

    public void setUrlParamsBundle(Bundle urlParamsBundle) {
        this.urlParamsBundle = urlParamsBundle;
    }

    public InterceptorChain getInterceptorChain() {
        return mInterceptorChain;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public HashMap<String, Class> getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(HashMap<String, Class> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public List<Interceptor> getmInterceptors() {
        return mInterceptors;
    }

    public void setmInterceptors(List<Interceptor> mInterceptors) {
        this.mInterceptors = mInterceptors;
    }
}
