package com.zhx.lib_router_lrouter.lrouter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.ljb.lib_router_annotation.entity.Constant;
import com.ljb.lib_router_annotation.entity.IRouterGroup;
import com.ljb.lib_router_annotation.entity.IRouterRoot;
import com.ljb.lib_router_annotation.entity.RouterInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class LRouter {
    private static Application mContext;

    private static LRouter mInstance;

    public static LRouter getInstance() {
        if (null == mInstance) {
            synchronized (LRouter.class) {
                if (null == mInstance) {
                    mInstance = new LRouter();
                }
            }
        }
        return mInstance;
    }

    public static void init(Application context) {
        mContext = context;
        try {
            loadInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadInfo() throws PackageManager.NameNotFoundException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Set<String> sourceFilePath = ClassUtils.getSourceFilePath(mContext);
        for (String filePath : sourceFilePath) {
            if (filePath.startsWith(Constant.PACKAGE_OF_GENERATE_FILE + "." + Constant.NAME_OF_ROOT)) {
                ((IRouterRoot) Class.forName(filePath).getConstructor().newInstance()).loadInfo(LRouterCache.getFileRoots());
            }
        }

        Log.i("aaaaa", "root_length: " + LRouterCache.getFileRoots().size());
        for (Map.Entry<String, Class<? extends IRouterGroup>> entry : LRouterCache.getFileRoots().entrySet()) {
            Log.i("aaaaa", "key: " + entry.getKey());
            Log.i("aaaaa", "value: " + entry.getValue());
        }
    }

    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException("不能提取组别");
        }
        return path.substring(1, path.indexOf("/", 1));
    }

    public RouterCard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址不能为空");
        } else {
            return build(path, extractGroup(path));
        }
    }

    public RouterCard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址不能为空");
        } else {
            return new RouterCard(path, group);
        }
    }

    protected void _navigate(Context context, RouterCard card) {
        prepare(card);

        Context currentContext = null == context ? mContext : context;
        Intent intent = new Intent(currentContext, card.getTarget());
        intent.putExtras(card.getExtras());

        if (-1 != card.getFlags()) {
            intent.setFlags(card.getFlags());
        } else if (!(currentContext instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ActivityCompat.startActivity(currentContext, intent, null);
    }

    private void prepare(RouterCard card) {
        RouterInfo info = LRouterCache.getRouters().get(card.getPath());
        if (null == info) {
            Class<? extends IRouterGroup> aClass = LRouterCache.getFileRoots().get(card.getGroup());
            try {
                IRouterGroup iRouterGroup = aClass.getConstructor().newInstance();
                if (null == iRouterGroup) {
                    throw new RuntimeException("没有找到对应的分组");
                }
                iRouterGroup.loadInfo(LRouterCache.getRouters());
                LRouterCache.getFileRoots().remove(card.getGroup());

                prepare(card);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        } else {
            card.setTarget(info.object);
        }
    }

}
