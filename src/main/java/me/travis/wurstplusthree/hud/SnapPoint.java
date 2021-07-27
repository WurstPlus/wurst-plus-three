package me.travis.wurstplusthree.hud;

public enum SnapPoint {
    RIGHTUP,
    RIGHTDOWN,
    LEFTUP,
    LEFTDOWN,
    NONE;
    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
