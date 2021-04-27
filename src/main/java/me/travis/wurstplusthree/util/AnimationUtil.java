package me.travis.wurstplusthree.util;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public class AnimationUtil {
    public static float clamp(float number, float min, float max) {
        return (number < min) ? min : Math.min(number, max);
    }

}
