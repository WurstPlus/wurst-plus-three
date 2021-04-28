package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.songs.DontStop;
import me.travis.wurstplusthree.util.elements.songs.FireBall;
import me.travis.wurstplusthree.util.elements.songs.HotelRoom;
import net.minecraft.client.audio.ISound;

import java.util.Arrays;
import java.util.List;

public class SongManager implements Globals {

    private final List<ISound> songs = Arrays.asList(
            FireBall.sound,
            HotelRoom.sound,
            DontStop.sound
    );

    private final ISound menuSong;

    public SongManager() {
        this.menuSong = this.getRandomSong();
    }

    public ISound getMenuSong() {
        return this.menuSong;
    }

    public ISound getRandomSong() {
        return songs.get(random.nextInt(songs.size()));
    }

}
