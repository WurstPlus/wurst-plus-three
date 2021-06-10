package me.travis.wurstplusthree.util;

import java.awt.*;
import java.util.List;

/**
 * @author Madmegsox1
 * @since 01/06/2021
 *  - shit code from my old client
 */

public class KeyUtil extends Thread{
    public static List<Integer> keys_;
    public static void clip(List<Integer> keys){
        keys_ = keys;
        KeyUtil obj = new KeyUtil();
        Thread thread = new Thread(obj);
        thread.start();
    }

    public void run(){
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            return;
        }
        for (Integer integer : keys_) {
            r.keyPress(integer);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Integer integer : keys_) {
            r.keyRelease(integer);
        }
    }
}