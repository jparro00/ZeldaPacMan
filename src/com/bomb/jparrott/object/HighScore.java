package com.bomb.jparrott.object;

import com.bomb.jparrott.game.GameContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Created by jparrott on 9/18/2016.
 */
public class HighScore extends GameObject implements Serializable{

    private static HighScore instance;
    private static int score;
    transient Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);
    transient TrueTypeFont trueTypeFont = new TrueTypeFont(font, true);
    private static Logger log = LogManager.getLogger(HighScore.class);

    private HighScore() {
        super(0, 0, 32, 32);
        this.setXBlock(6);
        this.setYBlock(0);
        setRenderableLayer(DEFAULT_RENDERABLE_LAYER);
    }

    private HighScore(int score){
        this();
        this.score = score;
    }

    public static HighScore getInstance(){

        File highScoreSave = new File(".\\data\\sav\\score.ser");
        if(instance == null){
            if(highScoreSave.exists()) {
                try (
                        FileInputStream fIn = new FileInputStream(highScoreSave);
                        ObjectInputStream ois = new ObjectInputStream(fIn);
                ) {
                    int score = (Integer)ois.readObject();
                    instance = new HighScore(score);
                } catch (IOException | ClassCastException | ClassNotFoundException ex) {
                    log.error("unable to load HighScore save file.  Initializing new HighScore");
                    log.error(ex);

                    //initialize new player if there was an issue loading the file
                    instance = new HighScore();
                }
            }
        }
        return instance;
    }

    public static int getScore(){
        return score;
    }

    @Override
    public void update(int delta) {
        int playerScore = GameContext.getInstance().getPlayer().getScore();
        if(score < playerScore){
            score = playerScore;
        }
    }

    @Override
    public void draw() {
        String scoreString = String.format("%07d", score);
        trueTypeFont.drawString(x, y, scoreString, Color.white);
    }
}
