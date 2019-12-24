package com.zhx.lib_router_lrouter.lrouter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.ljb.lib_router_annotation.entity.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import dalvik.system.DexFile;

public class ClassUtils {

    public static List<String> getSourcePaths(Context context) throws PackageManager.NameNotFoundException {
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        ArrayList<String> sources = new ArrayList<>();
        sources.add(applicationInfo.sourceDir);//获取APK的完整路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] splitSourceDirs = applicationInfo.splitSourceDirs;//获取多个APK的完整路径
            if (null != splitSourceDirs) {
                sources.addAll(Arrays.asList(splitSourceDirs));
            }
        }
        return sources;
    }

    public static Set<String> getSourceFilePath(Context context) throws PackageManager.NameNotFoundException, InterruptedException {
        final Set<String> classNames = new HashSet<>();
        final List<String> sourcePaths = getSourcePaths(context);
        //使用同步计数器判断均处理完成
        final CountDownLatch countDownLatch = new CountDownLatch(sourcePaths.size());
        ThreadPoolExecutor poolExecutor = ThreadPoolFactory.getThreadPool(sourcePaths.size());

        for (final String path : sourcePaths) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexFile = null;
                    try {
                        //加载 apk中的dex 并遍历 获得所有包名为 {packageName} 的类
                        dexFile = new DexFile(path);
                        Enumeration<String> entries = dexFile.entries();
                        while (entries.hasMoreElements()) {
                            String className = entries.nextElement();
                            if (!TextUtils.isEmpty(className) && className.startsWith(Constant.PACKAGE_OF_GENERATE_FILE)) {
                                classNames.add(className);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (null != dexFile) {
                            try {
                                dexFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //释放一个
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //等待执行完成
        countDownLatch.await();
        return classNames;
    }
}
