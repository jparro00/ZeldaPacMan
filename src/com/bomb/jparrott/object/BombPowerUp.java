package com.bomb.jparrott.object;

import com.bomb.jparrott.animation.Animatable;
import com.bomb.jparrott.animation.AnimationFactory;
import com.bomb.jparrott.game.GameContext;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import java.util.Map;

/**
 * Created by jparrott on 11/19/2015.
 */
public class BombPowerUp extends PowerUp implements Animatable{

    private Map<String, Animation> animationMap;

    public BombPowerUp(int xBlock, int yBlock) throws SlickException {
        super(xBlock, yBlock);
        this.animationMap = AnimationFactory.getAnimationMap(this);
        this.sprite = animationMap.get(AnimationFactory.DEFAULT);
    }

    @Override
    public Map<String, Animation> getAnimationMap() {
        return animationMap;
    }

    @Override
    public void setAnimationMap(Map<String, Animation> animationMap) {
        this.animationMap = animationMap;
    }
}
