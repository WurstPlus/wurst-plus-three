package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.gui.toasts.GuiToast;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={GuiToast.class})
public class MixinGuiToast {
}

