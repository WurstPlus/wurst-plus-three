package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.setting.type.ParentSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Madmegsox1
 * @since 22/07/2021
 */
@Hack.Registration(name = "Crosshair", description = "Renders a Crosshair like csgo", category = Hack.Category.RENDER, priority = HackPriority.Lowest)
public class Crosshair extends Hack {


    ParentSetting dot = new ParentSetting("Dot", this);
    BooleanSetting centerDot = new BooleanSetting("Center Dot", true, dot);
    BooleanSetting dotOutline = new BooleanSetting("Dot Outline", true, dot, s -> centerDot.getValue());
    IntSetting dotSize = new IntSetting("Dot Size", 2, 1, 5, dot, s -> centerDot.getValue());
    IntSetting dotOutlineSize = new IntSetting("Dot Outline Width", 1, 1, 5, dot, s -> centerDot.getValue() && dotOutline.getValue());
    ColourSetting dotColor = new ColourSetting("Dot Color", new Colour(255,255,255),  dot, s -> centerDot.getValue());
    ColourSetting dotOutlineColor = new ColourSetting("Dot Outline Color", new Colour(0, 0, 0), dot, s -> centerDot.getValue() && dotOutline.getValue());

    ParentSetting linesInner = new ParentSetting("Inner Lines", this);
    BooleanSetting innerLines = new BooleanSetting("Inner Lines", false, linesInner);
    BooleanSetting moveError = new BooleanSetting("Move Error", true, linesInner, s -> innerLines.getValue());
    BooleanSetting innerLinesOutline = new BooleanSetting("Inner Lines Outline", true, linesInner, s -> innerLines.getValue());
    ColourSetting innerLineColor = new ColourSetting("Inner Line Color", new Colour(255,255,255), linesInner, s -> innerLines.getValue());
    ColourSetting innerOutlineColor = new ColourSetting("Inner Outline Color", new Colour(0, 0, 0), linesInner, s -> innerLines.getValue());
    IntSetting innerLineLength = new IntSetting("Inner Length", 5, 1, 8, linesInner, s -> innerLines.getValue());
    IntSetting innerLineOffset = new IntSetting("Inner Offset", 1, 0, 8, linesInner, s -> innerLines.getValue());
    IntSetting innerLineWidth = new IntSetting("Inner Width", 2, 1, 5, linesInner, s -> innerLines.getValue());
    IntSetting innerLinesOutlineWidth = new IntSetting("Inner Outline Width", 1, 1, 5, linesInner, s -> innerLines.getValue() && innerLinesOutline.getValue());

    boolean shouldMove = false;

