package com.github.imishx.activityrouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.imishx.activityrouter.annotation.ActivityMapping;
import com.github.imishx.activityrouter.router.Router;

@ActivityMapping({"user/login"})
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Router.forward(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Router.back(this);
    }
}
