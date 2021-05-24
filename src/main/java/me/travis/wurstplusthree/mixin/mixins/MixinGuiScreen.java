package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={GuiScreen.class})
public class MixinGuiScreen extends Gui {
}

