package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.renderer.RenderItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={RenderItem.class})
public abstract class MixinRenderItem {
}

