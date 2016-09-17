package com.bomb.jparrott.animation;

import com.bomb.jparrott.object.Bomb;
import com.bomb.jparrott.object.BombPowerUp;
import com.bomb.jparrott.object.CoinPowerUp;
import com.bomb.jparrott.object.Enemy;
import com.bomb.jparrott.object.Explosion;
import com.bomb.jparrott.object.Player;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jparrott on 11/20/2015.
 */
public class AnimationFactory {

    public final static String NORTH = "north";
    public final static String SOUTH = "south";
    public final static String WEST = "west";
    public final static String EAST = "east";
    public final static String DEAD = "dead";
    public final static String DEFAULT = "default";

    public final static String BOMB_EXPLODING = "exploding";
    public final static String BOMB_FUSE = "fuse";

    private final static Class PLAYER_CLASS = Player.class;
    private final static Class ENEMY_CLASS = Enemy.class;
    private final static Class BOMB_CLASS = Bomb.class;
    private final static Class BOMB_POWER_UP_CLASS = BombPowerUp.class;
    private final static Class COIN_POWER_UP_CLASS = CoinPowerUp.class;


    //no constructor
    private AnimationFactory(){}

    public static Map<String, Animation> getAnimationMap(Animatable animatable, Map<String, Object> options) throws SlickException{
        Map<String, Animation> animationMap = new HashMap<String, Animation>();
        Class animatableClass = animatable.getClass();

        if(PLAYER_CLASS.equals(animatableClass)){
            animationMap = getPlayerAnimationMap();

        }
        else if(ENEMY_CLASS.equals(animatableClass)){
            animationMap = getEnemyAnimationMap(options);
        }
        else if(BOMB_CLASS.equals(animatableClass)){
            animationMap = getBombAnimationMap();
        }
        else if(BOMB_POWER_UP_CLASS.equals(animatableClass)){
            animationMap = getBombPowerUpAnimationMap();
        }
        else if(COIN_POWER_UP_CLASS.equals(animatableClass)){
            animationMap = getCoinPowerUpAnimationMap();
        }
        else{
            throw new UnsupportedOperationException(String.format("Unsupported class %s", animatableClass.getName()));
        }

        return animationMap;

    }

    public static Map<String, Animation> getAnimationMap(Animatable animatable) throws SlickException{
        return getAnimationMap(animatable, new HashMap<String, Object>());
    }

    private static Map<String, Animation> getPlayerAnimationMap() throws SlickException{
        Map<String, Animation> animationMap = new HashMap<String, Animation>();

        Animation animation;
        SpriteSheet spriteSheet = new SpriteSheet("data/images//playerSpriteSheet.png", 32, 32, new Color(255, 0, 255));

        //NORTH Animation
        int[] uFrames = new int[] {0, 0, 1, 0};
        animation = new Animation(spriteSheet, uFrames, new int[] {150, 150});
        animation.setAutoUpdate(false);
        animationMap.put(NORTH, animation);

        //SOUTH Animation
        int[] dFrames = new int[] {0, 1, 1, 1};
        animation = new Animation(spriteSheet, dFrames, new int[] {150, 150});
        animation.setAutoUpdate(false);
        animationMap.put(SOUTH, animation);

        //WEST Animation
        int[] lFrames = new int[] {0, 2, 1, 2};
        animation = new Animation(spriteSheet, lFrames, new int[] {150, 150});
        animation.setAutoUpdate(false);
        animationMap.put(WEST, animation);

        //EAST Animation
        int[] rFrames = new int[] {0, 3, 1, 3};
        animation = new Animation(spriteSheet, rFrames, new int[] {150, 150});
        animation.setAutoUpdate(false);
        animationMap.put(EAST, animation);

        //DEAD animation
        animation = new Animation(new SpriteSheet("data/images//dead.png", 32, 32, new Color(255, 0, 255)), new int[] {0,0,1,0}, new int[] {150, 150});
        animationMap.put(DEAD, animation);

        //DEFAULT animation
        animationMap.put(DEFAULT, animationMap.get(EAST));

        return animationMap;
    }

