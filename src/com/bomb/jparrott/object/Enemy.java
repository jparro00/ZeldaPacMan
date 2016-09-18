package com.bomb.jparrott.object;

import com.bomb.jparrott.animation.AnimationFactory;
import com.bomb.jparrott.game.GameContext;
import com.bomb.jparrott.map.GameMap;
import com.bomb.jparrott.map.Tile;
import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Color;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFinder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by jparrott on 11/13/2015.
 */
public class Enemy extends Character implements Hazard, Blockable {

    private final static int DEFAULT_TIME_DEAD = 12000;
    private final static int DEFAULT_TIME_VULNERABLE = 4000;

    private Movement defaultMovement;
    private PathFinder pathFinder;
    private boolean dead;
    private boolean vulnerable;
    private boolean immaterial;
    private boolean safe;
    private int timeUntilLiving;

    /**
     * primary constructor
     *
     * @param gameContext
     * @param pathFinder
     * @param spriteSheet
     * @param xBlock
     * @param yBlock
     * @param width
     * @param height
     * @param speed
     * @throws SlickException
     */
    public Enemy(PathFinder pathFinder, int xBlock, int yBlock, int width, int height, float speed) throws SlickException {
        super(xBlock, yBlock, width, height, speed);
        GameMap gameMap = gameContext.getMap();
        this.pathFinder = pathFinder;
        defaultMovement = Movement.RANDOM;
    }

    /**
     * constructor with default initializers
     *
     * @param gameContext
     * @throws SlickException
     */
    public Enemy(int xBlock, int yBlock) throws SlickException {
        this(new AStarPathFinder(GameContext.getInstance().getMap(), 8, false), xBlock, yBlock, 30, 30, 0.1f);
    }

    public Enemy(int xBlock, int yBlock, Movement defaultMovement) throws SlickException {
        this(xBlock, yBlock);
        this.setDefaultMovement(defaultMovement);
    }

    public void defaultMove(int delta) {
        switch (defaultMovement) {
            case RANDOM:
                randomMove(delta);
                break;
            case HORIZONTAL:
                horizontalMove(delta);
                break;
            case VERTICAL:
                verticalMove(delta);
                break;
            case FOLLOW:
                follow(delta);
                break;
            default:
                randomMove(delta);
                break;
        }
    }

    public void randomMove(int delta) {

        if (!isBlocked(getDirection(), delta)) {
            move(getDirection(), delta);
        } else {
            int randDirection = new Random().nextInt(4);
            move(Direction.values()[randDirection], delta);
        }
    }

    public void horizontalMove(int delta) {
        Direction currentDirection = getDirection();

        if (!isBlocked(currentDirection, delta)) {
            move(currentDirection, delta);
        } else {
            currentDirection = currentDirection.equals(Direction.WEST) ? Direction.EAST : Direction.WEST;
            if (!isBlocked(currentDirection, delta)) {
                move(currentDirection, delta);
            }
        }
    }

    public void verticalMove(int delta) {
        Direction currentDirection = getDirection();

        if (!isBlocked(currentDirection, delta)) {
            move(currentDirection, delta);
        } else {
            currentDirection = currentDirection.equals(Direction.NORTH) ? Direction.SOUTH : Direction.NORTH;
            if (!isBlocked(currentDirection, delta)) {
                move(currentDirection, delta);
            }
        }
    }

    public void follow(int delta) {

        try {
            boolean playerIsDead = gameContext.getPlayer().isDead();
            if (playerIsDead) {
                randomMove(delta);
                return;
            }

            int tx = gameContext.getPlayerXBlock();
            int ty = gameContext.getPlayerYBlock();
            int tileHeight = gameContext.getTileHeight();
            int tileWidth = gameContext.getTileWidth();

            if (!isBlocked(getDirection(), delta)) {

                Path path = pathFinder.findPath(this, getXBlock(), getYBlock(), tx, ty);
                Path.Step currentStep = path.getStep(0);
                Path.Step nextStep = path.getStep(1);
                AABB currentTileAABB = gameContext.getMap().getTileAABB(currentStep.getX(), currentStep.getY());

                if (currentTileAABB.contains(getAABB()) && nextStep.getX() > getXBlock()) {
                    setDirection(Direction.EAST);
                } else if (currentTileAABB.contains(getAABB()) && nextStep.getX() < getXBlock()) {
                    setDirection(Direction.WEST);
                } else if (currentTileAABB.contains(getAABB()) && nextStep.getY() > getYBlock()) {
                    setDirection(Direction.SOUTH);
                } else if (currentTileAABB.contains(getAABB()) && nextStep.getY() < getYBlock()) {
                    setDirection(Direction.NORTH);
                }

                move(getDirection(), delta);
            } else {

                Path path = pathFinder.findPath(this, getXBlock(), getYBlock(), tx, ty);
                Path.Step step = path.getStep(1);

                if (step.getX() > getXBlock()) {
                    setDirection(Direction.EAST);
                } else if (step.getX() < getXBlock()) {
                    setDirection(Direction.WEST);
                } else if (step.getY() > getYBlock()) {
                    setDirection(Direction.SOUTH);
                } else if (step.getY() < getYBlock()) {
                    setDirection(Direction.NORTH);
                }

                move(getDirection(), delta);
            }
        } catch (Exception e) {
            randomMove(delta);
        }
    }

