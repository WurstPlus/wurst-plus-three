package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.gui.component.HackButton;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;

public class ModeComponent extends Component {
    private final EnumSetting set;
    int y;
    int x;

    private int modeIndex;

    public ModeComponent(EnumSetting set){
        super(set);
        this.set = set;
        this.modeIndex = 0;
    }
    @Override
    public void renderComponent(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;
        RenderUtil2D.drawRectMC(x + WurstplusGuiNew.SETTING_OFFSET, y , x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT , isMouseOnButton(mouseX, mouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : this.set.isChild() ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR());
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(set.getName() + ": " + set.getValue(), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(set.getName() + ": " + set.getValue(), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            increment();
        }

        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            deincrement();
        }

    }

    public void increment() {
        set.setValue(set.getModes().get(((set.getModes().indexOf(set.value) + 1) % set.getModes().size())));
    }

    public void deincrement() {
        set.setValue(set.getModes().get(((set.getModes().indexOf(set.value) + 1) % set.getModes().size())));
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y  && y < this.y + WurstplusGuiNew.HEIGHT ;
    }
}
