package com.ljb.lib_router_annotation.entity;

import javax.lang.model.element.Element;

public class RouterInfo {
    public Element element;
    public String path;
    public String group;
    public Class<?> object;

    public RouterInfo(Element className, String message) {
        this.element = className;
        this.path = message;
    }

    public RouterInfo(String group, String path, Class<?> object) {
        this.group = group;
        this.path = path;
        this.object = object;
    }

    public static RouterInfo build(String group, String path, Class<?> object) {
        return new RouterInfo(group, path, object);
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
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

    public Class<?> getObject() {
        return object;
    }

    public void setObject(Class<?> object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "RouterInfo{" +
                "element=" + element +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", object=" + object +
                '}';
    }
}
