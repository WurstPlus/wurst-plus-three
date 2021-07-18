package me.travis.wurstplusthree.hack.hacks.misc;

import me.travis.wurstplusthree.event.events.BlockDestroyEvent;
import me.travis.wurstplusthree.event.events.BlockEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Madmegsox1
 * @since 10/06/2021
 */

@Hack.Registration(name = "Speed Mine", description = "break shit fast idfk", category = Hack.Category.MISC, priority = HackPriority.Highest)
public final class SpeedMine extends Hack{
    private final ParentSetting packetMine = new ParentSetting("Packet Mine", this);
    private final BooleanSetting rangeCheck = new BooleanSetting("RangeCheck", true, packetMine);
    private final DoubleSetting range = new DoubleSetting("Range", 12.0, 1.0, 60.0, packetMine, s -> rangeCheck.getValue());
    private final BooleanSetting swing = new BooleanSetting("Swing", true, packetMine);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", false ,packetMine);
    private final EnumSetting rotateSetting = new EnumSetting("RotateSettings", "Break", Arrays.asList("Break", "Constant", "Hit"), packetMine, s -> rotate.getValue());
    private final BooleanSetting cancel = new BooleanSetting("CancelEvent", true, packetMine);
    private final BooleanSetting packetLoop = new BooleanSetting("PacketLoop", false, packetMine);
    private final IntSetting packets = new IntSetting("Packets", 1, 1, 25, packetMine, s -> packetLoop.getValue());
    private final BooleanSetting abortPacket = new BooleanSetting("AbortPacket", true ,packetMine);
    private final ParentSetting switch0 = new ParentSetting("Switch", this);
    private final EnumSetting switchMode = new EnumSetting("SwitchMode", "None", Arrays.asList("None", "Silent", "Normal"), switch0);
    private final BooleanSetting switchBack = new BooleanSetting("SwitchBack", false, switch0, v -> !switchMode.is("None"));
    private final BooleanSetting noDesync = new BooleanSetting("NoDesync", false, switch0, v -> switchMode.is("Silent"));
    private final ParentSetting parentInstant = new ParentSetting("Instant", this);
    private final BooleanSetting instant = new BooleanSetting("Instant Mine", false, parentInstant);
    private final IntSetting instantPacketLoop = new IntSetting("InstantPackets", 2, 2, 25, parentInstant, s -> instant.getValue());
    private final IntSetting instantDelay = new IntSetting("InstantDelay", 0, 0, 120, parentInstant,s -> instant.getValue());
    private final ParentSetting parentRender = new ParentSetting("Render", this);
    private final BooleanSetting render = new BooleanSetting("Render", true , parentRender);
    private final EnumSetting animation = new EnumSetting("Animation", "Grow", Arrays.asList("Grow", "Static", "Fill"), parentRender);
    private final EnumSetting renderMode = new EnumSetting("Mode", "Both", Arrays.asList("Both", "Outline", "Fill"), parentRender, s -> render.getValue());
    private final ColourSetting instantColor = new ColourSetting("InstantColorOutline", new Colour(100, 0, 100), parentRender, s -> render.getValue() && instant.getValue());
    private final ColourSetting instantColorFill = new ColourSetting("InstantColorFill", new Colour(100, 0, 100, 40), parentRender, s -> render.getValue() && instant.getValue());
    private final ColourSetting breakColor = new ColourSetting("BreakColorOutline", new Colour(255, 0, 0), parentRender, s -> render.getValue());
    private final ColourSetting doneColor = new ColourSetting("FinishedColorOutline", new Colour(0, 255, 0), parentRender, s -> render.getValue());
    private final ColourSetting breakColorFill = new ColourSetting("BreakColorFill", new Colour(255, 0, 0, 40), parentRender, s -> render.getValue());
    private final ColourSetting doneColorFill = new ColourSetting("FinishedColorFill", new Colour(0, 255, 0, 40), parentRender, s -> render.getValue());

