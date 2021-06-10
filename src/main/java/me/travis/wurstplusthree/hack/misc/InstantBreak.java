package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.BlockEvent;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.MathsUtil;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;

@Hack.Registration(name = "Better Mine", description = "breaks blocks in strange ways", category = Hack.Category.MISC, isListening = false)
public class InstantBreak extends Hack {

    public static InstantBreak INSTANCE;

    public InstantBreak() {
        INSTANCE = this;
    }

    private final Timer timer = new Timer();

    public DoubleSetting range = new DoubleSetting("Range", 10.0, 0.0, 50.0, this);
    public BooleanSetting tweaks = new BooleanSetting("Tweaks", true, this);
    public EnumSetting mode = new EnumSetting("Mode", "None", Arrays.asList("Packet", "Damage", "Instant", "None"), this);
    public BooleanSetting reset = new BooleanSetting("Reset", true, this);
    public DoubleSetting damage = new DoubleSetting("Damage", 0.7, 0.0, 1.0, this);
    public BooleanSetting noBreakAnim = new BooleanSetting("No Break Animation", false, this);
    public BooleanSetting noDelay = new BooleanSetting("No Delay", false, this);
    public BooleanSetting noSwing = new BooleanSetting("No Swing", false, this);
    public BooleanSetting noTrace = new BooleanSetting("No Trace", false, this);
    public BooleanSetting noGapTrace = new BooleanSetting("No Gap Trace", false, this);
    public BooleanSetting allow = new BooleanSetting("Allow", false, this);
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting pickaxe = new BooleanSetting("Pickaxe", false, this);
    public BooleanSetting dualUse = new BooleanSetting("DualUse", false, this);
    public BooleanSetting doubleBreak = new BooleanSetting("Double Break", false, this);
    public BooleanSetting webSwitch = new BooleanSetting("Web Switch", false, this);
    public EnumSetting switchMode = new EnumSetting("SwitchMode", "None", Arrays.asList("None", "Silent", "Normal"), this);
    public BooleanSetting render = new BooleanSetting("Render", false, this);
    public BooleanSetting box = new BooleanSetting("Box", false, this, s -> render.getValue());
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    private boolean isMining = false;
    private BlockPos lastPos = null;
    private EnumFacing lastFacing = null;
    private int lastHotbarSlot = -1;
    private boolean switched = false;
    private float yaw;
    private float pitch;

