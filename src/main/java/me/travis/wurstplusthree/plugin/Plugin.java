package me.travis.wurstplusthree.plugin;

import java.lang.reflect.Method;

public class Plugin {
    public String name;
    public String id;
    public Class pluginClass;
    public Method pluginMethod;

    public Plugin(String name, String id, Class pluginClass, Method pluginMethod){
        this.name = name;
        this.id = id;
        this.pluginClass = pluginClass;
        this.pluginMethod = pluginMethod;
    }
}
