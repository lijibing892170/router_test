package com.ljb.router_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ljb.lib_router_annotation.Router;
import com.zhx.lib_router_lrouter.lrouter.LRouter;

@Router(path = "/main/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_a = findViewById(R.id.tv_a);
        tv_a.setText(MainActivity.class.getSimpleName());
        tv_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LRouter.getInstance().build("/modulea/maina").withInt("a", 123).withString("b", "abc").navigate();
//                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
    }
}
