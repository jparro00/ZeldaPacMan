package com.bomb.jparrott.test;

import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Renderable;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.pathfinding.Mover;

import java.util.Map;
import java.util.Properties;

/**
 * Created by jparrott on 10/26/2015.
 */
public class Collider implements Mover, Renderable{
    public final static int DIRECTION_UP = 0;
    public final static int DIRECTION_DOWN = 1;
    public final static int DIRECTION_LEFT = 2;
    public final static int DIRECTION_RIGHT = 3;

    private final static Color COLOR_TRANSPARENT = new Color(255, 0, 255);

    protected volatile Map context;
    protected float x, y;
    protected int size;
    protected int aabbSize;
    protected float speed;
    protected int direction;
    protected Map currentTileProperties;
    protected SpriteSheet spriteSheet;
    protected Animation sprite, upAnimation, downAnimation, leftAnimation, rightAnimation;
    private int[] uFrames;
    private int[] dFrames;
    private int[] lFrames;
    private int[] rFrames;
    private int duration;
    private int[] durations;


    public Collider(Map context, SpriteSheet spriteSheet, int spriteSize, int duration, int boundingBoxSize, float x, float y, float speed) throws SlickException {
        this.context = context;
        this.x = x;
        this.y = y;
        this.size = spriteSize;
        this.aabbSize = boundingBoxSize;
        this.speed = speed;
        this.direction = DIRECTION_RIGHT;
        this.currentTileProperties = new Properties();
        this.spriteSheet = spriteSheet;

        int horizontalCount = spriteSheet.getHorizontalCount();
        int lastIndex = horizontalCount - 1;
        this.uFrames = new int[] {0,DIRECTION_UP, lastIndex,DIRECTION_UP};
        this.dFrames = new int[] {0,DIRECTION_DOWN, lastIndex,DIRECTION_DOWN};
        this.lFrames = new int[] {0, DIRECTION_LEFT, lastIndex, DIRECTION_LEFT};
        this.rFrames = new int[] {0, DIRECTION_RIGHT, lastIndex, DIRECTION_RIGHT};
        this.duration = duration;
        this.durations = new int[horizontalCount];
        for(int i = 0; i < horizontalCount; i++){
            durations[i] = duration;
        }

        this.upAnimation = new Animation(spriteSheet, uFrames, new int[] {150, 150});
        this.downAnimation = new Animation(spriteSheet, dFrames, new int[] {150, 150});
        this.leftAnimation = new Animation(spriteSheet, lFrames, new int[] {150, 150});
        this.rightAnimation = new Animation(spriteSheet, rFrames, new int[] {150, 150});
        this.upAnimation.setAutoUpdate(false);
        this.downAnimation.setAutoUpdate(false);
        this.leftAnimation.setAutoUpdate(false);
        this.rightAnimation.setAutoUpdate(false);

        this.sprite = rightAnimation;
    }

    public void draw(){
        sprite.draw(getX(), getY());
    }

    @Override
    public void draw(float x, float y) {
        sprite.draw();
    }

