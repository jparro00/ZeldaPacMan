package com.bomb.jparrott.object;

import com.bomb.jparrott.game.GameContext;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import java.awt.Font;


/**
 * Created by jparrott on 9/18/2016.
 */
public class ScoreContainer extends GameObject{

    private int score;
    Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);
    TrueTypeFont trueTypeFont = new TrueTypeFont(font, true);

    public ScoreContainer() {
        super(0, 0, 32, 32);
        this.setXBlock(17);
        this.setYBlock(0);
        setRenderableLayer(DEFAULT_RENDERABLE_LAYER);
    }


    @Override
    public void update(int delta) {
        score = gameContext.getPlayer().getScore();
    }

    @Override
    public void draw() {

        String scoreString = String.format("%07d", score);

        trueTypeFont.drawString(x, y, scoreString, Color.white);
    }
}
