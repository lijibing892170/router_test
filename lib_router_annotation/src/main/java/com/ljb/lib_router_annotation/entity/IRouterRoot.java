package com.ljb.lib_router_annotation.entity;

import java.util.Map;

public interface IRouterRoot {
    void loadInfo(Map<String, Class<? extends IRouterGroup>> roots);
}
