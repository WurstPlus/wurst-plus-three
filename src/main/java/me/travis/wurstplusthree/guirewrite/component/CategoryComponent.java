package me.travis.wurstplusthree.guirewrite.component;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGui;
import me.travis.wurstplusthree.hack.Hack;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public class CategoryComponent {
    public ArrayList<Component> components;
    public Hack.Category category;
    private int width;
    private int height;
    public int x;
    public int y;
    public boolean isOpen;
    private boolean isDragging;
    public int dragX;
    public int dragY;

    public CategoryComponent(Hack.Category cat) {
        this.category = cat;

        this.components = new ArrayList<Component>();
        this.width = WurstplusGui.WIDTH;
        this.height = WurstplusGui.HEIGHT;
        this.x = 5; // Absolute positions for category boxes, x gets bigger over time
        this.y = 5; // Absolute positions for category boxes, y stays the same
        this.dragX = 0; // Why only x and not y???
        this.isOpen = true;
        this.isDragging = false;

        int tY = this.height;

        for (Hack mod : WurstplusThree.HACKS.getHacks()) {
            if (mod.getCategory().equals(category)) {
                /*
                Button moduleButton = new Button(mod, this, tY); // Button object
                this.components.add(moduleButton); // Adds module to arraylist
                tY += ClickGui.HEIGHT + ClickGui.MODULE_SPACING; // Adds the height to get height for next item in the list (is +1 module spacing or not?)
                 */
            }
        }
    }
}
