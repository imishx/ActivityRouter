package com.github.imishx.activityrouter;

import android.app.Application;

import com.github.imishx.activityrouter.router.Routers;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Routers.init("app");
    }
}
