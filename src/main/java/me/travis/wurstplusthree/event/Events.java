package me.travis.wurstplusthree.event;

import me.travis.wurstplusthree.gui.Rainbow;
import me.travis.wurstplusthree.util.Globals;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Events implements Globals {

    public Events() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onRenderGui(final RenderGameOverlayEvent.Post event) {
        Rainbow.updateRainbow();
    }

}
