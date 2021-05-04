package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.event.events.BlockCollisionBoundingBoxEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Madmegsox1
 * @since 04/05/2021
 */

@Hack.Registration(name = "Anti Web", description = "stops faggots from trapping you in webs", category = Hack.Category.MISC, isListening = false)
public class AntiWeb extends Hack {

    BooleanSetting disableBB = new BooleanSetting("Disable BB", true, this);
    DoubleSetting bbOffset = new DoubleSetting("BB Offset", 0.0, -2.0, 2.0,this);

    @SubscribeEvent
    public void bbEvent(BlockCollisionBoundingBoxEvent event){
        if(nullCheck())return;
        if(mc.world.getBlockState(event.getPos()).getBlock() instanceof BlockWeb){
            if(disableBB.getValue()) {
                event.setCanceled(true);
                event.setBoundingBox(Block.FULL_BLOCK_AABB.contract(0, bbOffset.getValue(), 0));
            } //TODO 1. set block to web 2. set mc.player motionX *= 0.84
        }
    }


    @Override
    public void onUpdate(){

    }
}
