package com.github.imishx.activityrouter.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.imishx.activityrouter.annotation.ActivityMapping;

@ActivityMapping("module")
public class ModuleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView t = new TextView(this);
        t.setText("module");
        setContentView(t);
    }
}

