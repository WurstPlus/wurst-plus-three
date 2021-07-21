package me.travis.wurstplusthree.hack.hacks.misc;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.BlockEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Madmegsox1
 * @since 10/06/2021
 * <p>
 * How it works:
 * <p>
 * When the player hits a block it triggers a event
 * Then it sends the start mine packet this is so
 * we can cancel the packet mine if we need to with a abort packet
 * Then it will instant mine once the block is mined!
 */

@Hack.Registration(name = "Speed Mine", description = "break shit fast idfk", category = Hack.Category.MISC, priority = HackPriority.Highest)
public final class SpeedMine extends Hack {

    private final ParentSetting packetMine = new ParentSetting("Packet Mine", this);
    private final BooleanSetting rangeCheck = new BooleanSetting("RangeCheck", true, packetMine);
    private final DoubleSetting range = new DoubleSetting("Range", 12.0, 1.0, 60.0, packetMine, s -> rangeCheck.getValue());
    private final BooleanSetting swing = new BooleanSetting("Swing", true, packetMine);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", false, packetMine);
    private final EnumSetting rotateSetting = new EnumSetting("RotateSettings", "Break", Arrays.asList("Break", "Constant", "Hit"), packetMine, s -> rotate.getValue());
    private final BooleanSetting cancel = new BooleanSetting("CancelEvent", true, packetMine);
    private final BooleanSetting packetLoop = new BooleanSetting("PacketLoop", false, packetMine);
    private final IntSetting packets = new IntSetting("Packets", 1, 1, 25, packetMine, s -> packetLoop.getValue());
    private final BooleanSetting abortPacket = new BooleanSetting("AbortPacket", true, packetMine);
    private final BooleanSetting correctHit = new BooleanSetting("Correction Hit", true, packetMine);
    private final IntSetting tickSub = new IntSetting("Tick Sub", 10, 1, 20, packetMine, s -> (rangeCheck.getValue() || correctHit.getValue()));
    private final BooleanSetting shouldLoop = new BooleanSetting("Should Loop", false, packetMine, s -> (rangeCheck.getValue() || correctHit.getValue()));

    private final ParentSetting switch0 = new ParentSetting("Switch", this);
    private final EnumSetting switchMode = new EnumSetting("SwitchMode", "None", Arrays.asList("None", "Silent", "Normal"), switch0);
    private final BooleanSetting keyMode = new BooleanSetting("KeyOnly", false, switch0);
    private final KeySetting key = new KeySetting("SwitchKey", Keyboard.KEY_NONE, switch0, v -> keyMode.getValue());
    private final BooleanSetting switchBack = new BooleanSetting("SwitchBack", false, switch0, v -> !switchMode.is("None"));
    private final BooleanSetting noDesync = new BooleanSetting("NoDesync", false, switch0, v -> switchMode.is("Silent"));

    private final ParentSetting parentInstant = new ParentSetting("Instant", this);
    private final BooleanSetting instant = new BooleanSetting("Instant Mine", false, parentInstant);
    private final IntSetting instantPacketLoop = new IntSetting("InstantPackets", 2, 2, 25, parentInstant, s -> instant.getValue());
    private final IntSetting instantDelay = new IntSetting("InstantDelay", 0, 0, 120, parentInstant, s -> instant.getValue());

    private final ParentSetting parentCombat = new ParentSetting("Combat", this);
    private final BooleanSetting packetCity = new BooleanSetting("Packet City", false, parentCombat);
    private final BooleanSetting packetBurrow = new BooleanSetting("Packet Burrow", false, parentCombat);
    private final IntSetting cityRange = new IntSetting("Combat Range", 5, 1, 15, parentCombat, s -> (packetCity.getValue() || packetBurrow.getValue()));

    private final ParentSetting parentRender = new ParentSetting("Render", this);
    private final BooleanSetting render = new BooleanSetting("Render", true, parentRender);
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
    private double time = 0;
    private int tickCount = 0;
    private int switchedSlot;
    private boolean shouldInstant;
    private boolean firstPacket;
    private int delay;
    private int oldSlot = -1;
    private boolean loopStopPackets;

    @Override
    public void onEnable() {
        switchedSlot = -1;
        shouldInstant = false;
        firstPacket = true;
        delay = 0;
        oldSlot = -1;
        loopStopPackets = true;
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public final void onBlockDamage(@NotNull BlockEvent event) {
        if (nullCheck()) return;
        if (event.getStage() == 3 && mc.playerController.curBlockDamageMP > 0.1f) {
            mc.playerController.isHittingBlock = true;
        }

        if (event.getStage() == 4) {
            if (!canBreakBlockFromPos(event.pos)) return;
            if (event.pos != lastBreakPos) {
                shouldInstant = false;
            }
            if (swing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            if(event.pos != lastPos && correctHit.getValue() && lastPos != null){
                isActive = false;
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastPos, lastFace));
                mc.playerController.isHittingBlock = false;
                mc.playerController.curBlockDamageMP = 0;
            }
            if (!isActive) {
                event.setCancelled(cancel.getValue());
                this.setPacketPos(event.pos, event.facing);
            }
        }
    }


