package me.travis.wurstplusthree.gui.windowgui.buttons;

import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.Globals;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 06/07/2021
 */

public class CategoryButton implements Globals {
    public ArrayList<Component> components;
    public Hack.Category category;


    public CategoryButton(Hack.Category category){
        this.category = category;
        this.components = new ArrayList<>();
    }
}
