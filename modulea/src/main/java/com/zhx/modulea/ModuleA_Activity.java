package com.zhx.modulea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.ljb.lib_router_annotation.Router;
import com.zhx.lib_router_lrouter.lrouter.LRouter;

@Router(path = "/modulea/maina")
public class ModuleA_Activity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulea);
        Intent intent = getIntent();
        int a = intent.getIntExtra("a", -1);
        String b = intent.getStringExtra("b");
        Log.i("aaaaaa", "onCreate: a=" + a + "    b=" + b);
        findViewById(R.id.tv_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LRouter.getInstance().build("/moduleb/mainb").navigate();
            }
        });
    }
}
