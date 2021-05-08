package me.travis.wurstplusthree.util;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.util.math.MathHelper;

public class RotationUtil implements Globals {

    public static Timer rotationTimer = new Timer();
    private static float yaw;
    private static float pitch;

    public static int getDirection4D() {
        return MathHelper.floor((double) (RotationUtil.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    public static String getDirection4D(boolean northRed) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0) {
            return "South (+Z)";
        }
        if (dirnumber == 1) {
            return "West (-X)";
        }
        if (dirnumber == 2) {
            return (northRed ? "\u00a7c" : "") + "North (-Z)";
        }
        if (dirnumber == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }

    public static void resetRotations() {
        try {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            mc.player.rotationYawHead = mc.player.rotationYaw;
            rotationTimer.reset();
        } catch (Exception ignored) {
            WurstplusThree.LOGGER.info("Failed to reset rotations...");
        }
    }

}
