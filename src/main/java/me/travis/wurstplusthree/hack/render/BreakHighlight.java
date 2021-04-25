package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.event.events.BlockBreakingEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

/**
 * @author Madmegsox1
 * @since 25/04/2021
 * fixed ur shit nerd ¬ travis
 */

public class BreakHighlight extends Hack {
    public BreakHighlight(){
        super("Break Highlight", "Highlights where people are breaking", Category.RENDER, false);
    }
    ColourSetting self = new ColourSetting("Self Colour", new Colour(255,255,255), this);
    ColourSetting other = new ColourSetting("Other Colour", new Colour(160,0,0), this);
    IntSetting alpha = new IntSetting("Alpha", 90, 0, 255, this);
    HashMap<Integer, Pair<Integer, BlockPos>> breakingBlockList = new HashMap();

    @SubscribeEvent
    public void damageBlockEvent(BlockBreakingEvent event){
        if(breakingBlockList.isEmpty()){
            breakingBlockList.putIfAbsent(event.breakingID, new Pair(event.breakStage, event.pos));
        }else {
            for(int i : breakingBlockList.keySet()){
                Pair<Integer, BlockPos> current = breakingBlockList.get(i);
                if(event.breakingID != i){
                    breakingBlockList.put(event.breakingID, new Pair(event.breakStage, event.pos));
                } else if(event.breakStage > current.getKey()){
                    breakingBlockList.remove(i);
                    breakingBlockList.put(event.breakingID, new Pair(event.breakStage, event.pos));
                }
                else if(event.breakingID == i && event.pos != current.getValue()){
                    breakingBlockList.remove(i);
                }
                else if(event.breakingID == i && event.breakStage < current.getKey()){
                    breakingBlockList.remove(i);
                }
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event){
        for(int i : breakingBlockList.keySet()){
            BlockPos pos = breakingBlockList.get(i).getValue();
            int state = breakingBlockList.get(i).getKey();
            EntityPlayer player = (EntityPlayer) mc.world.getEntityByID(i);
            if(pos != null && state != -1 && mc.world.getBlockState(pos).getBlock() != Blocks.AIR){
                AxisAlignedBB bb = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos);
                bb = calcBB(bb, state);
                if(player == mc.player){
                    RenderUtil.drawBBBox(bb, self.getValue(), this.alpha.getValue());
                }else {
                    RenderUtil.drawBBBox(bb, other.getValue(), this.alpha.getValue());
                }
            }
        }
    }


    private AxisAlignedBB calcBB(AxisAlignedBB bb, int state){
        AxisAlignedBB rbb = bb;
        switch (state){
            case 0:
                rbb = bb.shrink(0.6);
                break;
            case 1:
                rbb = bb.shrink(0.65);
                break;
            case 2:
                rbb = bb.shrink(0.7);
                break;
            case 3:
                rbb = bb.shrink(0.75);
                break;
            case 4:
                rbb = bb.shrink(0.8);
                break;
            case 5:
                rbb = bb.shrink(0.85);
                break;
            case 6:
                rbb = bb.shrink(0.9);
                break;
            case 7:
                rbb = bb.shrink(0.95);
                break;
            case 8:
                rbb = bb;
                break;
        }
        return rbb;
    }
}
