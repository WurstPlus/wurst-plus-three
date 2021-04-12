package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={BlockEndPortalFrame.class})
public class MixinBlockEndPortalFrame {
    @Shadow
    @Final
    protected static AxisAlignedBB AABB_BLOCK;
}