    private boolean isActive;
    private EnumFacing lastFace;
    private BlockPos lastPos;
    private BlockPos lastBreakPos;
    private EnumFacing lastBreakFace;
    private Block lastBlock;
    private double time = 0;
    private int tickCount = 0;
    private boolean shouldInstant;
    private boolean firstPacket;
    private int delay;
    private int oldSlot = -1;
    private boolean switched = false;
    private boolean done = false;

    @Override
    public void onEnable(){
        shouldInstant = false;
        firstPacket = true;
        delay = 0;
        oldSlot = -1;
        switched = false;
        done = false;
    }


    @CommitEvent(priority = EventPriority.HIGH)
    public final void onBlockDamage(@NotNull BlockEvent event){
        if(nullCheck())return;
        if (event.getStage() == 3 && mc.playerController.curBlockDamageMP > 0.1f) {
            mc.playerController.isHittingBlock = true;
        }

        if(event.getStage() == 4) {
            if (!canBreakBlockFromPos(event.pos)) return;
            if(event.pos != lastBreakPos) {
                shouldInstant = false;
            }
            if (swing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (!isActive) {
                if (packetLoop.getValue()) {
                    for (int i = 0; i < packets.getValue(); i++) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                    }
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                }
                event.setCancelled(cancel.getValue());
                isActive = true;
                lastFace = event.facing;
                lastPos = event.pos;
                lastBreakPos = event.pos;
                lastBreakFace = event.facing;
                firstPacket = true;
                switched = false;
                lastBlock = mc.world.getBlockState(lastPos).getBlock();
                ItemStack item;
                if(PlayerUtil.getItemStackFromItem(PlayerUtil.getBestItem(lastBlock)) != null){
                    item = PlayerUtil.getItemStackFromItem(PlayerUtil.getBestItem(lastBlock));
                }else {
                    item = mc.player.getHeldItem(EnumHand.MAIN_HAND);
                }
                if (item == null) return;
                time = BlockUtil.getMineTime(lastBlock, item);

                tickCount = 0;

                if(rotate.getValue() && rotateSetting.is("Hit")){
                    RotationUtil.rotateHead(lastPos.getX(), lastPos.getY(), lastPos.getZ(), mc.player);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;

        if (instant.getValue() && shouldInstant && !isActive && (delay >= instantDelay.getValue())) { // instant mine
            delay = 0;
            if (firstPacket) {
                firstPacket = false;
                for (int i = 0; i < instantPacketLoop.getValue(); i++) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                }
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                if (abortPacket.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                }
            } else {
                if (mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE && mc.world.getBlockState(lastBreakPos).getBlock() != Blocks.AIR) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                }
            }
        }
        delay++;

        if (shouldInstant && rangeCheck.getValue() && lastBreakPos != null) {
            double dis = mc.player.getDistanceSq(lastBreakPos);
            shouldInstant = !(dis > range.getValue());
        }

        if (lastPos != null && lastBlock != null && isActive) {

            if (rotate.getValue() && rotateSetting.is("Constant")) {
                RotationUtil.rotateHead(lastPos.getX(), lastPos.getY(), lastPos.getZ(), mc.player);
            }
            if (abortPacket.getValue()) {
                if (packetLoop.getValue()) {
                    for (int i = 0; i < packets.getValue(); i++) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastPos, lastFace));
                    }
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastPos, lastFace));
                }

                isActive = true;
            }
            mc.playerController.blockHitDelay = 0;
            if (mc.world.getBlockState(lastPos).getBlock() != lastBlock) {
                if (rotate.getValue() && rotateSetting.is("Break")) {
                    RotationUtil.rotateHead(lastPos.getX(), lastPos.getY(), lastPos.getZ(), mc.player);
                }
                shouldInstant = true;
                isActive = false;
                lastBreakPos = lastPos;
                lastBreakFace = lastFace;
                lastPos = null;
                lastFace = null;
                lastBlock = null;
            }
        }

        if (isActive && rangeCheck.getValue()) { // range check
            double dis = mc.player.getDistanceSq(lastPos);
            if (dis > range.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastPos, lastFace));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastPos, lastFace));
                //mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(0,0,0), lastFace));
                mc.playerController.isHittingBlock = false;
                isActive = false;
                shouldInstant = false;
                firstPacket = true;
                lastPos = null;
                lastFace = null;
                lastBlock = null;
            }
        }
        int subVal = 40;

        if (lastBlock == Blocks.OBSIDIAN && PlayerUtil.getBestItem(lastBlock) != null) {
            subVal = 146;
        } else if (lastBlock == Blocks.ENDER_CHEST && PlayerUtil.getBestItem(lastBlock) != null) {
            subVal = 66;
        }

        if (!switchMode.is("None") && !switched && isActive && lastPos != null && tickCount > time - subVal) {
            final int slot = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
            if (slot == -1) return;
            oldSlot = mc.player.inventory.currentItem;
            if (switchMode.is("Silent")) {
                if (!noDesync.getValue() || !(mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.gameSettings.keyBindUseItem.isKeyDown())) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    switched = true;
                }
            } else {
                mc.player.inventory.currentItem = slot;
                switched = true;
            }
        }

        if (switchBack.getValue() && switched && !isActive) {
            if (switchMode.is("Silent")) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            } else if (mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                mc.player.inventory.currentItem = oldSlot;
            }
            switched = false;
        }

        tickCount++;
    }

    @Override
    public void onRender3D(Render3DEvent event){
        if(!render.getValue())return;
        if(isActive && lastPos != null) {
            Colour c = breakColor.getValue();
            Colour c2 = breakColorFill.getValue();

            int subVal = 40;

            if (lastBlock == Blocks.OBSIDIAN && PlayerUtil.getBestItem(lastBlock) != null) {
                subVal = 146;
            } else if (lastBlock == Blocks.ENDER_CHEST && PlayerUtil.getBestItem(lastBlock) != null) {
                subVal = 66;
            }

            if (tickCount > time - subVal) {
                c = doneColor.getValue();
                c2 = doneColorFill.getValue();
            }

            if (animation.is("Fill")) {
                RenderUtil.drawBoxESP(lastPos, c2, c, 1f, !renderMode.is("Box"), !renderMode.is("Outline"), (float) Math.max(Math.min(normalize(tickCount, time - subVal, 0), 1.0), 0.0) - 1);
                return;
            }
            AxisAlignedBB bb = mc.world.getBlockState(lastPos).getSelectedBoundingBox(mc.world, lastPos);
            if (animation.is("Grow")) {
                bb = bb.shrink(Math.max(Math.min(normalize(tickCount, time - subVal, 0), 1.0), 0.0));
            }
            switch (renderMode.getValue()) {
                case "Both":
                    RenderUtil.drawBBBox(bb, c2, c2.getAlpha());
                    RenderUtil.drawBlockOutlineBB(bb, new Color(c.getRed(), c.getGreen(), c.getBlue(), 255), 1f);
                    break;
                case "Outline":
                    RenderUtil.drawBlockOutlineBB(bb, c, 1f);
                    break;
                case "Fill":
                    RenderUtil.drawBBBox(bb, c2, c2.getAlpha());
                    break;
            }
            ;
        }
        if(instant.getValue() && shouldInstant && lastBreakPos != null){
            Colour c = instantColor.getValue();
            Colour c2 = instantColorFill.getValue();
            switch (renderMode.getValue()){
                case "Both":
                    RenderUtil.drawBlockOutline(lastBreakPos, new Color(c.getRed(),c.getGreen(), c.getBlue(), 255), 1f, true);
                    RenderUtil.drawBox(lastBreakPos, c2, true);
                    break;
                case "Outline":
                    RenderUtil.drawBlockOutline(lastBreakPos, c, 1f, true);
                    break;
                case "Fill":
                    RenderUtil.drawBox(lastBreakPos, c2, true);
                    break;
            };
        }
    }


    private boolean canBreakBlockFromPos(@NotNull final BlockPos p){
        final IBlockState stateInterface = mc.world.getBlockState(p);
        final Block block = stateInterface.getBlock();
        return block.getBlockHardness(stateInterface, mc.world,  p) != -1;
    }

    private double normalize(double value, double max, double min){
        return  (1 - 0.5) * ((value - min) / (max - min)) + 0.5;
    }

}
