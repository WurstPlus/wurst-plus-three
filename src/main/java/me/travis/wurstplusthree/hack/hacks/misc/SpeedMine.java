package me.travis.wurstplusthree.hack.hacks.misc;

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
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

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
    private final BooleanSetting keyMode = new BooleanSetting("KeyOnly", false, switch0);
    private final KeySetting key = new KeySetting("SwitchKey", Keyboard.KEY_NONE, switch0, v -> keyMode.getValue());
    private final BooleanSetting switchBack = new BooleanSetting("SwitchBack", false, switch0, v -> !switchMode.is("None"));
    private final BooleanSetting noDesync = new BooleanSetting("NoDesync", false, switch0, v -> switchMode.is("Silent"));

    private final ParentSetting parentInstant = new ParentSetting("Instant", this);
    private final BooleanSetting instant = new BooleanSetting("Instant Mine", false, parentInstant);
    private final IntSetting instantPacketLoop = new IntSetting("InstantPackets", 2, 2, 25, parentInstant, s -> instant.getValue());
    private final IntSetting instantDelay = new IntSetting("InstantDelay", 0, 0, 120, parentInstant,s -> instant.getValue());

    private final ParentSetting parentRender = new ParentSetting("Render", this);
    private final BooleanSetting render = new BooleanSetting("Render", true , parentRender);
    private final EnumSetting renderMode = new EnumSetting("Mode", "Both", Arrays.asList("Both", "Outline", "Fill", "Gradient"), parentRender, s -> render.getValue());
    private final EnumSetting fillMode = new EnumSetting("Animation", "Expand", Arrays.asList("Expand", "Fill", "Static"), parentRender, s -> render.getValue());

    private final ColourSetting instantColor = new ColourSetting("InstantColorOutline", new Colour(100, 0, 100), parentRender, s -> render.getValue() && instant.getValue());
    private final ColourSetting instantColor0 = new ColourSetting("InstantColorOutlineTop", new Colour(10, 0, 100, 255), parentRender, s -> render.getValue() && instant.getValue() && renderMode.is("Gradient"));
    private final ColourSetting instantColorFill = new ColourSetting("InstantColorFill", new Colour(100, 0, 100, 40), parentRender, s -> render.getValue() && instant.getValue());
    private final ColourSetting instantColorFill0 = new ColourSetting("InstantColorFillTop", new Colour(10, 0, 100, 40), parentRender, s -> render.getValue() && instant.getValue() && renderMode.is("Gradient"));

    private final ColourSetting breakColor = new ColourSetting("BreakColorOutline", new Colour(255, 0, 0), parentRender, s -> render.getValue());
    private final ColourSetting breakColor0 = new ColourSetting("BreakColorOutlineTop", new Colour(10, 0, 0, 255), parentRender, s -> render.getValue() && renderMode.is("Gradient"));
    private final ColourSetting doneColor = new ColourSetting("FinishedColorOutline", new Colour(0, 255, 0), parentRender, s -> render.getValue());
    private final ColourSetting doneColor0 = new ColourSetting("FinishedColorOutlineTop", new Colour(0, 10, 0, 255), parentRender, s -> render.getValue() && renderMode.is("Gradient"));

    private final ColourSetting breakColorFill = new ColourSetting("BreakColorFill", new Colour(255, 0, 0, 40), parentRender, s -> render.getValue());
    private final ColourSetting breakColorFill0 = new ColourSetting("BreakColorFillTop", new Colour(10, 0, 0, 40), parentRender, s -> render.getValue() && renderMode.is("Gradient"));
    private final ColourSetting doneColorFill = new ColourSetting("FinishedColorFill", new Colour(0, 255, 0, 40), parentRender, s -> render.getValue());
    private final ColourSetting doneColorFill0 = new ColourSetting("FinishedColorFillTop", new Colour(0, 10, 0, 40), parentRender, s -> render.getValue() && renderMode.is("Gradient"));

    private boolean isActive;
    private EnumFacing lastFace;
    private BlockPos lastPos;
    private BlockPos lastBreakPos;
    private EnumFacing lastBreakFace;
    private Block lastBlock;
    private boolean switched = false;
    private double time = 0;
    private int tickCount = 0;
    private boolean shouldInstant;
    private boolean firstPacket;
    private int delay;
    private int oldSlot = -1;
    private boolean shouldSwitch = false;
    private EnumHand activeHand = null;

    @Override
    public void onEnable() {
        switched = false;
        shouldInstant = false;
        firstPacket = true;
        delay = 0;
        oldSlot = -1;
        shouldSwitch = false;
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
    public void onUpdate(){
        if(nullCheck())return;

        if(instant.getValue() && shouldInstant && !isActive && (delay >= instantDelay.getValue())){ // instant mine
            delay = 0;
            if(firstPacket) {
                firstPacket = false;
                for (int i = 0; i < instantPacketLoop.getValue(); i++) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                }
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                if (abortPacket.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                }
            }else {
                if(mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE && mc.world.getBlockState(lastBreakPos).getBlock() != Blocks.AIR) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastBreakPos, lastBreakFace));
                }
            }
        }
        delay++;

        if(shouldInstant && rangeCheck.getValue() && lastBreakPos != null){
            double dis = mc.player.getDistanceSq(lastBreakPos);
            shouldInstant = !(dis > range.getValue());
        }

        if(lastPos != null && lastBlock != null && isActive){

            if(rotate.getValue() && rotateSetting.is("Constant")){
                RotationUtil.rotateHead(lastPos.getX(), lastPos.getY(), lastPos.getZ(), mc.player);
            }
            if(abortPacket.getValue()) {
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
            if(mc.world.getBlockState(lastPos).getBlock() != lastBlock){
                if(rotate.getValue() && rotateSetting.is("Break")){
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

        if(isActive && rangeCheck.getValue()){ // range check
            double dis = mc.player.getDistanceSq(lastPos);
            if(dis > range.getValue()){
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

        if(lastBlock == Blocks.OBSIDIAN && PlayerUtil.getBestItem(lastBlock) != null){
            subVal = 146;
        }else if(lastBlock == Blocks.ENDER_CHEST && PlayerUtil.getBestItem(lastBlock) != null){
            subVal = 66;
        }

        if (!switchMode.is("None") && !switched && isActive && lastPos != null && tickCount > time - subVal && (!keyMode.getValue() || key.isDown())) {
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

        if (switchBack.getValue() && switched && (!isActive || (keyMode.getValue()) && !key.isDown())) {
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
        if(isActive && lastPos != null){
            Colour c = breakColor.getValue();
            Colour c2 = breakColorFill.getValue();

            Colour c0 = breakColor0.getValue();
            Colour c20 = breakColorFill0.getValue();

            int subVal = 40;

            if(lastBlock == Blocks.OBSIDIAN && PlayerUtil.getBestItem(lastBlock) != null){
                subVal = 146;
            }else if(lastBlock == Blocks.ENDER_CHEST && PlayerUtil.getBestItem(lastBlock) != null){
                subVal = 66;
            }

            if(tickCount > time - subVal){
                c = doneColor.getValue();
                c2 = doneColorFill.getValue();

                c0 = doneColor0.getValue();
                c20 = doneColorFill0.getValue();
            }

            AxisAlignedBB bb = mc.world.getBlockState(lastPos).getSelectedBoundingBox(mc.world, lastPos);

            switch(fillMode.getValue()){
                case "Expand":
                    bb = bb.shrink(Math.max(Math.min(normalize(tickCount, time - subVal, 0), 1.0), 0.0));
                    break;
                case "Fill":
                    bb = bb.setMaxY(bb.minY - 0.5 + (Math.max(Math.min(normalize(tickCount * 2, time - subVal, 0), 1.5), 0.0)));
                    break;
                default:
                    break;
            }

            switch (renderMode.getValue()){
                case "Both":
                    RenderUtil.drawBBBox(bb, c2, c2.getAlpha());
                    RenderUtil.drawBlockOutlineBB(bb, new Color(c.getRed(),c.getGreen(), c.getBlue(), 255), 1f);
                    break;
                case "Outline":
                    RenderUtil.drawBlockOutlineBB(bb, c, 1f);
                    break;
                case "Fill":
                    RenderUtil.drawBBBox(bb, c2, c2.getAlpha());
                    break;
                case "Gradient":
                    Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
                    RenderUtil.drawGradientBlockOutline(bb.grow(0.002f).offset(-interp.x, -interp.y, -interp.z), c0, c, 2f);
                    RenderUtil.drawOpenGradientBoxBB(bb, c2, c20, 0);
                    break;
            };
        }
        if(instant.getValue() && shouldInstant && lastBreakPos != null){
            Colour c = instantColor.getValue();
            Colour c2 = instantColorFill.getValue();

            Colour c0 = instantColor0.getValue();
            Colour c20 = instantColorFill0.getValue();

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
                case "Gradient":
                    RenderUtil.drawGradientBlockOutline(lastBreakPos, c0, c, 1f, 0);
                    RenderUtil.drawOpenGradientBox(lastBreakPos, c2, c20, 0);
                    break;
            }
        }
    }


    private boolean canBreakBlockFromPos(@NotNull final BlockPos p){
        final IBlockState stateInterface = mc.world.getBlockState(p);
        final Block block = stateInterface.getBlock();
        return block.getBlockHardness(stateInterface, mc.world,  p) != -1;
    }

    private double normalize(final double value, final double max, final double min){
        return  (1 - 0.5) * ((value - min) / (max - min)) + 0.5;
    }

}
