package me.travis.wurstplusthree.gui.hud.element;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.util.Globals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Madmegsox1
 * @since 17/06/2021
 */

public class HudElement implements Globals {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Element {
        String name();
        int posX() default 0;
        int posY() default 0;
        float rotation() default 0;
        float scale() default 0;
        boolean enabled() default false;
    }

    public Element getElement(){
        return this.getClass().getAnnotation(Element.class);
    }

    private final String name = getElement().name();
    private int x = getElement().posX();
    private int y = getElement().posY();
    private float rotation = getElement().rotation();
    private float scale = getElement().scale();
    private boolean enabled = getElement().enabled();

    public void onRender2D(Render2DEvent event) {
    }

    public int getWidth(){
        return 30;
    }

    public int getHeight(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextHeight();
    }

     public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle(){
        this.enabled = !enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
