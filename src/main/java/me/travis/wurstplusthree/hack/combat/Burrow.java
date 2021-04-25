package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Burrow extends Hack {

    public Burrow() {
        super("Burrow", "wank", Category.COMBAT, false);
    }

    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);

    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.disable();
            return;
        }
        mc.player.jump();
        mc.player.jump();
    }

    @Override
    public void onUpdate() {
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.getBlockState(pos.down()).getBlock() == Blocks.AIR) {
            int old = mc.player.inventory.currentItem;
            this.switchToSlot(PlayerUtil.findObiInHotbar());
            BlockUtil.placeBlock(pos.down(), EnumHand.MAIN_HAND, rotate.getValue(), true, false);
            this.switchToSlot(old);
        }
        if (mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.3, mc.player.posZ, false));
            mc.player.setPosition(mc.player.posX, mc.player.posY - 1.3, mc.player.posZ);
            this.disable();
        }
    }

    public void switchToSlot(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

}
