package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={GuiIngame.class})
public class MixinGuiIngame
extends Gui {
    @Shadow
    @Final
    public GuiNewChat persistantChatGUI;
}

