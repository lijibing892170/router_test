package com.zhx.lib_router_lrouter.lrouter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RouterCard {
    private String path;
    private String group;
    private Class<?> target;
    private Bundle extras;
    private int flags = -1;

    public RouterCard(String path, String group) {
        this.path = path;
        this.group = group;
        extras = new Bundle();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public void navigate() {
        LRouter.getInstance()._navigate(null, this);
    }

    public Bundle getExtras() {
        return extras;
    }

    public int getFlags() {
        return flags;
    }

    public RouterCard withString(String key, String value) {
        extras.putString(key, value);
        return this;
    }

    public RouterCard withInt(String key, int value) {
        extras.putInt(key, value);
        return this;
    }

    public RouterCard withBoolean(String key, boolean value) {
        extras.putBoolean(key, value);
        return this;
    }

    public RouterCard withLong(String key, long value) {
        extras.putLong(key, value);
        return this;
    }

    public RouterCard withDouble(String key, double value) {
        extras.putDouble(key, value);
        return this;
    }

    public RouterCard withFloat(String key, float value) {
        extras.putFloat(key, value);
        return this;
    }

    public RouterCard addFlag(@FlagInt int flags) {
        this.flags = flags;
        return this;
    }

    //@IntDef替代枚举,声明常量
    @IntDef(flag = true,value = {
            Intent.FLAG_ACTIVITY_SINGLE_TOP,
            Intent.FLAG_ACTIVITY_NEW_TASK,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            Intent.FLAG_DEBUG_LOG_RESOLUTION,
            Intent.FLAG_FROM_BACKGROUND,
            Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT,
            Intent.FLAG_ACTIVITY_CLEAR_TASK,
            Intent.FLAG_ACTIVITY_CLEAR_TOP,
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
            Intent.FLAG_ACTIVITY_FORWARD_RESULT,
            Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY,
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
            Intent.FLAG_ACTIVITY_NO_ANIMATION,
            Intent.FLAG_ACTIVITY_NO_USER_ACTION,
            Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP,
            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT,
            Intent.FLAG_ACTIVITY_TASK_ON_HOME,
            Intent.FLAG_RECEIVER_REGISTERED_ONLY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlagInt {
    }

    @Override
    public String toString() {
        return "RouterCard{" +
                "path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", target=" + target +
                '}';
    }
}
