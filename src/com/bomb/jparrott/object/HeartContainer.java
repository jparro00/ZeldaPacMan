package com.bomb.jparrott.object;

import com.bomb.jparrott.animation.Animatable;
import com.bomb.jparrott.game.GameContext;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jparrott on 11/23/2015.
 */
public class HeartContainer extends GameObject {

    private GameContext gameContext;
    private List<Image> images;
    private Image currentImage;

    public HeartContainer(GameContext gameContext, int xBlock, int yBlock) throws SlickException {
        super(gameContext, 0, 0, 32, 32);

        //set the x,y coordinates by block/tile instead of by pixel location.  NOTE we have passed 0,0 in to GameObject constructor
        this.setXBlock((int) xBlock);
        this.setYBlock((int) yBlock);

        this.gameContext = gameContext;
        this.images = new ArrayList<Image>();
        SpriteSheet spriteSheet = new SpriteSheet("data/images/hearts.png", 32, 32);
        this.images.add(spriteSheet.getSprite(0, 0));
        this.images.add(spriteSheet.getSprite(1, 0));
        this.images.add(spriteSheet.getSprite(0, 2));
        this.images.add(spriteSheet.getSprite(1, 2));
        this.images.add(spriteSheet.getSprite(0, 3));
        this.currentImage = images.get(0);

    }

    public void draw(int index) {
        currentImage = images.get(index);
        draw();
    }

    @Override
    public void draw() {
        currentImage.draw(x, y);
    }

    @Override
    public void update(int delta) {
    }

}

