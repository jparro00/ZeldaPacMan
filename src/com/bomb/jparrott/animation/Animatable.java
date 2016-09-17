package com.bomb.jparrott.animation;

import com.bomb.jparrott.object.Renderable;
import org.newdawn.slick.Animation;

import java.util.Map;

/**
 * Created by jparrott on 11/20/2015.
 */
public interface Animatable extends Renderable{

    public Map<String, Animation> getAnimationMap();
    public void setAnimationMap(Map<String, Animation> animationMap);
}
