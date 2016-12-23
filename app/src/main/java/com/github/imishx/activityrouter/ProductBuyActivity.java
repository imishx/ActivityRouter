package com.github.imishx.activityrouter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.imishx.activityrouter.annotation.ActivityInterceptor;
import com.github.imishx.activityrouter.annotation.ActivityMapping;

@ActivityMapping(value = {"product/buy"})
@ActivityInterceptor({LoginInterceptor.class})
public class ProductBuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView t = new TextView(this);
        t.setTextColor(Color.BLACK);
        t.setText("product buy");
        setContentView(t);
    }
}
