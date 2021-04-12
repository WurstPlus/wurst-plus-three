package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Block.class})
public abstract class MixinBlock {
    @Shadow
    @Deprecated
    public abstract float getBlockHardness(IBlockState var1, World var2, BlockPos var3);
}

