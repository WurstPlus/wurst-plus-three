package me.travis.wurstplusthree.util;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public class Timer {
    private long time = -1L;

    public boolean passedS(double s) {
        return passedMs((long) s * 1000L);
    }

    public boolean passedDms(double dms) {
        return passedMs((long) dms * 10L);
    }

    public boolean passedDs(double ds) {
        return passedMs((long) ds * 100L);
    }

    public boolean passedMs(long ms) {
        return passedNS(convertToNS(ms));
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - convertToNS(ms);
    }

    public boolean passedNS(long ns) {
        return (System.nanoTime() - this.time >= ns);
    }

    public long getPassedTimeMs() {
        return getMs(System.nanoTime() - this.time);
    }

    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }
}
