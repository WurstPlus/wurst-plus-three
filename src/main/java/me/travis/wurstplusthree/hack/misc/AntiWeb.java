package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.BlockCollisionBoundingBoxEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * @author Madmegsox1
 * @since 04/05/2021
 */

@Hack.Registration(name = "Anti Web", description = "stops faggots from trapping you in webs", category = Hack.Category.MISC, isListening = false)
public class AntiWeb extends Hack {

    BooleanSetting disableBB = new BooleanSetting("Add BB", true, this);
    DoubleSetting bbOffset = new DoubleSetting("BB Offset", 0.0, -2.0, 2.0,this);
    BooleanSetting onGround = new BooleanSetting("On Ground", true, this);
    DoubleSetting motionY = new DoubleSetting("Set MotionY", 0.0, -1.0, 2.0, this);
    DoubleSetting motionX = new DoubleSetting("Set MotionX", 0.84, -1.0, 5.0, this);

    @SubscribeEvent
    public void bbEvent(BlockCollisionBoundingBoxEvent event){
        if(nullCheck())return;
        if(mc.world.getBlockState(event.getPos()).getBlock() instanceof BlockWeb){
            if(disableBB.getValue()) {
                event.setCanceledE(true);
                event.setBoundingBox(Block.FULL_BLOCK_AABB.contract(0, bbOffset.getValue(), 0));
            }
        }
    }


    @Override
    public void onUpdate(){
        if(mc.player.isInWeb){
            if(onGround.getValue()) {
                mc.player.isInWeb = false;
            }
            if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode)){
                mc.player.jumpMovementFactor *= 1.1;
                mc.player.motionY *= motionY.getValue();
            }else if(onGround.getValue()){
                mc.player.onGround = false;
            }
            mc.player.motionX *= motionX.getValue();
            mc.player.motionZ *= motionX.getValue();
        }
    }
}