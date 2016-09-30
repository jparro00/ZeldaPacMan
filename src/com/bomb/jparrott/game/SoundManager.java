package com.bomb.jparrott.game;

import com.bomb.jparrott.animation.AnimationFactory;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jparrott on 9/30/2016.
 */
public class SoundManager {

    private SoundManager instance;
    private static Map<String, Audio> audioMap;

    private SoundManager() throws SlickException{

        this.instance = this;
        this.audioMap = new HashMap<>();

        try {
            audioMap.put("die", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sounds/die.wav")));
            audioMap.put("drop_bomb", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sounds/bomb_drop.wav")));
            audioMap.put("bomb_explode", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sounds/bomb_explode.wav")));
            audioMap.put("gain_life", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sounds/gain_life.wav")));
        } catch (IOException io) {
            SlickException sl = new SlickException(io.getMessage());
            sl.setStackTrace(io.getStackTrace());
            throw sl;
        }

    }

    public static void init() throws SlickException{
        new SoundManager();
    }

    public static void play(String sound){
        Audio audio = audioMap.get(sound);
        if(sound != null){
            audio.playAsSoundEffect(1.0f, 1.0f, false);
        }
    }

}



