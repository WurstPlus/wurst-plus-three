package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.event.events.PerspectiveEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author megyn
 * @since 01/05/2021
 */

@Hack.Registration(name = "Aspect", description = "Does aspect shit", category = Hack.Category.RENDER, isListening = false)
public class Aspect extends Hack{
    DoubleSetting aspect = new DoubleSetting("Aspect",  mc.displayWidth / mc.displayHeight + 0.0, 0.0 ,3.0, this);

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent event){
        event.setAspect(aspect.getValue().floatValue());
    }
}
