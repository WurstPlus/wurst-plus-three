package me.travis.wurstplusthree.util;

import net.minecraft.client.Minecraft;

/**
 * @author Madmegsox1
 * @since 25/04/2021
 */

public class MappingUtil {
    public static final String tickLength = isObfuscated() ? "field_194149_e" : "tickLength";
    public static final String timer = isObfuscated() ? "field_71428_T" : "timer";
    public static final String placedBlockDirection = isObfuscated() ? "field_149579_d" : "placedBlockDirection";
    public static final String playerPosLookYaw = isObfuscated() ? "field_148936_d" : "yaw";
    public static final String playerPosLookPitch = isObfuscated() ? "field_148937_e" : "pitch";
    public static final String isInWeb = isObfuscated() ? "field_70134_J" : "isInWeb";
    public static final String cPacketPlayerYaw = isObfuscated() ? "field_149476_e" : "yaw";
    public static final String cPacketPlayerPitch = isObfuscated() ? "field_149473_f" : "pitch";
    public static final String renderManagerRenderPosX = isObfuscated() ? "field_78725_b" : "renderPosX";
    public static final String renderManagerRenderPosY = isObfuscated() ? "field_78726_c" : "renderPosY";
    public static final String renderManagerRenderPosZ = isObfuscated() ? "field_78723_d" : "renderPosZ";
    public static final String rightClickDelayTimer = isObfuscated() ? "field_71467_ac" : "rightClickDelayTimer";
    public static final String sPacketEntityVelocityMotionX = isObfuscated() ? "field_70159_w" : "motionX";
    public static final String sPacketEntityVelocityMotionY = isObfuscated() ? "field_70181_x" : "motionY";
    public static final String sPacketEntityVelocityMotionZ = isObfuscated() ? "field_70179_y" : "motionZ";

    public static boolean isObfuscated() {
        try {
            return Minecraft.class.getDeclaredField("instance") == null;
        } catch (Exception var1) {
            return true;
        }
    }
}
