package me.travis.wurstplusthree.util;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nullable;

public class SoundUtil {
    public static final ISound sound;
    static {
        sound = new ISound() {
            @Override
            public ResourceLocation getSoundLocation() {
                return new ResourceLocation("sounds/sound.ogg");
                //return null;
            }

            @Nullable
            @Override
            public SoundEventAccessor createAccessor(SoundHandler soundHandler) {
                return new SoundEventAccessor(new ResourceLocation("sounds/sound.ogg"), "Pitbull");
                //return null;
            }

            @Override
            public Sound getSound() {
                return new Sound("sound", 1, 1, 1, Sound.Type.SOUND_EVENT, false);
                //return null;
            }

            @Override
            public SoundCategory getCategory() {
                return SoundCategory.VOICE;
            }

            @Override
            public boolean canRepeat() {
                return true;
            }

            @Override
            public int getRepeatDelay() {
                return 10;
            }

            @Override
            public float getVolume() {
                return 1;
            }

            @Override
            public float getPitch() {
                return 1;
            }

            @Override
            public float getXPosF() {
                return 1;
            }

            @Override
            public float getYPosF() {
                return 0;
            }

            @Override
            public float getZPosF() {
                return 0;
            }

            @Override
            public AttenuationType getAttenuationType() {
                return AttenuationType.LINEAR;
            }
        };
    }
}

