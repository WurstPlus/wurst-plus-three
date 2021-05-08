package me.travis.wurstplusthree.util;

/**
 * @author Madmegsox1
 * @since 02/05/2021
 */

public class MouseUtil {

    public static int convertToMouse(int key){
        switch (key){
            case -2:
                return 0;
            case -3:
                return 1;
            case -4:
                return 2;
            case -5:
                return 3;
            case -6:
                return 4;
        }
        return -1;
    }
}
