package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.EventStage;

/**
 * @author Madmegsox1
 * @since 26/04/2021
 *  -> dumb shit idk if this could be made better but oh well
 */

public class RenderItemEvent extends EventStage {
    double mainX, mainY, mainZ,
            offX, offY, offZ,
            mainRAngel, mainRx, mainRy, mainRz,
            offRAngel, offRx, offRy, offRz,
            mainHandScaleX, mainHandScaleY, mainHandScaleZ,
            offHandScaleX, offHandScaleY, offHandScaleZ;


    public RenderItemEvent(double mainX, double mainY, double mainZ,
                           double offX, double offY, double offZ,
                           double mainRAngel, double mainRx, double mainRy, double mainRz,
                           double offRAngel, double offRx, double offRy, double offRz,
                           double mainHandScaleX, double mainHandScaleY, double mainHandScaleZ,
                           double offHandScaleX, double offHandScaleY, double offHandScaleZ) {
        this.mainX = mainX;
        this.mainY = mainY;
        this.mainZ = mainZ;
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        this.mainRAngel = mainRAngel;
        this.mainRx = mainRx;
        this.mainRy = mainRy;
        this.mainRz = mainRz;
        this.offRAngel = offRAngel;
        this.offRx = offRx;
        this.offRy = offRy;
        this.offRz = offRz;
        this.mainHandScaleX = mainHandScaleX;
        this.mainHandScaleY = mainHandScaleY;
        this.mainHandScaleZ = mainHandScaleZ;
        this.offHandScaleX = offHandScaleX;
        this.offHandScaleY = offHandScaleY;
        this.offHandScaleZ = offHandScaleZ;
    }

    public void setMainX(double v) {
        this.mainX = v;
    }

    public void setMainY(double v) {
        this.mainY = v;
    }

    public void setMainZ(double v) {
        this.mainZ = v;
    }

    public void setOffX(double v) {
        this.offX = v;
    }

    public void setOffY(double v) {
        this.offY = v;
    }

    public void setOffZ(double v) {
        this.offZ = v;
    }

    public void setOffRAngel(double v) {
        this.offRAngel = v;
    }

    public void setOffRx(double v) {
        this.offRx = v;
    }

    public void setOffRy(double v) {
        this.offRy = v;
    }

    public void setOffRz(double v) {
        this.offRz = v;
    }

    public void setMainRAngel(double v) {
        this.mainRAngel = v;
    }

    public void setMainRx(double v) {
        this.mainRx = v;
    }

    public void setMainRy(double v) {
        this.mainRy = v;
    }

    public void setMainRz(double v) {
        this.mainRz = v;
    }

    public void setMainHandScaleX(double v) {
        this.mainHandScaleX = v;
    }

    public void setMainHandScaleY(double v) {
        this.mainHandScaleY = v;
    }

    public void setMainHandScaleZ(double v) {
        this.mainHandScaleZ = v;
    }

    public void setOffHandScaleX(double v) {
        this.offHandScaleX = v;
    }

    public void setOffHandScaleY(double v) {
        this.offHandScaleY = v;
    }

    public void setOffHandScaleZ(double v) {
        this.offHandScaleZ = v;
    }


    public double getMainX() {
        return mainX;
    }

    public double getMainY() {
        return mainY;
    }

    public double getMainZ() {
        return mainZ;
    }

    public double getOffX() {
        return offX;
    }

    public double getOffY() {
        return offY;
    }

    public double getOffZ() {
        return offZ;
    }

    public double getMainRAngel() {
        return mainRAngel;
    }

    public double getMainRx() {
        return mainRx;
    }

    public double getMainRy() {
        return mainRy;
    }

    public double getMainRz() {
        return mainRz;
    }

    public double getOffRAngel() {
        return offRAngel;
    }

    public double getOffRx() {
        return offRx;
    }

    public double getOffRy() {
        return offRy;
    }

    public double getOffRz() {
        return offRz;
    }

    public double getMainHandScaleX() {
        return mainHandScaleX;
    }

    public double getMainHandScaleY() {
        return mainHandScaleY;
    }

    public double getMainHandScaleZ() {
        return mainHandScaleZ;
    }

    public double getOffHandScaleX() {
        return offHandScaleX;
    }

    public double getOffHandScaleY() {
        return offHandScaleY;
    }

    public double getOffHandScaleZ() {
        return offHandScaleZ;
    }
}
