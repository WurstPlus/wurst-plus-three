package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.entity.item.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={EntityBoat.class})
public abstract class MixinEntityBoat/* extends MixinEntity*/ {
    @Shadow
    public abstract double getMountedYOffset();

}

