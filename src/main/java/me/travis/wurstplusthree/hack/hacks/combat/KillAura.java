package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.DamageUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;

import java.util.Arrays;

@Hack.Registration(name = "Kill Aura", description = "hits people", category = Hack.Category.COMBAT, priority = HackPriority.Low)
public class KillAura extends Hack {

    public EnumSetting mode = new EnumSetting("Mode", "Normal", Arrays.asList("Normal", "32k"), this);
    public EnumSetting switch0 = new EnumSetting("Switch", "None", Arrays.asList("None", "Auto", "SwordOnly"), this);
    public EnumSetting targetMode = new EnumSetting("Target", "Focus", Arrays.asList("Focus", "Closest", "Health"), this);
    public DoubleSetting range = new DoubleSetting("Range", 4.5, 0.0, 7.0, this);
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting raytrace = new BooleanSetting("Walls", true, this);
    public BooleanSetting swingArm = new BooleanSetting("Swing", true, this);
    public IntSetting ttkDelay = new IntSetting("32kDelay", 3, 0, 10, this, s -> mode.is("32k"));

    private Entity target;
    private int ticksPassed;

    @Override
    public void onEnable() {
        this.target = null;
        this.ticksPassed = 0;
    }

    @Override
    public void onTick() {
        if (!this.rotate.getValue()) {
            this.doKA();
        }
        this.ticksPassed++;
    }

    @CommitEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue()) {
            if ((this.target = this.getTarget()) != null) {
                WurstplusThree.ROTATION_MANAGER.lookAtEntity(this.target);
                this.doKA();
            }
        }
    }

    private void doKA() {
        this.target = this.getTarget();
        if (this.target == null) return;
        switch (switch0.getValue()) {
            case "Auto":
                for (int I = 0; I < 9; ++I)
                {
                    if (mc.player.inventory.getStackInSlot(I).getItem() instanceof ItemSword)
                    {
                        if (EntityUtil.is32k(mc.player.inventory.getStackInSlot(I)) || mode.is("Normal")) {
                            mc.player.inventory.currentItem = I;
                            mc.playerController.updateController();
                            break;
                        }
                    }
                }
            case "SwordOnly":
                if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                    return;
                }
        }
        if (this.mode.is("32k")) {
            if (EntityUtil.holding32k(mc.player) && this.ticksPassed >= this.ttkDelay.getValue()) {
                this.ticksPassed = 0;
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (EntityUtil.isntValid(player, this.range.getValue())) continue;
                    EntityUtil.attackEntity(player, true, this.swingArm.getValue());
                }
            }
        } else {
            if (!this.shouldWait()) {
                EntityUtil.attackEntity(this.target, false, this.swingArm.getValue());
            }
        }
    }

    private boolean shouldWait() {
        if (mc.player.getCooledAttackStrength(this.getLagComp()) < 1) {
            return true;
        } else return mc.player.ticksExisted % 2 == 1;
    }

    private float getLagComp() {
        return -(20 - WurstplusThree.SERVER_MANAGER.getTPS());
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue().floatValue();
        double maxHealth = 36.0;
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityPlayer && !EntityUtil.isntValid(entity, distance)) {
                if (!this.raytrace.getValue() && !mc.player.canEntityBeSeen(entity)) continue;
                if (target == null) {
                    target = entity;
                    distance = mc.player.getDistanceSq(entity);
                    maxHealth = EntityUtil.getHealth(entity);
                    continue;
                }
                if (DamageUtil.isArmorLow((EntityPlayer) entity, 18)) {
                    target = entity;
                    break;
                }
                if (this.targetMode.is("Health") && mc.player.getDistanceSq(entity) < distance) {
                    target = entity;
                    distance = mc.player.getDistanceSq(entity);
                    maxHealth = EntityUtil.getHealth(entity);
                }
                if (this.targetMode.is("Health") || !((double) EntityUtil.getHealth(entity) < maxHealth))
                    continue;
                target = entity;
                distance = mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
        }
        return target;
    }

    @Override
    public String getDisplayInfo() {
        return target instanceof EntityPlayer ? target.getName() : null;
    }

}