    @Override
    public void onTick(){
        if(moveError.getValue() && (mc.player.motionX > 0.1 || mc.player.motionY > 0.1 || mc.player.motionZ > 0.1 || mc.player.motionX < -0.1 || mc.player.motionY < -0.1 || mc.player.motionZ < -0.1)){
            shouldMove = true;
        }else {
            shouldMove = false;
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if(mc.gameSettings.thirdPersonView != 0)return;
        ScaledResolution sr = new ScaledResolution(mc);
        int w = sr.getScaledWidth();
        int h = sr.getScaledHeight();
        if(centerDot.getValue() && !dotOutline.getValue()) {
            RenderUtil2D.drawRectMC((w / 2) + dotSize.getValue(), (h / 2) + dotSize.getValue(), w / 2 - dotSize.getValue(), h / 2 - dotSize.getValue(), dotColor.getValue().hashCode());
        }
        else if(dotOutline.getValue() && centerDot.getValue()){
            RenderUtil2D.drawBorderedRect((w / 2) + dotSize.getValue(), (h / 2) + dotSize.getValue(), w / 2 - dotSize.getValue(), h / 2 - dotSize.getValue(), dotOutlineSize.getValue(), dotColor.getValue().hashCode(), dotOutlineColor.getValue().hashCode(), false);
        }

        if(innerLinesOutline.getValue() && innerLines.getValue()){

            //BOTTOM
            RenderUtil2D.drawRectMC(
                    (w/2) + innerLineWidth.getValue() + innerLinesOutlineWidth.getValue(),
                    (h/2)  + innerLineOffset.getValue() - innerLinesOutlineWidth.getValue() + ((shouldMove) ? innerLineOffset.getValue() : 0),
                   (w/2) - innerLineWidth.getValue() - innerLinesOutlineWidth.getValue(),
                    (h/2) + innerLineLength.getValue() + innerLineOffset.getValue() + innerLinesOutlineWidth.getValue() + ((shouldMove) ? innerLineOffset.getValue() : 0),
                    innerOutlineColor.getValue().hashCode());

            //TOP
            RenderUtil2D.drawRectMC(
                    (w/2) + innerLineWidth.getValue() + innerLinesOutlineWidth.getValue(),
                    (h/2) - innerLineOffset.getValue() + innerLinesOutlineWidth.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (w/2) - innerLineWidth.getValue() - innerLinesOutlineWidth.getValue(),
                    (h/2) - innerLineLength.getValue() - innerLineOffset.getValue() - innerLinesOutlineWidth.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    innerOutlineColor.getValue().hashCode());

            //LEFT
            RenderUtil2D.drawRectMC(
                    (w/2) - innerLineOffset.getValue() + innerLinesOutlineWidth.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) + innerLineWidth.getValue() + innerLinesOutlineWidth.getValue(),
                    (w/2) - innerLineLength.getValue() - innerLineOffset.getValue() - innerLinesOutlineWidth.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) - innerLineWidth.getValue() - innerLinesOutlineWidth.getValue(),
                    innerOutlineColor.getValue().hashCode());

            //RIGHT
            RenderUtil2D.drawRectMC(
                    (w/2) + innerLineOffset.getValue() - innerLinesOutlineWidth.getValue()  + ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) - innerLineWidth.getValue() - innerLinesOutlineWidth.getValue(),
                   (w/2) + innerLineLength.getValue() + innerLineOffset.getValue() + innerLinesOutlineWidth.getValue()  + ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) + innerLineWidth.getValue() + innerLinesOutlineWidth.getValue(),
                    innerOutlineColor.getValue().hashCode());
        }

        if(innerLines.getValue()){
            //TOP
            RenderUtil2D.drawRectMC(
                    (w/2) + innerLineWidth.getValue(),
                    (shouldMove) ?  (h/2)  + innerLineOffset.getValue() + innerLineOffset.getValue() : (h/2)  + innerLineOffset.getValue(),
                    (w/2) - innerLineWidth.getValue(),
                    (h/2) + innerLineLength.getValue() + innerLineOffset.getValue() + ((shouldMove) ? innerLineOffset.getValue() : 0),
                    innerLineColor.getValue().hashCode());

            //BOTTOM
            RenderUtil2D.drawRectMC(
                    (w/2) + innerLineWidth.getValue(),
                    (h/2) - innerLineOffset.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (w/2) - innerLineWidth.getValue(),
                    (h/2) - innerLineLength.getValue() - innerLineOffset.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    innerLineColor.getValue().hashCode());

            //LEFT
            RenderUtil2D.drawRectMC(
                    (w/2) - innerLineOffset.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) + innerLineWidth.getValue(),
                    (w/2) - innerLineLength.getValue() - innerLineOffset.getValue() - ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) - innerLineWidth.getValue() ,
                    innerLineColor.getValue().hashCode());

            //RIGHT
            RenderUtil2D.drawRectMC(
                    (w/2) + innerLineOffset.getValue() + ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) - innerLineWidth.getValue(),
                    (w/2) + innerLineLength.getValue() + innerLineOffset.getValue() + ((shouldMove) ? innerLineOffset.getValue() : 0),
                    (h/2) + innerLineWidth.getValue(),
                    innerLineColor.getValue().hashCode());
        }

    }


    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event){
        if(mc.gameSettings.thirdPersonView != 0)return;
        if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS){
            event.setCanceled(true);
        }
    }


}
