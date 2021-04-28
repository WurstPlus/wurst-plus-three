package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;

public class Radio extends Hack {

    public Radio() {
        super("Radio", "plays the best music", Category.MISC, false);
    }

    BooleanSetting playButton = new BooleanSetting("Play", false, this);
    BooleanSetting stopButton = new BooleanSetting("Stop", false, this);
    BooleanSetting skipButton = new BooleanSetting("Skip", false, this);
    BooleanSetting shuffleButton = new BooleanSetting("Shuffle", false, this);

    @Override
    public void onUpdate() {
        if (this.playButton.getValue()) {
            WurstplusThree.SONG_MANAGER.play();
            this.playButton.toggle();
        }
        if (this.stopButton.getValue()) {
            WurstplusThree.SONG_MANAGER.stop();
            this.stopButton.toggle();
        }
        if (this.skipButton.getValue()) {
            WurstplusThree.SONG_MANAGER.skip();
            this.skipButton.toggle();
        }
        if (this.shuffleButton.getValue()) {
            WurstplusThree.SONG_MANAGER.shuffle();
            this.shuffleButton.toggle();
        }
    }
}
