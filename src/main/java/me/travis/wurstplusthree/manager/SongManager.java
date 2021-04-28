package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.songs.DontStop;
import me.travis.wurstplusthree.util.elements.songs.FireBall;
import me.travis.wurstplusthree.util.elements.songs.HotelRoom;
import net.minecraft.client.audio.ISound;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SongManager implements Globals {

    private final List<ISound> songs = Arrays.asList(
            FireBall.sound,
            HotelRoom.sound,
            DontStop.sound
    );

    private final ISound menuSong;
    private ISound currentSong;

    public SongManager() {
        this.menuSong = this.getRandomSong();
        this.currentSong = this.getRandomSong();
    }

    public ISound getMenuSong() {
        return this.menuSong;
    }

    public void skip() {
        boolean flag = isCurrentSongPlaying();
        if (flag) {
            this.stop();
        }
        this.currentSong = songs.get((songs.indexOf(currentSong) + 1) % songs.size());
        if (flag) {
            this.play();
        }
    }

    public void play() {
        if (!this.isCurrentSongPlaying()) {
            mc.soundHandler.playSound(currentSong);
        }
    }

    public void stop() {
        if (this.isCurrentSongPlaying()) {
            mc.soundHandler.stopSound(currentSong);
        }
    }

    private boolean isCurrentSongPlaying() {
        return mc.soundHandler.isSoundPlaying(currentSong);
    }

    public void shuffle() {
        this.stop();
        Collections.shuffle(this.songs);
    }

    private ISound getRandomSong() {
        return songs.get(random.nextInt(songs.size()));
    }

}
