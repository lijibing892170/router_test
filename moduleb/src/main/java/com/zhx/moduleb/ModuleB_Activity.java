package com.zhx.moduleb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ljb.lib_router_annotation.Router;
import com.zhx.lib_router_lrouter.lrouter.LRouter;

@Router(path = "/moduleb/mainb")
public class ModuleB_Activity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_b);
        findViewById(R.id.tv_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LRouter.getInstance().build("/main/main").addFlag(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).navigate();
            }
        });
    }
}
