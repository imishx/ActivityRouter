package com.github.imishx.activityrouter.router;

import android.content.Context;
import android.os.Bundle;

/**
 * @author 李博
 * @date 2016年12月02日
 * @desc
 */
public interface Interceptor {
    void doIntercept(Context context, String url, InterceptorChain interceptorChain);
}
