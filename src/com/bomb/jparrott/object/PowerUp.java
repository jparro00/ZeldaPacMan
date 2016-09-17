package com.bomb.jparrott.object;

import com.bomb.jparrott.game.GameContext;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * Created by jparrott on 11/16/2015.
 */
public abstract class PowerUp extends GameObject{

    private final static int DEFAULT_RENDERABLE_LAYER = 0;
    protected Animation sprite;

    protected PowerUp(GameContext gameContext, int xBlock, int yBlock) throws SlickException{
        super(gameContext, 0, 0, 32, 32);
        this.setXBlock(xBlock);
        this.setYBlock(yBlock);
        setRenderableLayer(DEFAULT_RENDERABLE_LAYER);
    }

    @Override
    public void update(int delta) {
        sprite.update(delta);
    }

    @Override
    public void draw() {
        sprite.draw(x, y);
    }
}
