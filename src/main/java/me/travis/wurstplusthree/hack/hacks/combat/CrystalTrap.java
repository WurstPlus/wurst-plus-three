package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
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
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Hack.Registration(name = "Crystal Trap", description = "Traps your enemy and proceeds to RAPE him WTF?!?!?!", category = Hack.Category.COMBAT, priority = HackPriority.Highest)
public class CrystalTrap extends Hack {
    DoubleSetting range = new DoubleSetting("Range", 4.5, 0.0, 7.0, this);
    EnumSetting structure = new EnumSetting("Structure", "Minimum", Arrays.asList("Minimum", "Trap", "NoStep"), this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);
    EnumSetting breakMode = new EnumSetting("BreakMode", "Packet", Arrays.asList("Sequential", "Normal", "Packet"), this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    ColourSetting color = new ColourSetting("Render", new Colour(0, 0, 0 ,255), this);
    IntSetting breakdelay = new IntSetting("BreakDelay", 0, 0, 10, this);
    BooleanSetting stopCA = new BooleanSetting("StopCa", false, this);
    private int offsetStep = 0;
    private int obsidianslot;
    private int pickslot;
    private boolean sendPacket;
    private int delayCounter = 0;
    private boolean stoppedCa;
    private int tickCounter = 0;
    private step currentStep;
    private BlockPos breakBlock;
    private boolean firstPacket = true;
    Entity player;
    EntityEnderCrystal crystal;

    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.disable();
            return;
        }
        if (stopCA.getValue()) {
            if (CrystalAura.INSTANCE.isEnabled()) {
                stoppedCa = true;
                CrystalAura.INSTANCE.disable();
            }
        }
        sendPacket = false;
        crystal = null;
        breakBlock = null;
        firstPacket = true;
        player = null;
        currentStep = step.Trapping;
        player = findClosestTarget();
    }

    @Override
    public void onDisable() {
        if (stoppedCa) {
            CrystalAura.INSTANCE.enable();
            stoppedCa = false;
        }
    }

    public enum step {
        Trapping,
        Breaking,
        Explode
    }

    @Override
    public void onUpdate() {
        if (player == null) return;
        check();
        switch (currentStep) {
            case Trapping:
                final List<Vec3d> place_targets = new ArrayList<Vec3d>();
                if (structure.is("Minimum"))
                    Collections.addAll(place_targets, offsetsMinimum);
                if (structure.is("Trap"))
                    Collections.addAll(place_targets, offsetsTrap);
                if (structure.is("NoStep"))
                    Collections.addAll(place_targets, offsetsNoStep);
                if (offsetStep >= place_targets.size()) {
                    offsetStep = 0;
                    break;
                }
                boolean foundblock = false;
                while (!foundblock && offsetStep <= place_targets.size() - 1) {
                    final BlockPos offset_pos = new BlockPos(place_targets.get(offsetStep));
                    final BlockPos target_pos = new BlockPos(player.getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());

                    if (mc.world.getBlockState(target_pos).getMaterial().isReplaceable()) {
                        foundblock = true;
                    }

                    for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
                        if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                            foundblock = false;
                        }
                    }

                    if (foundblock) {
                        BlockUtil.placeBlock(target_pos, obsidianslot, rotate.getValue(), rotate.getValue(), swing);
                    }

                    offsetStep++;
                }
                if (breakBlock != null)
                if (mc.world.getBlockState(new BlockPos(breakBlock)).getBlock() instanceof BlockObsidian) {
                    currentStep = step.Breaking;
                    sendPacket = false;
                    tickCounter = 0;
                }
                return;
            case Breaking:
                boolean rotated = false;
                if (crystal == null && (tickCounter > 42) || breakMode.is("Sequential")) {
                    if (rotate.getValue()) {
                        RotationUtil.faceVector(new Vec3d(breakBlock), true);
                        rotated = true;
                    }
                    BlockUtil.placeCrystalOnBlock(breakBlock, EnumHand.OFF_HAND, true);
                }
                if (mc.world.getBlockState(breakBlock).getBlock() instanceof BlockAir) {
                    currentStep = step.Explode;
                } else {
                    InventoryUtil.switchToHotbarSlot(pickslot, false);
                    if (rotate.getValue() && !rotated) {
                        RotationUtil.faceVector(new Vec3d(breakBlock), true);
                    }
                    EnumFacing direction = BlockUtil.getPlaceableSide(breakBlock);
                    switch (breakMode.getValue()) {
                        case "Packet":
                            if (!sendPacket) {
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakBlock, direction));
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakBlock, direction));
                                sendPacket = true;
                                return;
                            }
                        case "Normal":
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            mc.playerController.onPlayerDamageBlock(breakBlock, direction);
                            return;
                        case "Sequential":
                                if(firstPacket)
                                {
                                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakBlock, direction));
                                    firstPacket = false;
                                }
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakBlock, direction));
                    }
                }
            case Explode:
                if (!(mc.world.getBlockState(breakBlock).getBlock() instanceof BlockAir)) {
                    currentStep = step.Breaking;
                    return;
                }
                if (crystal != null) {
                    if (crystal.isDead) {
                        crystal = null;
                        currentStep = step.Trapping;
                        return;
                    } else if (delayCounter >= breakdelay.getValue() - 1) {
                        if (rotate.getValue()) {
                            RotationUtil.faceVector(crystal.getPositionVector(), true);
                        }
                        EntityUtil.attackEntity(crystal, true, true);
                        delayCounter = 0;
                    }
                } else currentStep = step.Trapping;
        }
    }

    private EntityEnderCrystal getCrystal() {
        if (breakBlock != null) {
            for (Entity crystal : mc.world.loadedEntityList) {
                if (crystal instanceof EntityEnderCrystal && !crystal.isDead) {
                    if (crystal.getDistance(breakBlock.getX(), breakBlock.getY(), breakBlock.getZ()) <= 2) {
                        return (EntityEnderCrystal) crystal;
                    }
                }
            }
        }
        return null;
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public final void onPacketSend(@NotNull PacketEvent.Send event) {
        if(event.getPacket() instanceof CPacketPlayerDigging && breakMode.is("Sequential"))
        {
            CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
            if(packet.getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK)
            {
                event.setCancelled(true);
            }
            if(packet.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                if(!firstPacket)
                    event.setCancelled(true);
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (breakBlock != null) {
            RenderUtil.drawBoxESP(breakBlock, color.getColor(), color.getColor(), 2.0f, false, true, false);
        }
    }

    private void check() {
        delayCounter++;
        tickCounter++;
        if (player == null) {
            ClientMessage.sendMessage("No target lol");
            this.disable();
            return;
        }
        if (player.isDead) {
            ClientMessage.sendMessage("We fucking Killed him EZZZZZZZZZZZ");
            this.disable();
            return;
        }
        if (!EntityUtil.isInHole(player)) {
            ClientMessage.sendMessage("lmfaoo your target ran out of the hole");
            this.disable();
            return;
        }
        if (mc.player.getDistance(player) >= range.getValue()) {
            ClientMessage.sendMessage("Enemy out of range!?");
            this.disable();
            return;
        }
        if (!(mc.world.getBlockState(player.getPosition().add(0, 3, 0)).getBlock() instanceof BlockAir) || !(mc.world.getBlockState(player.getPosition().add(0, 4, 0)).getBlock() instanceof BlockAir) || !(mc.world.getBlockState(player.getPosition().add(0, 5, 0)).getBlock() instanceof BlockAir)) {
            ClientMessage.sendMessage("Oh shit no space here");
            this.disable();
            return;
        }
        crystal = getCrystal();
        if (!(mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal)) {
            ClientMessage.sendMessage("Make sure to have crystals in your offhand!!");
            this.disable();
            return;
        }
        obsidianslot = findObiInHotbar();
        if (obsidianslot == -1) {
            ClientMessage.sendMessage("No obsidian found in your hotbar (Kinda bruh moment ngl)");
            this.disable();
            return;
        }
        pickslot = findPickInHotbar();
        if (pickslot == -1) {
            ClientMessage.sendMessage("No pickaxe found what was your plan in the first place lol");
            this.disable();
            return;
        }
        breakBlock = new BlockPos(player.posX, player.posY + 2.0, player.posZ);
        if (!(mc.world.getBlockState(breakBlock).getBlock() instanceof BlockAir || mc.world.getBlockState(breakBlock).getBlock() instanceof BlockObsidian)) {
            ClientMessage.sendMessage("Something is wrong with the break position try reanabling the module");
            this.disable();
            return;
        }
    }

    public EntityPlayer findClosestTarget()  {

        if (mc.world.playerEntities.isEmpty())
            return null;

        EntityPlayer closestTarget = null;

        for (final EntityPlayer target : mc.world.playerEntities)
        {
            if (target == mc.player || !target.isEntityAlive())
                continue;

            if (target.getPositionVector() == mc.player.getPositionVector()) {
                continue;
            }

            if (!EntityUtil.isInHole(target))
                continue;

            if (WurstplusThree.FRIEND_MANAGER.isFriend(target.getName()))
                continue;

            if (!(mc.world.getBlockState(target.getPosition().add(0, 3, 0)).getBlock() instanceof BlockAir) || !(mc.world.getBlockState(target.getPosition().add(0, 4, 0)).getBlock() instanceof BlockAir) || !(mc.world.getBlockState(target.getPosition().add(0, 5, 0)).getBlock() instanceof BlockAir))
                continue;

            if (mc.player.getDistance(target) >= range.getValue())
                continue;

            if (target.getHealth() <= 0.0f)
                continue;

            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;

            closestTarget = target;
        }
        if (closestTarget == null) {
            this.disable();
            ClientMessage.sendMessage("No target disabling...");
        }
        return closestTarget;
    }

    public static int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockObsidian)
                    return i;
            }
        }
        return -1;
    }

    public static int findPickInHotbar() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String getDisplayInfo() {
        return currentStep.toString();
    }

    private final Vec3d[] offsetsMinimum = new Vec3d[]{
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsetsTrap = new Vec3d[]{
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsetsNoStep = new Vec3d[]{
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 0.0)
    };
}
