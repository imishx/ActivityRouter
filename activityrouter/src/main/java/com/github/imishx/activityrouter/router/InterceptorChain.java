package com.github.imishx.activityrouter.router;

import android.content.Context;

import java.util.ArrayList;

/**
 * @author 李博
 * @date 2016年12月09日
 * @desc
 */
public class InterceptorChain implements Interceptor {

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private Listener mListener;
    private int mIndex = 0;

    public InterceptorChain addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    @Override
    public void doIntercept(Context context, String url, InterceptorChain interceptorChain) {
        if (mIndex == mInterceptors.size()) {
            sendMsg(new RouterMsg(Router.MSG_GO, null));
            return;
        }
        mInterceptors.get(mIndex++).doIntercept(context, url, this);
    }

    public void setChainStateChangeListener(Listener listener) {
        this.mListener = listener;
    }

    public void sendMsg(RouterMsg msg) {
        mListener.onChainStateChange(msg);
    }

    public void reset() {
        mIndex = 0;
    }


    public interface Listener{
        void onChainStateChange(RouterMsg msg);
    }
}