    @CommitEvent(priority = EventPriority.HIGH)
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if(this.currentPos != null){
            this.setYawPitch(currentPos);
        }
    }

    @Override
    public void onDisable(){
        this.currentPos = null;
    }

    @Override
    public void onTick() {
        if (this.currentPos != null) {
            if (mc.player != null && mc.player.getDistanceSq(this.currentPos) > MathsUtil.square(this.range.getValue().floatValue())) {
                this.currentPos = null;
                this.currentBlockState = null;
                return;
            }
            if (mc.player != null && this.switchMode.getValue().equals("Silent") && this.timer.passedMs((int) (2000.0f * WurstplusThree.SERVER_MANAGER.getTpsFactor()))
                    && this.getPickSlot() != -1) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(this.getPickSlot()));
            }
            if (!mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
                this.currentPos = null;
                this.currentBlockState = null;
            } else if (this.webSwitch.getValue() && this.currentBlockState.getBlock() == Blocks.WEB
                    && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
            }
        }
    }

    @SubscribeEvent
    public void onBlockInteract(final PlayerInteractEvent.LeftClickBlock event) {
        if (switchMode.getValue().equals("Normal")) {
            this.equipBestTool(mc.world.getBlockState(event.getPos()));
        }
    }

    private void equip(final int slot, final boolean equipTool) {
        if (slot != -1) {
            if (slot != mc.player.inventory.currentItem) {
                this.lastHotbarSlot = mc.player.inventory.currentItem;
            }
            mc.player.inventory.currentItem = slot;
            //BlockTweaks.mc.playerController.syncCurrentPlayItem();
            this.switched = equipTool;
        }
    }

    private void equipBestTool(final IBlockState blockState) {
        int bestSlot = -1;
        double max = 0.0;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                float speed = stack.getDestroySpeed(blockState);
                if (speed > 1.0f) {
                    final int eff;
                    speed += (float)(((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0) ? (Math.pow(eff, 2.0) + 1.0) : 0.0);
                    if (speed > max) {
                        max = speed;
                        bestSlot = i;
                    }
                }
            }
        }
        this.equip(bestSlot, true);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (this.noDelay.getValue()) {
            mc.playerController.blockHitDelay = 0;
        }
        if (this.isMining && this.lastPos != null && this.lastFacing != null && this.noBreakAnim.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
        }
        if (this.reset.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && !this.allow.getValue()) {
            mc.playerController.isHittingBlock = false;
        }
        if (!mc.gameSettings.keyBindAttack.isKeyDown() && this.switched && switchMode.getValue().equals("Normal")) {
            this.equip(this.lastHotbarSlot, false);
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.render.getValue() && this.currentPos != null) {
            Color color = new Color(this.timer.passedMs((int) (2000.0f * WurstplusThree.SERVER_MANAGER.getTpsFactor())) ? 0 :
                    255, this.timer.passedMs((int) (2000.0f * WurstplusThree.SERVER_MANAGER.getTpsFactor())) ? 255 : 0, 0, 255);
            RenderUtil.drawBoxESP(this.currentPos, color, color, 2, true, this.box.getValue(), true);
        }
    }

    @CommitEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer && rotate.getValue() && this.currentPos != null) {
            final CPacketPlayer p = event.getPacket();
            p.yaw = yaw;
            p.pitch = pitch;
        }
        if (event.getStage() == 0) {
            CPacketPlayerDigging packet;
            if (this.noSwing.getValue() && event.getPacket() instanceof CPacketAnimation) {
                event.setCancelled(true);
            }
            if (this.noBreakAnim.getValue() && event.getPacket() instanceof CPacketPlayerDigging
                    && (packet = event.getPacket()) != null) {
                packet.getPosition();
                try {
                    for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(packet.getPosition()))) {
                        if (!(entity instanceof EntityEnderCrystal)) continue;
                        this.showAnimation();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (packet.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                    this.showAnimation(true, packet.getPosition(), packet.getFacing());
                }
                if (packet.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                    this.showAnimation();
                }
            }
        }
    }

    @CommitEvent
    public void onBlockEvent(BlockEvent event) {
        if (nullCheck()) {
            return;
        }
        if (event.getStage() == 3 && mc.world.getBlockState(event.pos).getBlock() instanceof BlockEndPortalFrame) {
            mc.world.getBlockState(event.pos).getBlock().setHardness(50.0f);
        }
        if (event.getStage() == 3 && this.reset.getValue() && mc.playerController.curBlockDamageMP > 0.1f) {
            mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4 && this.tweaks.getValue()) {
            BlockPos above;
            if (BlockUtil.canBreak(event.pos)) {
                if (this.reset.getValue()) {
                    mc.playerController.isHittingBlock = false;
                }
                switch (this.mode.getValue()) {
                    case "Packet": {
                        if (this.currentPos == null) {
                            this.currentPos = event.pos;
                            this.currentBlockState = mc.world.getBlockState(this.currentPos);
                            this.timer.reset();
                        }
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        event.setCancelled(true);
                        break;
                    }
                    case "Damage": {
                        if (!(mc.playerController.curBlockDamageMP >= this.damage.getValue().floatValue()))
                            break;
                        mc.playerController.curBlockDamageMP = 1.0f;
                        break;
                    }
                    case "Instant": {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        mc.playerController.onPlayerDestroyBlock(event.pos);
                        mc.world.setBlockToAir(event.pos);
                    }
                }
            }
            if (this.doubleBreak.getValue() && BlockUtil.canBreak(above = event.pos.add(0, 1, 0)) && mc.player.getDistance(above.getX(), above.getY(), above.getZ()) <= 5.0) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
                mc.playerController.onPlayerDestroyBlock(above);
                mc.world.setBlockToAir(above);
            }
        }
    }

    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) continue;
            return i;
        }
        return -1;
    }

    private void showAnimation(boolean isMining, BlockPos lastPos, EnumFacing lastFacing) {
        this.isMining = isMining;
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public void showAnimation() {
        this.showAnimation(false, null, null);
    }

    // retarded fix idk why it needs it
    @Override
    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    private void setYawPitch(BlockPos pos) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.yaw = angle[0];
        this.pitch = angle[1];
    }
}
