package com.zhx.lib_router_lrouter.lrouter;

import com.ljb.lib_router_annotation.entity.IRouterGroup;
import com.ljb.lib_router_annotation.entity.RouterInfo;

import java.util.HashMap;
import java.util.Map;

class LRouterCache {

    //保存所有分组信息
    static Map<String, Class<? extends IRouterGroup>> fileRoots = new HashMap<>();
    //保存路由信息
    static Map<String, RouterInfo> routers = new HashMap<>();

    static Map<String, Class<? extends IRouterGroup>> getFileRoots() {
        return fileRoots;
    }

    static Map<String, RouterInfo> getRouters() {
        return routers;
    }
}