    private static Map<String, Animation> getEnemyAnimationMap(Map<String, Object> options) throws SlickException{
        Map<String, Animation> animationMap = new HashMap<String, Animation>();

        Animation animation;
        SpriteSheet spriteSheet = new SpriteSheet("data/images//bug.png", 32, 32, new Color(255, 0, 255));

        boolean dead = false;
        Object value = options.get("dead");
        if(value instanceof Boolean){
            dead = (Boolean)value;
        }

        boolean vulnerable = false;
        value = options.get("vulnerable");
        if(value instanceof Boolean){
            vulnerable = (Boolean)value;
        }

        if(dead){
            //DEAD animationMap
            animation = new Animation(spriteSheet, new int[] {4,0, 5,0}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(NORTH, animation);

            animation = new Animation(spriteSheet, new int[] {4,1, 5,1}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(SOUTH, animation);

            animation = new Animation(spriteSheet, new int[] {4,2, 5,2}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(WEST, animation);

            animation = new Animation(spriteSheet, new int[] {4,3, 5,3}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(EAST, animation);

            //DEFAULT animation
            animationMap.put(DEFAULT, animationMap.get(EAST));
        }
        else if(vulnerable){

            //VULNERABLE animationMap
            animation = new Animation(spriteSheet, new int[] {2,0, 3,0}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(NORTH, animation);

            animation = new Animation(spriteSheet, new int[] {2,1, 3,1}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(SOUTH, animation);

            animation = new Animation(spriteSheet, new int[] {2,2, 3,2}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(WEST, animation);

            animation = new Animation(spriteSheet, new int[] {2,3, 3,3}, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(EAST, animation);

            //DEFAULT animation
            animationMap.put(DEFAULT, animationMap.get(EAST));

        } else {
            //NORTH Animation
            int[] uFrames = new int[] {0, 0, 1, 0};
            animation = new Animation(spriteSheet, uFrames, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(NORTH, animation);

            //SOUTH Animation
            int[] dFrames = new int[] {0, 1, 1, 1};
            animation = new Animation(spriteSheet, dFrames, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(SOUTH, animation);

            //WEST Animation
            int[] lFrames = new int[] {0, 2, 1, 2};
            animation = new Animation(spriteSheet, lFrames, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(WEST, animation);

            //EAST Animation
            int[] rFrames = new int[] {0, 3, 1, 3};
            animation = new Animation(spriteSheet, rFrames, new int[] {150, 150});
            animation.setAutoUpdate(false);
            animationMap.put(EAST, animation);

            //DEFAULT animation
            animationMap.put(DEFAULT, animationMap.get(EAST));

        }


        return animationMap;
    }

    private static Map<String, Animation> getBombAnimationMap() throws SlickException{
        Map<String, Animation> animationMap = new HashMap<String, Animation>();

        Animation animation;
        SpriteSheet bombSpriteSheet = new SpriteSheet("data/images//bomb.png", 32, 32);

        //FUSE Animation
        animation = new Animation(bombSpriteSheet, new int[] {0,0,1,0}, new int[]{150, 150});
        animationMap.put(BOMB_FUSE, animation);

        //EXPLODING Animation
        animation = new Animation(bombSpriteSheet, new int[] {0,1,1,1,0,2,1,1}, new int[]{150, 150, 150, 150});
        animation.setLooping(false);
        animationMap.put(BOMB_EXPLODING, animation);

        //DEFAULT animation
        animationMap.put(DEFAULT, animationMap.get(BOMB_FUSE));

        return animationMap;

    }

    private static Map<String, Animation> getCoinPowerUpAnimationMap() throws SlickException{
        Map<String, Animation> animationMap = new HashMap<String, Animation>();

        Animation animation;
//        SpriteSheet spriteSheet = new SpriteSheet("data/images//coins.png", 32, 32, new Color(255, 0, 255));
        SpriteSheet spriteSheet = new SpriteSheet("data/images//rupee.png", 32, 32, new Color(255, 0, 255));

        //DEFAULT animation
//        animation = new Animation(spriteSheet, new int[] {0,0, 1,0, 2,0, 3,0, 4,0, 5,0, 6,0, 5,0, 4,0, 3,0, 2,0, 1,0}, new int[]{150,150,150,150,150,150,150,150,150,150,150,150});
        animation = new Animation(spriteSheet, new int[] {0,0}, new int[] {150});
        animation.setAutoUpdate(false);
        animationMap.put(DEFAULT, animation);



        return animationMap;

    }

    private static Map<String, Animation> getBombPowerUpAnimationMap() throws SlickException{
        Map<String, Animation> animationMap = new HashMap<String, Animation>();

        Animation animation;
        SpriteSheet spriteSheet = new SpriteSheet("data/images//bomb.png", 32, 32);

        //DEFAULT animation
        animation = new Animation(spriteSheet, new int[] {0,0,1,0}, new int[]{150, 150});
        animationMap.put(DEFAULT, animation);

        return animationMap;
    }
}
