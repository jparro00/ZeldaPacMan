package com.bomb.jparrott.test;

import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import java.util.Map;

/**
 * Created by jparrott on 10/24/2015.
 */
public class Player extends Collider{

    public Player(TileBasedMap map, Map context, SpriteSheet spriteSheet, int spriteSize, int duration, int boundingBoxSize, float x, float y, float speed) throws SlickException {
        super(context, spriteSheet, spriteSize, duration, boundingBoxSize, x, y, speed);
    }

    public void move(Input input,AABB[][] blocked, int delta){

        int direction;
        boolean isBlocked;
        if (input.isKeyDown(Input.KEY_UP) || input.isKeyDown(Input.KEY_K)){
            direction = DIRECTION_UP;
            isBlocked = isBlocked(blocked, direction, delta);
            move(direction, delta, isBlocked);
        }
        else if (input.isKeyDown(Input.KEY_DOWN) || input.isKeyDown((Input.KEY_J))){
            direction = DIRECTION_DOWN;
            isBlocked = isBlocked(blocked, direction, delta);
            move(DIRECTION_DOWN, delta, isBlocked);
        }
        else if (input.isKeyDown(Input.KEY_LEFT) || input.isKeyDown(Input.KEY_H)){
            direction = DIRECTION_LEFT;
            isBlocked = isBlocked(blocked, direction, delta);
            move(direction, delta,isBlocked);
        }
        else if (input.isKeyDown(Input.KEY_RIGHT) || input.isKeyDown(Input.KEY_L)){
            direction = DIRECTION_RIGHT;
            isBlocked = isBlocked(blocked, direction, delta);
            move(direction, delta, isBlocked);
        }
    }

    public void dropBomb(){

    }
    @Override
    public float getSpeed(){
        float speed = super.getSpeed();
        Map properties = getCurrentTileProperties();
        if(properties != null && "true".equals(getCurrentTileProperties().get(ZeldaGameTest.TILE_SLOW))){
            speed /= 4;
        }
        return speed;
    }

}