    @Override
    public void onUpdate() {
        if (nullCheck()) return;


        if(!isActive && (packetBurrow.getValue() || packetCity.getValue())){
            if(packetCity.getValue()){
                ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>> cityPos = PlayerUtil.GetPlayersReadyToBeCitied();
                BlockPos toCity = null;
                for(Pair<EntityPlayer, ArrayList<BlockPos>> p : cityPos){
                    if(p.getKey() == mc.player)continue;
                    if(mc.player.getDistance(p.getKey()) > cityRange.getValue())continue;
                    for(BlockPos pos : p.getValue()){
                        if(toCity == null){
                            toCity = pos;
                            continue;
                        }
                        if(mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < mc.player.getDistance(toCity.getX(), toCity.getY(), toCity.getZ())){
                            toCity = pos;
                        }

                    }
                }
                if(toCity != null){
                    this.setPacketPos(toCity, BlockUtil.getPlaceableSide(toCity));
                }
            }
            if(packetBurrow.getValue() && !isActive){
                for (EntityPlayer entity : mc.world.playerEntities.stream().filter(entityPlayer -> !WurstplusThree.FRIEND_MANAGER.isFriend(entityPlayer.getName())).collect(Collectors.toList()))
                {
                    if(entity == mc.player)continue;
                    if(mc.player.getDistance(entity) > cityRange.getValue())continue;
                    if(isBurrowed(entity)){
                        BlockPos burrowPos = new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));

                        this.setPacketPos(burrowPos, BlockUtil.getPlaceableSide(burrowPos));
                    }
                }
            }
        }


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


        int subVal = 40;
        if (lastBlock == Blocks.OBSIDIAN && PlayerUtil.getBestItem(lastBlock) != null) {
            subVal = 146;
        } else if (lastBlock == Blocks.ENDER_CHEST && PlayerUtil.getBestItem(lastBlock) != null) {
            subVal = 66;
        }


        if (lastPos != null && lastBlock != null && isActive) {
            /*
             * We do this so we can cancel the mine event with a abort packet
             * I calculate when to send it by finding how many ticks it takes to mine the block
             * I then subtract by a amount and basically that's the stage where you cant cancel the mine
             */
            if ((rangeCheck.getValue() || correctHit.getValue()) && (tickCount > time - subVal - tickSub.getValue()) && loopStopPackets) {
                if (packetLoop.getValue()) {
                    for (int i = 0; i < packets.getValue(); i++) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastPos, lastFace));
                    }
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastPos, lastFace));
                }
                loopStopPackets = shouldLoop.getValue();
            }

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

        /*
         * Switch system
         */


        if (!switchMode.is("None") && switchedSlot == -1 && isActive && lastPos != null && tickCount > time - subVal && (!keyMode.getValue() || key.isDown())) {
            int slot = findBestTool(lastBlock.getBlockState().getBaseState());
            if (slot == -1) return;
            oldSlot = mc.player.inventory.currentItem;
            if (switchMode.is("Silent")) {
                if (!noDesync.getValue() || !(mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.gameSettings.keyBindUseItem.isKeyDown())) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    switchedSlot = slot;
                }
            } else {
                mc.player.inventory.currentItem = slot;
                mc.playerController.syncCurrentPlayItem();
                switchedSlot = slot;
            }
        }

        if (switchBack.getValue() && switchedSlot != -1 && (!isActive || (keyMode.getValue()) && !key.isDown())) {
            if (switchMode.is("Silent")) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            } else if (mc.player.inventory.currentItem == switchedSlot) {
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.syncCurrentPlayItem();
            }
            switchedSlot = -1;
        }


        if (isActive && rangeCheck.getValue()) { // range check
            double dis = mc.player.getDistanceSq(lastPos);
            if (Math.sqrt(dis) > range.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastPos, lastFace));
                mc.playerController.isHittingBlock = false;
                isActive = false;
                shouldInstant = false;
                firstPacket = true;
                lastPos = null;
                lastFace = null;
                lastBlock = null;
            }
        }

        tickCount++;

    }

    //thx phobos
    private int findBestTool(IBlockState blockState) {
        int bestSlot = -1;
        double max = 0.0;
        for (int i = 0; i < 9; ++i) {
            int eff;
            float speed;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty || !((speed = stack.getDestroySpeed(blockState)) > 1.0f) || !((double) (speed = (float) ((double) speed + ((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0 ? Math.pow(eff, 2.0) + 1.0 : 0.0))) > max))
                continue;
            max = speed;
            bestSlot = i;
        }
        return bestSlot;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!render.getValue()) return;
        if (isActive && lastPos != null) {
            Colour c = breakColor.getValue();
            Colour c2 = breakColorFill.getValue();

            Colour c0 = breakColor0.getValue();
            Colour c20 = breakColorFill0.getValue();

            int subVal = 40;

            if (lastBlock == Blocks.OBSIDIAN && PlayerUtil.getBestItem(lastBlock) != null) {
                subVal = 146;
            } else if (lastBlock == Blocks.ENDER_CHEST && PlayerUtil.getBestItem(lastBlock) != null) {
                subVal = 66;
            }

            if (tickCount > time - subVal) {
                c = doneColor.getValue();
                c2 = doneColorFill.getValue();

                c0 = doneColor0.getValue();
                c20 = doneColorFill0.getValue();
            }

            AxisAlignedBB bb = mc.world.getBlockState(lastPos).getSelectedBoundingBox(mc.world, lastPos);

            switch (fillMode.getValue()) {
                case "Expand":
                    bb = bb.shrink(Math.max(Math.min(normalize(tickCount, time - subVal, 0), 1.0), 0.0));
                    break;
                case "Fill":
                    bb = bb.setMaxY(bb.minY - 0.5 + (Math.max(Math.min(normalize(tickCount * 2, time - subVal, 0), 1.5), 0.0)));
                    break;
                default:
                    break;
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
                case "Gradient":
                    Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
                    RenderUtil.drawGradientBlockOutline(bb.grow(0.002f).offset(-interp.x, -interp.y, -interp.z), c0, c, 2f);
                    RenderUtil.drawOpenGradientBoxBB(bb, c2, c20, 0);
                    break;
            }
            ;
        }
        if (instant.getValue() && shouldInstant && lastBreakPos != null) {
            Colour c = instantColor.getValue();
            Colour c2 = instantColorFill.getValue();

            Colour c0 = instantColor0.getValue();
            Colour c20 = instantColorFill0.getValue();

            switch (renderMode.getValue()) {
                case "Both":
                    RenderUtil.drawBlockOutline(lastBreakPos, new Color(c.getRed(), c.getGreen(), c.getBlue(), 255), 1f, true);
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


    private boolean canBreakBlockFromPos(@NotNull final BlockPos p) {
        final IBlockState stateInterface = mc.world.getBlockState(p);
        final Block block = stateInterface.getBlock();
        return block.getBlockHardness(stateInterface, mc.world, p) != -1;
    }

    private double normalize(final double value, final double max, final double min) {
        return (1 - 0.5) * ((value - min) / (max - min)) + 0.5;
    }

    public void setPacketPos(BlockPos pos, EnumFacing facing){
        if (!canBreakBlockFromPos(pos)) return;
        if (pos != lastBreakPos) {
            shouldInstant = false;
        }
        if (packetLoop.getValue()) {
            for (int i = 0; i < packets.getValue(); i++) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
                if(!(rangeCheck.getValue() || correctHit.getValue())) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
                }
            }
        } else {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
            if(!(rangeCheck.getValue() || correctHit.getValue())) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
            }
        }
        isActive = true;
        lastFace = facing;
        lastPos = pos;
        lastBreakPos = pos;
        lastBreakFace = facing;
        firstPacket = true;
        switchedSlot = -1;
        loopStopPackets = true;
        lastBlock = mc.world.getBlockState(lastPos).getBlock();
        ItemStack item;
        if (PlayerUtil.getItemStackFromItem(PlayerUtil.getBestItem(lastBlock)) != null) {
            item = PlayerUtil.getItemStackFromItem(PlayerUtil.getBestItem(lastBlock));
        } else {
            item = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        }
        if (item == null) return;
        time = BlockUtil.getMineTime(lastBlock, item);

        tickCount = 0;

        if (rotate.getValue() && rotateSetting.is("Hit")) {
            RotationUtil.rotateHead(lastPos.getX(), lastPos.getY(), lastPos.getZ(), mc.player);
        }
    }

    private boolean isBurrowed(EntityPlayer player) {
        BlockPos pos = new BlockPos(Math.floor(player.posX), Math.floor(player.posY+0.2), Math.floor(player.posZ));
        return mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.CHEST;
    }

}
