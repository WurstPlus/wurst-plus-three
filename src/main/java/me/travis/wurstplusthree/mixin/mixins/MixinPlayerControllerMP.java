package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.BlockDestroyEvent;
import me.travis.wurstplusthree.event.events.BlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerControllerMP.class})
public class MixinPlayerControllerMP {

    @Inject(method = {"clickBlock"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(3, pos, face);
        WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = {"onPlayerDamageBlock"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(4, pos, face);
        WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "onPlayerDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V"), cancellable = true)
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BlockDestroyEvent event = new BlockDestroyEvent(pos);
        WurstplusThree.EVENT_PROCESSOR.postEvent(event);
    }

//    @Redirect(method = {"processRightClickBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemBlock;canPlaceBlockOnSide(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z"))
//    public boolean canPlaceBlockOnSideHook(ItemBlock itemBlock, World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
//        Block block = worldIn.getBlockState(pos).getBlock();
//        if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos)) {
//            side = EnumFacing.UP;
//        } else if (!block.isReplaceable(worldIn, pos)) {
//            pos = pos.offset(side);
//        }
//        IBlockState iblockstate1 = worldIn.getBlockState(pos);
//        AxisAlignedBB axisalignedbb = itemBlock.block.getDefaultState().getCollisionBoundingBox((IBlockAccess) worldIn, pos);
//        if (axisalignedbb != Block.NULL_AABB && !worldIn.checkNoEntityCollision(Objects.requireNonNull(axisalignedbb).offset(pos), null)) {
//            return false;
//        } else if (iblockstate1.getMaterial() == Material.CIRCUITS && itemBlock.block == Blocks.ANVIL) {
//            return true;
//        }
//        return iblockstate1.getBlock().isReplaceable(worldIn, pos) && itemBlock.block.canPlaceBlockOnSide(worldIn, pos, side);
//    }
}

