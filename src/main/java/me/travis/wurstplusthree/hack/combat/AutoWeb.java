package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Madmegsox1
 * @since 03/05/2021
 */

@Hack.Registration(name = "Auto Web", description = "webs ppl coz its crazy annoying", category = Hack.Category.COMBAT, isListening = false)
public class AutoWeb extends Hack {

    DoubleSetting range = new DoubleSetting("Range", 5.0, 1.0, 8.0, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    IntSetting type = new IntSetting("Type", 3, 1, 3,this);
    IntSetting delayTick = new IntSetting("Delay", 1, 0, 10, this);
    //BooleanSetting PredictPlace = new BooleanSetting("Predict", false, this);
    BooleanSetting packet = new BooleanSetting("Packet", true, this);
    BooleanSetting lowFeet = new BooleanSetting("Low Feet", false, this);
    BooleanSetting legs = new BooleanSetting("Legs", true, this);
    BooleanSetting chest = new BooleanSetting("Chest", true, this, s -> legs.getValue());
    BooleanSetting head = new BooleanSetting("Head", false , this, s -> legs.getValue() && chest.getValue());

    EntityPlayer player;
    boolean r = false;
    int delay = 0;

    @Override
    public void onEnable(){
        if(nullCheck())return;
        r = false;
        delay = 0;
    }

    @Override
    public void onTick(){
        if(type.getValue() == 3){
            r = false;
            trap();
        }
    }

    @CommitEvent
    public void onWalingEvent(UpdateWalkingPlayerEvent event){
        if(event.getStage() == 0 && type.getValue() == 2){
            r = rotate.getValue();
            trap();
        }
    }

    @Override
    public void onUpdate(){
        if(type.getValue() == 1){
            r = false;
            trap();
        }
    }

    private void trap() {
        if(delay < this.delayTick.getValue()){
            delay++;
            return;
        }
        else {
            delay = 0;
        }
        this.player = getTarget(this.range.getValue().floatValue(), false);
        List<Vec3d> placeTargets = this.getPos();
        if(placeTargets == null)return;
        this.placeList(placeTargets);
    }

    private EntityPlayer getTarget(double range, boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range) || trapped && player.isInWeb) continue;
            if (target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }
            if (!(mc.player.getDistanceSq(player) < distance)) continue;
            target = player;
            distance = mc.player.getDistanceSq(player);
        }
        return target;
    }

    private List<Vec3d> getPos() {
        ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        if(player == null)return null;
        Vec3d baseVec = this.player.getPositionVector();
        if (this.lowFeet.getValue()) {
            list.add(baseVec.add(0.0, -1.0, 0.0));
        }
        if (this.legs.getValue()) {
            list.add(baseVec);
        }
        if (this.chest.getValue()) {
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        if(this.head.getValue()){
            list.add(baseVec.add(0.0, 2.0, 0.0));
        }
        return list;
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (Vec3d vec3d3 : list) {
            BlockPos position = new BlockPos(vec3d3);
            int placeability = BlockUtil.isPositionPlaceable(position, false);
            if (placeability != 3 && placeability != 1) continue;
            this.placeBlock(position);
        }
    }

    private void placeBlock(BlockPos pos) {
        int oldSlot = mc.player.inventory.currentItem;
        if(InventoryUtil.findHotbarBlock(BlockWeb.class) == -1) return;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.findHotbarBlock(BlockWeb.class)));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        mc.playerController.updateController();
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.r, this.packet.getValue(), true);
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
    }

    private Vec3d predict(Vec3d startPos){
        final double defSpeed = 0.4913640415;
        final double x = this.player.motionX;
        final double z = this.player.motionZ;

        if(x < 0.5 && x > -0.5){
            return startPos;
        }
        if(z < 0.5 && z > -0.5){
            return startPos;
        }
        final double predictX = EntityUtil.predictPos(x, 0.152);
        final double predictZ = EntityUtil.predictPos(z, 0.152);
        return startPos;
    }

}
