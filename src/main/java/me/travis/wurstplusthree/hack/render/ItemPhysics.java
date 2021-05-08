package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.DoubleSetting;

/**
 * @author Madmegsox1
 * @since 03/05/2021
 */
@Hack.Registration(name = "Item Physics", description = "Apply physics to items", category = Hack.Category.RENDER, isListening = false)
public class ItemPhysics extends Hack{
    public static ItemPhysics INSTANCE;

    public ItemPhysics(){
        INSTANCE = this;
    }

    public DoubleSetting Scaling = new DoubleSetting("Scaling", 0.5, 0.0, 10.0, this);
}
