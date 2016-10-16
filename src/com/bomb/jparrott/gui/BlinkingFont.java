package com.bomb.jparrott.gui;

import com.bomb.jparrott.game.GameContext;
import com.bomb.jparrott.game.GameInput;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jparrott on 9/29/2016.
 */
public class BlinkingFont {
    private TrueTypeFont font;
    private int blinkTime;
    private int timeRemaining;
    private boolean showText;
    private int x, y;
    private int screenWidth, screenHeight;

    public BlinkingFont(String ttfFile, int blinkTime)throws SlickException{
        // load font from a .ttf file
        try(InputStream inputStream = ResourceLoader.getResourceAsStream(ttfFile)){
            Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont2 = awtFont2.deriveFont(24f); // set font size
            this.font = new TrueTypeFont(awtFont2, false);
        }catch (IOException | FontFormatException ex){
            SlickException sl = new SlickException(ex.getMessage());
            sl.setStackTrace(ex.getStackTrace());
            throw sl;
        }
        this.blinkTime = blinkTime;
        screenWidth = GameContext.getInstance().getMap().getWidthInPixels();
        screenHeight = GameContext.getInstance().getMap().getHeightInPixels();

    }

    public BlinkingFont(TrueTypeFont font, int blinkTime){
        this.font = font;
        this.blinkTime = blinkTime;
        this.timeRemaining = blinkTime;
        screenWidth = GameContext.getInstance().getMap().getHeightInPixels();
        screenHeight = GameContext.getInstance().getMap().getWidthInPixels();

    }
    public void update(int delta){
        timeRemaining -= delta;
        if(timeRemaining <= 0){
            showText = !showText;
            timeRemaining = blinkTime;
        }
    }

    public void draw(int x, int y, String text){
        if(showText){
            font.drawString(x, y, text, Color.yellow);
        }
    }

    public void drawCentered(String text) {
        if(showText){
            x = (screenWidth - font.getWidth(text)) / 2;
            y = ((screenHeight - font.getHeight()) / 2);
            font.drawString(x, y, text, Color.yellow);
        }
    }
}














