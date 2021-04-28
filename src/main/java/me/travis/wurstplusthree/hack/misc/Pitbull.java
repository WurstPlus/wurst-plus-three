package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.hack.Hack;

/**
 * @author Madmegsox1
 * @since 28/04/2021
 */

public class Pitbull extends Hack {
    public Pitbull(){
        super("Pitbull", "makes everyones skin pitbull",  Category.MISC, false);
    }
    public static Pitbull INSTANCE;

    @Override
    public void onEnable(){
        INSTANCE = this;
    }
}
