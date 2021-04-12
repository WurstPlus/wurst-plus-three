package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.item.ItemFood;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ItemFood.class})
public class MixinItemFood {
}