    public void becomeVulnerable(){
        dead = false;
        immaterial = true;
        safe = true;
        vulnerable = true;
        timeUntilLiving = DEFAULT_TIME_VULNERABLE;
        try{
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("vulnerable", true);
            animationMap = AnimationFactory.getAnimationMap(this, options);
        }catch (SlickException se){
            se.printStackTrace();
        }
    }

    public void die(){
        dead = true;
        immaterial = true;
        safe = true;
        vulnerable = false;
        timeUntilLiving = DEFAULT_TIME_DEAD;
        setSpeed(.15f);
        try{
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("dead", true);
            animationMap = AnimationFactory.getAnimationMap(this, options);
        }catch (SlickException se){
            se.printStackTrace();
        }
    }

    public void revive() {
        dead = false;
        immaterial = false;
        safe = false;
        vulnerable = false;
        setSpeed(.1f);
        try{
            animationMap = AnimationFactory.getAnimationMap(this);
        }catch (SlickException se){
            se.printStackTrace();
        }
    }

    @Override
    public float getSpeed() {
        float speed = super.getSpeed();
        Map tileAttributes = getCurrentTileAttributes();
        if (tileAttributes != null && "true".equals(tileAttributes.get(Tile.SLOW))) {
            speed /= 1;
        }
        return speed;
    }

    public Movement getDefaultMovement() {
        return defaultMovement;
    }

    public void setDefaultMovement(Movement defaultMovement) {
        this.defaultMovement = defaultMovement;
    }

    @Override
    public void update(int delta) {
        //calculate whether dead or vulnerable and move appropriately
        if(vulnerable){
            if (timeUntilLiving <= 0) {
                revive();
            } else {
                timeUntilLiving -= delta;
                //if dead, do not follow player
                if(Movement.FOLLOW.equals(defaultMovement)){
                    randomMove(delta);
                }
                else{
                    defaultMove(delta);
                }
            }

        } else if(dead){
            if (timeUntilLiving <= 0) {
                revive();
            } else {
                timeUntilLiving -= delta;
                //if dead, do not follow player
                if(Movement.FOLLOW.equals(defaultMovement)){
                    randomMove(delta);
                }
                else{
                    defaultMove(delta);
                }
            }

        } else {
            defaultMove(delta);
        }

        //check if enemy is colliding with any hazards
        if(!dead){
            Set<Hazard> hazards = gameContext.getHazards();
            AABB enemyKillZone = getKillZone();
            for(Hazard hazard : hazards){
                if(hazard.isSafe()){
                    continue;
                }
                if(enemyKillZone.overlaps(hazard.getKillZone()) && !hazard.getClass().equals(this.getClass())){
                    becomeVulnerable();
                    break;
                }
            }
        }

        //check if enemy is colliding with player while vulnerable
        if(vulnerable){
            AABB enemyKillZone = getKillZone();
            AABB playerKillZone = gameContext.getPlayer().getKillZone();
            if(enemyKillZone.overlaps(playerKillZone) && !gameContext.getPlayer().isDead()){
                die();
            }

        }
    }

    public boolean isBlocked(Direction direction, int delta) {
        boolean isBlocked = false;
        float newX = x;
        float newY = y;

        this.direction = direction;
        switch (direction) {
            case NORTH:
                newY -= (getSpeed() * delta);
                break;
            case SOUTH:
                newY += (getSpeed() * delta);
                break;
            case WEST:
                newX -= (getSpeed() * delta);
                break;
            case EAST:
                newX += (getSpeed() * delta);
                break;
        }

        AABB newAABB = getTestAABB(newX, newY);
        Set<Blockable> blockables = gameContext.getBlockables();

        for (Blockable blockable : blockables) {
            if(blockable.isImmaterial()){
                continue;
            }
            AABB blockableAABB = blockable.getAABB();
            if (newAABB.overlaps(blockableAABB) && this != blockable && !getAABB().overlaps(blockableAABB)) {
                isBlocked = true;
                break;
            }
        }

        return isBlocked;

    }

    @Override
    public boolean isBlocked(float x, float y) {
        boolean isBlocked = false;

        //check if character has moved off screnn
        int mapWidth = gameContext.getMap().getWidthInPixels();
        int mapHeight = gameContext.getMap().getHeightInPixels();
        if(x >= mapWidth || x < 0 || y >= mapHeight || y < 0){
            isBlocked = true;
        }
        //else check if colliding with any blockables
        else{
            AABB newAABB = getTestAABB(x, y);
            isBlocked = gameContext.isCollidingWithBlockables(this, newAABB);
        }

        return isBlocked;
    }

    @Override
    public void draw() {
        if (dead) {
            currentAnimation.draw(x, y, new Color(1, 1, 1, 0.5f));
        } else {
            currentAnimation.draw(x, y);
        }
    }

    @Override
    public boolean isSafe() {
        return safe;
    }

    @Override
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Override
    public boolean isImmaterial() {
        return immaterial;
    }

    @Override
    public void setImmaterial(boolean immaterial) {
        this.immaterial = immaterial;
    }
}
