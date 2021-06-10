package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;

@Hack.Registration(name = "Shulker Preview", description = "lets u see shulker contents", category = Hack.Category.RENDER, isListening = false)
public class ShulkerPreview extends Hack {

    public static ShulkerPreview INSTACE;

    public ShulkerPreview() {
        INSTACE = this;
    }

}
