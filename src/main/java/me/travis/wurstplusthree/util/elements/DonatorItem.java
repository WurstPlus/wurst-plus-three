package me.travis.wurstplusthree.util.elements;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.MathsUtil;

import java.awt.*;

public class DonatorItem implements Globals {

    private final String name;
    private final int size;
    private double x;
    private double y;

    private final double width;
    private final double height;

    private final int canvasWidth;
    private final int canvasHeight;

    private final Color colour;

    private double xSpeed;
    private double ySpeed;

    public DonatorItem(String name, int size, int width, int height, int canvasWidth, int canvasHeight) {
        this.name = name;
        this.size = size;

        this.x = MathsUtil.random(20, canvasWidth - 20);
        this.y = MathsUtil.random(20, canvasHeight - 20);

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        this.width = width;
        this.height = height;

        this.colour = new Color((int)(Math.random() * 0x1000000));

        this.xSpeed = offsetStart(.1);
        this.ySpeed = offsetStart(.1);
    }

    public void updatePos() {
        if (this.x + width >= canvasWidth || this.x <= 0) {
            xSpeed *= -1;
        }
        if (this.y + height >= canvasHeight || this.y <= 0) {
            ySpeed *= -1;
        }

        this.x += xSpeed;
        this.y += ySpeed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getRgb() {
        return this.colour.getRGB();
    }

    private double offsetStart(double i) {
        if (random.nextInt(2) == 0) {
            return -i;
        } else {
            return i;
        }
    }

}
