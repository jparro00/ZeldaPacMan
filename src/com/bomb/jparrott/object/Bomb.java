package com.bomb.jparrott.object;

import com.bomb.jparrott.animation.Animatable;
import com.bomb.jparrott.animation.AnimationFactory;
import com.bomb.jparrott.game.GameContext;
import com.bomb.jparrott.game.SoundManager;
import org.newdawn.slick.*;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jparrott on 11/14/2015.
 */
public class Bomb extends GameObject implements Animatable{

    public final static int DEFAULT_RENDERABLE_LAYER = 0;

    private Animation currentAnimation;
    private int currentFrame;
    private float fuse;
    private int remainingFuseTime;
    private boolean exploding;
    private Explosion explosion;
    private Map<String, Animation> animationMap;

    public Bomb(float x, float y) throws SlickException{
        this(x, y, 10);

    }

    /**
     * Primary constructor
     *
     * @param gameContext
     * @param x
     * @param y
     * @param fuse
     * @throws SlickException
     */
    public Bomb(float x, float y, int fuse) throws SlickException{
        super(x, y, 32, 32);

        SpriteSheet bombSpriteSheet = new SpriteSheet("data/images/bomb.png", 32, 32);

        int horizontalCount = bombSpriteSheet.getHorizontalCount();
        int lastIndex = horizontalCount - 1;
        this.animationMap = AnimationFactory.getAnimationMap(this);
        this.currentAnimation = animationMap.get(AnimationFactory.BOMB_FUSE);
        this.currentFrame = 0;
        this.remainingFuseTime = -1;
        this.fuse = fuse;

        this.renderableLayer = DEFAULT_RENDERABLE_LAYER;
    }

    /**
     * changes the animation to explosion animation, sets a remaining fuse time, and adds an Explosion to gameContext.hazards
     */
    private void explode(){

        SoundManager.play("bomb_explode");


        exploding = true;
        currentAnimation = animationMap.get(AnimationFactory.BOMB_EXPLODING);
        int duration = currentAnimation.getDuration(0);
        remainingFuseTime = (currentAnimation.getFrameCount() * duration) - duration - duration;
        explosion = new Explosion(getAABB());
        gameContext.add(explosion);
    }

    @Override
    public void update(int delta) {

        int duration = currentAnimation.getDuration(currentAnimation.getFrame());
        //destroy this bomb if it is at the last frame
        if(exploding){
            if(remainingFuseTime <= 0){
                setDestroyed(true);
            }
            else{
                remainingFuseTime -= delta;
            }

        }
        else if(currentFrame > fuse * duration){
            explode();
        }

        currentAnimation.update(delta);
        currentFrame += delta;

    }

    @Override
    public void draw() {
        currentAnimation.draw(x, y);
    }

    @Override
    public void setDestroyed(boolean destroyed){
        if(explosion != null){
            explosion.setDestroyed(true);
        }
        super.setDestroyed(destroyed);
    }

    @Override
    public Map<String, Animation> getAnimationMap() {
        return null;
    }

    @Override
    public void setAnimationMap(Map<String, Animation> animationMap) {

    }
}
