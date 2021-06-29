package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.hack.hacks.player.Jesus;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value={Block.class})
public abstract class MixinBlock {
    @Shadow
    @Deprecated
    public abstract float getBlockHardness(IBlockState var1, World var2, BlockPos var3);

    @Inject(method={"addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void addCollisionBoxToListHook(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState, CallbackInfo info) {
        try {
            if (Jesus.INSTANCE.isEnabled() && Jesus.INSTANCE.mode.is("Trampoline")
                    && Globals.mc.player != null && state != null && state.getBlock() instanceof BlockLiquid
                    && !(entityIn instanceof EntityBoat) && !Globals.mc.player.isSneaking()
                    && Globals.mc.player.fallDistance < 3.0f && !EntityUtil.isAboveLiquid(Globals.mc.player)
                    && EntityUtil.checkForLiquid(Globals.mc.player, false) || EntityUtil.checkForLiquid(Globals.mc.player, false)
                    && Globals.mc.player.getRidingEntity() != null && Globals.mc.player.getRidingEntity().fallDistance < 3.0f && EntityUtil.isAboveBlock(Globals.mc.player, pos)) {
                AxisAlignedBB offset = Jesus.offset.offset(pos);
                if (entityBox.intersects(offset)) {
                    collidingBoxes.add(offset);
                }
                info.cancel();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

}