    public void move(int direction, int delta, boolean isBlocked){

        this.direction = direction;
        switch (direction){
            case DIRECTION_UP :
                sprite = upAnimation;
                if("up".equals(getCurrentTileProperties().get(ZeldaGameTest.TILE_PORTAL))){
                    String[] goTo = getCurrentTileProperties().get(ZeldaGameTest.TILE_GO_TO).toString().split(",");
                    setX(Integer.valueOf(goTo[0]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                    setY(Integer.valueOf(goTo[1]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                }
                else if(!isBlocked)
                    setY(y - getSpeed() * delta);
                break;
            case DIRECTION_DOWN :
                sprite = downAnimation;
                if("down".equals(getCurrentTileProperties().get(ZeldaGameTest.TILE_PORTAL))){
                    String[] goTo = getCurrentTileProperties().get(ZeldaGameTest.TILE_GO_TO).toString().split(",");
                    setX(Integer.valueOf(goTo[0]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                    setY(Integer.valueOf(goTo[1]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                }
                else if(!isBlocked)
                    setY(y + getSpeed() * delta);
                break;
            case DIRECTION_LEFT :
                sprite = leftAnimation;
                if("left".equals(getCurrentTileProperties().get(ZeldaGameTest.TILE_PORTAL))){
                    String[] goTo = getCurrentTileProperties().get(ZeldaGameTest.TILE_GO_TO).toString().split(",");
                    setX(Integer.valueOf(goTo[0]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                    setY(Integer.valueOf(goTo[1]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                }
                else if(!isBlocked) {
                    setX(x - getSpeed() * delta);
                }
                break;
            case DIRECTION_RIGHT :
                sprite = rightAnimation;
                if("right".equals(getCurrentTileProperties().get(ZeldaGameTest.TILE_PORTAL))){
                    String[] goTo = getCurrentTileProperties().get(ZeldaGameTest.TILE_GO_TO).toString().split(",");
                    setX(Integer.valueOf(goTo[0]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                    setY(Integer.valueOf(goTo[1]) * (Integer)context.get(ZeldaGameTest.CONTEXT_TILE_SIZE));
                }
                else if(!isBlocked) {
                    setX(x + getSpeed() * delta);
                }
                break;
        }

        sprite.update(delta);
        draw(getX(), getY());
    }

    public AABB testMove(int direction, int delta){
        AABB aabb = getAABB();
        double minX = aabb.getMinX();
        double minY = aabb.getMinY();
        double maxX = aabb.getMaxX();
        double maxY = aabb.getMaxY();

        switch (direction){
            case DIRECTION_UP :
                minY -= getSpeed() * delta;
                maxY -= speed * delta;
                break;
            case DIRECTION_DOWN :
                minY += getSpeed() * delta;
                maxY += getSpeed() * delta;
                break;
            case DIRECTION_LEFT :
                minX -= getSpeed() * delta;
                maxX -= getSpeed() * delta;
                break;
            case DIRECTION_RIGHT :
                minX += getSpeed() * delta;
                maxX += getSpeed() * delta;
                break;
        }

        return new AABB(minX,minY,maxX,maxY);

    }

    public Map getCurrentTileProperties() {
        return currentTileProperties;
    }
    public void setCurrentTileProperties(Map currentTileProperties) {
        this.currentTileProperties = currentTileProperties;
    }
    public AABB getAABB(){
        float centerX = getCenterX();
        float centerY = getCenterY();
        double minX = centerX - (aabbSize / 2);
        double minY = centerY - (aabbSize / 2);
        double maxX = centerX + (aabbSize / 2);
        double maxY = centerY + (aabbSize / 2);
        AABB aabb = new AABB(minX, minY, maxX, maxY);
        return aabb;
    }

    public boolean isBlocked(AABB[][] blocked, int direction, int delta)
    {
        boolean isBlocked = false;

        AABB newSpriteAABB = testMove(direction, delta);
        AABB blockedAABB = null;

        outer: for (int xAxis=0;xAxis<blocked.length; xAxis++)
        {
            inner: for (int yAxis=0;yAxis<blocked[xAxis].length; yAxis++)
            {
                blockedAABB = blocked[xAxis][yAxis];

                if(blockedAABB != null && blockedAABB.overlaps(newSpriteAABB)){
                    isBlocked = true;
                    break outer;
                }
            }
        }
        return isBlocked;
    }

    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public float getCenterX() {
        return x + (size / 2);
    }
    public float getCenterY() {
        return y + (size / 2);
    }
    public void setCenterX(float x){
        this.setX(x - (size / 2));
    }
    public void setCenterY(float y){
        this.setY(y - (size / 2));
    }
    public int getYBlock(){
        int yBlock = (int)getCenterY() / size;
        return yBlock;
    }
    public int getXBlock(){
        int xBlock = (int)getCenterX() / size;
        return xBlock;
    }
}
