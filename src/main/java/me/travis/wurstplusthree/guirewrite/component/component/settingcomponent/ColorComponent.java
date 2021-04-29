package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.setting.type.ColourSetting;
/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class ColorComponent extends Component {
    private boolean hovered;
    private ColourSetting set;
    private HackButton button;
    private int offset;
    private int x;
    private int y;

    public ColorComponent(ColourSetting value, HackButton button, int offset){
        this.set = value;
        this.button = button;
        this.offset = offset;

        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }
}
