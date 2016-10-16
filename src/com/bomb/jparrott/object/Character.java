package com.bomb.jparrott.object;

import com.bomb.jparrott.animation.Animatable;
import com.bomb.jparrott.animation.AnimationFactory;
import com.bomb.jparrott.game.GameContext;
import com.bomb.jparrott.map.Tile;
import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.pathfinding.Mover;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by jparrott on 11/11/2015.
 */
public abstract class Character extends GameObject implements Mover, Movable, Animatable{

    protected int killZoneWidth;
    protected int killZoneHeight;
    protected Direction direction;
    protected float speed;
    protected Map<String, Animation> animationMap;
    protected Animation currentAnimation;
    protected SpriteSheet spriteSheet;

    protected Character(int xBlock, int yBlock, int width, int height, float speed) throws SlickException {
        this(xBlock, yBlock, width, height, speed, width, height);
    }

    protected Character(int xBlock, int yBlock, int width, int height, float speed, int killZoneWidth, int killZoneHeight) throws SlickException{
        super(0, 0, width, height);

        //set the x,y coordinates by block/tile instead of by pixel location.  NOTE we have passed 0,0 in to GameObject constructor
        this.setXBlock((int) xBlock);
        this.setYBlock((int) yBlock);

        this.speed = speed;
        this.killZoneWidth = killZoneWidth;
        this.killZoneHeight = killZoneHeight;
        this.direction = Direction.EAST;

        this.animationMap = AnimationFactory.getAnimationMap(this);
        this.currentAnimation = animationMap.get(AnimationFactory.EAST);
    }

    /**
     * If the character is on a tile with property Tile.PORTAL set, parse the attribute and move the character to the respective
     * x,y block.  The tile attribute should be in the form "<Direction>,<xBlockLocation>,<yBlockLocation." If the attribute
     * cannot be properly parsed, the character will not be moved.
     *
     * @return true if the user was teleported
     */
    public boolean teleport(){
        boolean isTeleported = false;
        Map<String, Object> tileAttributes = getCurrentTileAttributes();
        String portal = (String)tileAttributes.get(Tile.PORTAL);

        if(portal != null){

            try{
                String[] portalArray = portal.split(",");
                Direction portalDirection = Direction.valueOf(portalArray[0]);
                int portalX = Integer.valueOf(portalArray[1]);
                int portalY = Integer.valueOf(portalArray[2]);

                if(direction.equals(portalDirection)){
                    setXBlock(portalX);
                    setYBlock(portalY);
                    isTeleported = true;
                }

            }catch (ArrayIndexOutOfBoundsException aioob){
                System.out.println("unable to parse tile attribute PORTAL");
                aioob.printStackTrace();

            }catch (IllegalArgumentException iae){
                System.out.println("unable to parse tile attribute PORTAL");
                iae.printStackTrace();
            }

        }

        return isTeleported;
    }

    /**
     * move Character unless it would collide with tlie at xBlockLimit, yBlockLimit
     */
    public void move(Direction direction, int delta, int xBlockLimit, int yBlockLimit){
        float newX = x;
        float newY = y;

        switch (direction){
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
        AABB testAABB = getTestAABB(newX, newY);
        AABB tileAABB = gameContext.getMap().getTileAABB(xBlockLimit, yBlockLimit);
        if(testAABB.overlaps(tileAABB)){
            return;
        }else{
            move(direction, delta);
        }

    }

    @Override
    public void move(Direction direction, int delta) {

        //if on a tile with Tile.PORTAL set, move to the appropriate location
        boolean isTeleported = teleport();

        if(!isTeleported){
            float newX = x;
            float newY = y;

            this.direction = direction;
            switch (direction){
                case NORTH:
                    currentAnimation = animationMap.get(AnimationFactory.NORTH);
                    newY -= (getSpeed() * delta);
                    break;
                case SOUTH:
                    currentAnimation = animationMap.get(AnimationFactory.SOUTH);
                    newY += (getSpeed() * delta);
                    break;
                case WEST:
                    currentAnimation = animationMap.get(AnimationFactory.WEST);
                    newX -= (getSpeed() * delta);
                    break;
                case EAST:
                    currentAnimation = animationMap.get(AnimationFactory.EAST);
                    newX += (getSpeed() * delta);
                    break;
            }


            /*
             * this is messy code that fixes a bug with enemies moving off the screen.  eventually everything with
             * movement methods amont Character, Enemy and Player need to be rearchitected, but this will do for now
             */

            if(this instanceof Player){
                if(!isBlocked(newX, newY) || isBlocked(x, y)){
                    setX(newX);
                    setY(newY);
                }
            }else{
                Set<Blockable> blockables = getCollidingBlockables(this, getTestAABB(x, y));
                Set<Blockable> newBlockables = getCollidingBlockables(this, getTestAABB(newX, newY));
                if(newBlockables.isEmpty() || blockables.containsAll(newBlockables)){
                    setX(newX);
                    setY(newY);
                }
            }
        }

        currentAnimation.update(delta);

    }

    public Set<Blockable> getCollidingBlockables(Movable movable, AABB aabb){
        Set<Blockable> returnBlockables = new HashSet<>();
        for(Blockable blockable : gameContext.getBlockables()){
            if(blockable.isImmaterial()){
                continue;
            }
            AABB blockableAABB = blockable.getAABB();
            if(aabb.overlaps(blockableAABB) && movable != blockable){
                returnBlockables.add(blockable);
            }
        }
        return returnBlockables;
    }

    @Override
    public AABB getAABB(){
        AABB aabb = new AABB(getCenterX() - (width / 2), getCenterY() - (height / 2), getCenterX() + (width / 2), getCenterY() + (height / 2));
        return aabb;
    }

    @Override
    public AABB getTestAABB(float x, float y){
        AABB aabb;
        float minX = x, minY = y, maxX = x + width, maxY = y + height;
        aabb = new AABB(minX, minY, maxX, maxY);
        return aabb;
    }

    @Override
    public Map<String, Animation> getAnimationMap(){
        return animationMap;
    }

    @Override
    public void setAnimationMap(Map<String, Animation> animationMap){
        this.animationMap = animationMap;
    }

    public Animation getCurrentAnimation(){

        Animation currentAnimation = null;
        switch (direction){
            case NORTH:
                currentAnimation = animationMap.get(AnimationFactory.NORTH);
                break;
            case SOUTH:
                currentAnimation = animationMap.get(AnimationFactory.SOUTH);
                break;
            case WEST:
                currentAnimation = animationMap.get(AnimationFactory.WEST);
                break;
            case EAST:
                currentAnimation = animationMap.get(AnimationFactory.EAST);
                break;
            default :
                throw new UnsupportedOperationException("Found unsupported case for direction " + direction);
        }
        return currentAnimation;
    }

    public AABB getKillZone(){
        float centerX = getCenterX();
        float centerY = getCenterY();
        double minX = centerX - (killZoneWidth / 2);
        double minY = centerY - (killZoneHeight / 2);
        double maxX = centerX + (killZoneWidth / 2);
        double maxY = centerY + (killZoneHeight / 2);
        AABB aabb = new AABB(minX, minY, maxX, maxY);
        return aabb;
    }
    public int getKillZoneWidth() {
        return killZoneWidth;
    }
    public int getKillZoneHeight() {
        return killZoneHeight;
    }
    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
