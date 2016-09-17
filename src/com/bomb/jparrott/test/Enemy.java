package com.bomb.jparrott.test;

import org.dyn4j.geometry.AABB;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFinder;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import java.util.IllegalFormatCodePointException;
import java.util.Map;
import java.util.Random;

/**
 * Created by jparrott on 10/26/2015.
 */
public class Enemy extends Collider{

    public final static int DEFAULT_MOVEMENT_RANDOM = 0;
    public final static int DEFAULT_MOVEMENT_HORIZONTAL = 1;
    public final static int DEFAULT_MOVEMENT_VERTICAL = 2;
    public final static int DEFAULT_MOVEMENT_FOLLOW = 3;
    private int defaultMovement;
    private PathFinder pathFinder;

    public Enemy(TileBasedMap map, int maxSearchDistance, Map context, SpriteSheet spriteSheet, int spriteSize, int duration, int boundingBoxSize, float x, float y, float speed) throws SlickException {
        super(context, spriteSheet,spriteSize,duration,boundingBoxSize,x,y,speed);
        this.pathFinder = new AStarPathFinder(map, maxSearchDistance, false);
        defaultMovement = DEFAULT_MOVEMENT_RANDOM;
    }

    public void defaultMove(AABB[][] blocked, int delta){
        switch(defaultMovement){
            case DEFAULT_MOVEMENT_RANDOM :
                randomMove(blocked, delta);
                break;
            case DEFAULT_MOVEMENT_HORIZONTAL :
                horizontalMove(blocked, delta);
                break;
            case DEFAULT_MOVEMENT_VERTICAL :
                verticalMove(blocked, delta);
                break;
            case DEFAULT_MOVEMENT_FOLLOW :
                follow(blocked, delta);
                break;
            default :
                randomMove(blocked, delta);
                break;
        }
    }
    public void randomMove(AABB[][] blocked, int delta){
        if(!isBlocked(blocked, getDirection(), delta)){
            move(getDirection(), delta, isBlocked(blocked, getDirection(), delta));
        }
        else {
            int randDirection = new Random().nextInt(4);
            move(randDirection, delta, isBlocked(blocked, randDirection, delta));
        }
    }
    public void horizontalMove(AABB[][] blocked, int delta){
        int currentDirection = getDirection();

        if(!isBlocked(blocked, currentDirection, delta)){
            move(currentDirection, delta, isBlocked(blocked, currentDirection, delta));
        }
        else {
            currentDirection = currentDirection == DIRECTION_RIGHT ? DIRECTION_LEFT : DIRECTION_RIGHT;
            if(!isBlocked(blocked, currentDirection, delta)){
                move(currentDirection, delta, isBlocked(blocked, currentDirection, delta));
            }
        }
    }
    public void verticalMove(AABB[][] blocked, int delta){
        int currentDirection = getDirection();

        if(!isBlocked(blocked, currentDirection, delta)){
            move(currentDirection, delta, isBlocked(blocked, currentDirection, delta));
        }
        else {
            currentDirection = currentDirection == DIRECTION_UP ? DIRECTION_DOWN : DIRECTION_UP;
            if(!isBlocked(blocked, currentDirection, delta)){
                move(currentDirection, delta, isBlocked(blocked, currentDirection, delta));
            }
        }
    }
    public void autoMove(AABB[][] blocked, int delta){

        try {
            int tx = (Integer)context.get(ZeldaGameTest.CONTEXT_PLAYER_X_BLOCK);
            int ty = (Integer)context.get(ZeldaGameTest.CONTEXT_PLAYER_Y_BLOCK);

            if (!isBlocked(blocked, getDirection(), delta)) {
                move(getDirection(), delta, isBlocked(blocked, getDirection(), delta));
            } else {
                PathFinder pathFinder = (PathFinder) context.get("pathFinder");

                Path path = pathFinder.findPath(this, getXBlock(), getYBlock(), tx, ty);
                Path.Step step = path.getStep(1);

                if (step.getX() > getXBlock()) {
                    setDirection(DIRECTION_RIGHT);
                } else if (step.getX() < getXBlock()) {
                    setDirection(DIRECTION_LEFT);
                } else if (step.getY() > getYBlock()) {
                    setDirection(DIRECTION_DOWN);
                } else if (step.getY() < getYBlock()) {
                    setDirection(DIRECTION_UP);
                }

                move(getDirection(), delta, isBlocked(blocked, getDirection(), delta));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            randomMove(blocked, delta);
        }
    }
    public void follow(AABB[][] blocked, int delta){

        try {
            int tx = (Integer)context.get(ZeldaGameTest.CONTEXT_PLAYER_X_BLOCK);
            int ty = (Integer)context.get(ZeldaGameTest.CONTEXT_PLAYER_Y_BLOCK);
            int tileSize = (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE);

            if (!isBlocked(blocked, getDirection(), delta)) {

                Path path = pathFinder.findPath(this, getXBlock(), getYBlock(), tx, ty);
                Path.Step currentStep = path.getStep(0);
                Path.Step nextStep = path.getStep(1);
                AABB currentTileAABB = AABBTiledMap.getTileAABB(currentStep.getX(), currentStep.getY(), tileSize);

                if (currentTileAABB.contains(getAABB()) && nextStep.getX() > getXBlock()) {
                    setDirection(DIRECTION_RIGHT);
                } else if (currentTileAABB.contains(getAABB()) && nextStep.getX() < getXBlock()) {
                    setDirection(DIRECTION_LEFT);
                } else if (currentTileAABB.contains(getAABB()) && nextStep.getY() > getYBlock()) {
                    setDirection(DIRECTION_DOWN);
                } else if (currentTileAABB.contains(getAABB()) && nextStep.getY() < getYBlock()) {
                    setDirection(DIRECTION_UP);
                }

                move(getDirection(), delta, isBlocked(blocked, getDirection(), delta));
            } else {

                Path path = pathFinder.findPath(this, getXBlock(), getYBlock(), tx, ty);
                Path.Step step = path.getStep(1);

                if (step.getX() > getXBlock()) {
                    setDirection(DIRECTION_RIGHT);
                } else if (step.getX() < getXBlock()) {
                    setDirection(DIRECTION_LEFT);
                } else if (step.getY() > getYBlock()) {
                    setDirection(DIRECTION_DOWN);
                } else if (step.getY() < getYBlock()) {
                    setDirection(DIRECTION_UP);
                }

                move(getDirection(), delta, isBlocked(blocked, getDirection(), delta));
            }
        } catch(IllegalFormatCodePointException e){
            System.out.println(e.getMessage());
            //e.printStackTrace();
            randomMove(blocked, delta);
        }
    }

    @Override
    public float getSpeed(){
        float speed = super.getSpeed();
        Map properties = getCurrentTileProperties();
        if(properties != null && "true".equals(getCurrentTileProperties().get(ZeldaGameTest.TILE_SLOW))){
            speed /= 1;
        }
        return speed;
    }
    public int getDefaultMovement() {
        return defaultMovement;
    }
    public void setDefaultMovement(int defaultMovement) {
        this.defaultMovement = defaultMovement;
    }
}
