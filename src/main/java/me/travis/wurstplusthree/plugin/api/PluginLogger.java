package me.travis.wurstplusthree.plugin.api;

import me.travis.wurstplusthree.WurstplusThree;

/**
 * @author Madmegsox1
 * @since 21/06/2021
 */

public class PluginLogger {
    public static void print(String msg){
        WurstplusThree.LOGGER.info("Plugin -> " + msg);
    }
}
