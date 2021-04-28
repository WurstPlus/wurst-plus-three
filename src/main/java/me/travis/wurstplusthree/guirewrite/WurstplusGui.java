package me.travis.wurstplusthree.guirewrite;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.component.CategoryComponent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.manager.fonts.GuiFont;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public class WurstplusGui extends GuiScreen {
    public static final int WIDTH = 120;
    public static final int HEIGHT = 16;
    public static final int MODULE_WIDTH_OFFSET = 2;
    public static final int MODULE_SPACING = 1;

    public static final int FONT_HEIGHT = 4;
    public static final int MODULE_FONT_INDENT = 6;
    public static final int SUB_FONT_INDENT = 2 * MODULE_FONT_INDENT;

    public static final GuiFont font = WurstplusThree.GUI_FONT_MANAGER;
    public static ArrayList<CategoryComponent> categoryComponents;

    public WurstplusGui() {
        this.categoryComponents = new ArrayList<>();
        int startX = 5;
        for (Hack.Category category : Hack.Category.values()) {
            CategoryComponent categoryComponent = new CategoryComponent(category);
            categoryComponents.add(categoryComponent);

        }
    }

}
