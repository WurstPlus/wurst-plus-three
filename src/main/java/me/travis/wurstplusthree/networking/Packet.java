package me.travis.wurstplusthree.networking;

import net.minecraft.client.Minecraft;

import java.io.IOException;

/**
 * @author Madmegsox1
 * @since 16/05/2021
 */

public class Packet {
    protected final Minecraft mc = Minecraft.getMinecraft();

    protected final String client = mc.player.getName() + ":" + mc.player.getUniqueID();

    public String[] run(String key, String... arguments) throws IOException {
        return null;
    }

    public String[] run(String key) throws IOException{
        return null;
    }

    public String[] run(String... arguments) throws IOException {
        return null;
    }

    public String[] run() throws IOException{
        return null;
    }
}
