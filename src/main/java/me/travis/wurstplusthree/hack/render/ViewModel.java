package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.event.events.RenderItemEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.DoubleSetting;

/**
 * @author Madmegsox1
 * @since 26/04/2021
 *  -> This is dumb as well but its the only way i think you can do it lol
 */

@Hack.Registration(name = "ViewModel", description = "makes you hand look cool", category = Hack.Category.RENDER, isListening = false)
public class ViewModel extends Hack {

    DoubleSetting mainX = new DoubleSetting("mainX", 1.2, 0.0, 6.0, this);
    DoubleSetting mainY = new DoubleSetting("mainY", -0.95, -3.0, 3.0, this);
    DoubleSetting mainZ = new DoubleSetting("mainZ", -1.45, -5.0, 5.0, this);
    DoubleSetting offX = new DoubleSetting("offX", -1.2, -6.0, 0.0, this);
    DoubleSetting offY = new DoubleSetting("offY", -0.95, -3.0, 3.0, this);
    DoubleSetting offZ = new DoubleSetting("offZ", -1.45, -5.0, 5.0, this);
    DoubleSetting mainAngel = new DoubleSetting("mainAngle", 0.0, 0.0, 360.0, this);
    DoubleSetting mainRx = new DoubleSetting("mainRotationPointX", 0.0, -1.0, 1.0, this);
    DoubleSetting mainRy = new DoubleSetting("mainRotationPointY", 0.0, -1.0, 1.0, this);
    DoubleSetting mainRz = new DoubleSetting("mainRotationPointZ", 0.0, -1.0, 1.0, this);
    DoubleSetting offAngel = new DoubleSetting("offAngle", 0.0, 0.0, 360.0, this);
    DoubleSetting offRx = new DoubleSetting("offRotationPointX", 0.0, -1.0, 1.0, this);
    DoubleSetting offRy = new DoubleSetting("offRotationPointY", 0.0, -1.0, 1.0, this);
    DoubleSetting offRz = new DoubleSetting("offRotationPointZ", 0.0, -1.0, 1.0, this);
    DoubleSetting mainScaleX = new DoubleSetting("mainScaleX", 1.0, -5.0, 10.0, this);
    DoubleSetting mainScaleY = new DoubleSetting("mainScaleY", 1.0, -5.0, 10.0, this);
    DoubleSetting mainScaleZ = new DoubleSetting("mainScaleZ", 1.0, -5.0, 10.0, this);
    DoubleSetting offScaleX = new DoubleSetting("offScaleX", 1.0, -5.0, 10.0, this);
    DoubleSetting offScaleY = new DoubleSetting("offScaleY", 1.0, -5.0, 10.0, this);
    DoubleSetting offScaleZ = new DoubleSetting("offScaleZ", 1.0, -5.0, 10.0, this);

    // Like look at all this shit xd

    @CommitEvent
    public void onItemRender(RenderItemEvent event) {
        event.setMainX(mainX.getValue());
        event.setMainY(mainY.getValue());
        event.setMainZ(mainZ.getValue());

        event.setOffX(offX.getValue());
        event.setOffY(offY.getValue());
        event.setOffZ(offZ.getValue());

        event.setMainRAngel(mainAngel.getValue());
        event.setMainRx(mainRx.getValue());
        event.setMainRy(mainRy.getValue());
        event.setMainRz(mainRz.getValue());

        event.setOffRAngel(offAngel.getValue());
        event.setOffRx(offRx.getValue());
        event.setOffRy(offRy.getValue());
        event.setOffRz(offRz.getValue());

        event.setMainHandScaleX(mainScaleX.getValue());
        event.setMainHandScaleY(mainScaleY.getValue());
        event.setMainHandScaleZ(mainScaleZ.getValue());

        event.setOffHandScaleX(offScaleX.getValue());
        event.setOffHandScaleY(offScaleY.getValue());
        event.setOffHandScaleZ(offScaleZ.getValue());
    }
}
