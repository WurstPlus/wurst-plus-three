package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import net.minecraft.network.play.client.CPacketPlayer;

public class PositionManager implements Globals {

    private double x;
    private double y;
    private double z;
    private boolean onground;

    public void updatePosition() {
        this.x = mc.player.posX;
        this.y = mc.player.posY;
        this.z = mc.player.posZ;
        this.onground = mc.player.onGround;
    }

    public void restorePosition() {
        mc.player.posX = this.x;
        mc.player.posY = this.y;
        mc.player.posZ = this.z;
        mc.player.onGround = this.onground;
    }

    public void setPlayerPosition(double x, double y, double z) {
        mc.player.posX = x;
        mc.player.posY = y;
        mc.player.posZ = z;
    }

    public void setPlayerPosition(double x, double y, double z, boolean onground) {
        mc.player.posX = x;
        mc.player.posY = y;
        mc.player.posZ = z;
        mc.player.onGround = onground;
    }

    public void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos, boolean noLagBack) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
        if (setPos) {
            mc.player.setPosition(x, y, z);
            if (noLagBack) {
                this.updatePosition();
            }
        }
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
