package com.bomb.jparrott.object;

import com.bomb.jparrott.animation.AnimationFactory;
import com.bomb.jparrott.game.GameContext;
import com.bomb.jparrott.map.Tile;
import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by jparrott on 11/12/2015.
 */
public class Player extends Character{

    public final static int DEFAULT_RENDERABLE_LAYER = 2;
    public final static int DEFAULT_BOMB_COUNT = 0;
    public final static int DEFAULT_LIVES_COUNT = 3;

    private int bombCount;
    private int coinCount;
    private int lives;
    private boolean dead;

    /**
     * Primary Constructor
     *
     * @param gameContext
     * @param spriteSheet
     * @param xBlock
     * @param yBlock
     * @param width
     * @param height
     * @param speed
     * @param killZoneWidth
     * @param killZoneHeight
     */
    public Player(GameContext gameContext, int xBlock, int yBlock, int width, int height, float speed, int killZoneWidth, int killZoneHeight) throws SlickException{
        super(gameContext, xBlock, yBlock, width, height, speed, killZoneWidth, killZoneHeight);
        this.renderableLayer = DEFAULT_RENDERABLE_LAYER;
        this.bombCount = DEFAULT_BOMB_COUNT;
        lives = DEFAULT_LIVES_COUNT;
    }

    /**
     *
     * @param gameContext
     * @param spriteSheet
     * @param xBlock
     * @param yBlock
     * @param width
     * @param height
     * @param speed
     */
    public Player(GameContext gameContext, int xBlock, int yBlock, int width, int height, float speed) throws SlickException{
        this(gameContext, xBlock, yBlock, width, height, speed, width, height);
    }

    /**
     * constructor with default initializers
     *
     * @param gameContext
     */
    public Player(GameContext gameContext, int xBlock, int yBlock) throws SlickException{
        this(gameContext, xBlock, yBlock, 24, 24, 0.15f, 24, 24);
    }

    /**
     * Create a Bomb object with centerX and cetnerY equalling that of player
     */
    public void dropBomb(){
        if(bombCount > 0){
            Bomb bomb;
            try{
                bomb = new Bomb(gameContext, x, y);
                gameContext.add(bomb);
                bombCount--;
            }catch (SlickException se){
                System.out.println("Caught exception while trying to create Bomb");
                se.printStackTrace();
            }
        }
    }

    public void die(){
        if(!dead){
            dead = true;
            try {
                Audio sound = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sounds/die.wav"));
                sound.playAsSoundEffect(1.0f, 1.0f, false);
            } catch (IOException io) {
                io.printStackTrace();
            }

            setRenderableLayer(0);
            currentAnimation = animationMap.get(AnimationFactory.DEAD);
        }
    }

    public void revive(){
        dead = false;
        lives--;
        setRenderableLayer(DEFAULT_RENDERABLE_LAYER);
        currentAnimation = animationMap.get(AnimationFactory.DEFAULT);
    }

    public void collect(PowerUp powerUp){
        if(powerUp instanceof BombPowerUp){
            bombCount++;
        }
        if(powerUp instanceof CoinPowerUp){
            coinCount++;
        }
        powerUp.setDestroyed(true);
    }

    @Override
    public void update(int delta){

        if(!dead) {
            for (PowerUp powerUp : gameContext.getPowerUps()) {
                if (getAABB().overlaps(powerUp.getAABB())) {
                    collect(powerUp);
                }
            }

            Input input = gameContext.getInput();

            //debug
            if (input.isKeyPressed(Input.KEY_TAB)) {
                setCenterX((getXBlock() * 32) + 16);
                setCenterY((getYBlock() * 32) + 16);
            }

            //calculate movement
            Direction direction;
            if (input.isKeyDown(Input.KEY_UP) || input.isKeyDown(Input.KEY_K)) {
                direction = Direction.NORTH;
                move(direction, delta);
            } else if (input.isKeyDown(Input.KEY_DOWN) || input.isKeyDown((Input.KEY_J))) {
                direction = Direction.SOUTH;
                move(direction, delta);
            } else if (input.isKeyDown(Input.KEY_LEFT) || input.isKeyDown(Input.KEY_H)) {
                direction = Direction.WEST;
                move(direction, delta);
            } else if (input.isKeyDown(Input.KEY_RIGHT) || input.isKeyDown(Input.KEY_L)) {
                direction = Direction.EAST;
                move(direction, delta);
            }

            //drop bomb if user presses space and there is not another bomb on screen
            Set<GameObject> gameObjects = gameContext.getGameObjects();
            if (input.isKeyPressed(Input.KEY_SPACE)) {
                //check if there is already a bomb on the screen
                boolean bombOnScreen = false;
                for (GameObject gameObject : gameObjects) {
                    if (gameObject instanceof Bomb) {
                        bombOnScreen = true;
                        break;
                    }
                }
                //only drop bomb if there is not already a bomb on screen
                if (!bombOnScreen) {
                    dropBomb();
                }
            }

            //check if player is colliding with hazards
            Set<Hazard> hazards = gameContext.getHazards();
            AABB playerKillzone = getKillZone();
            for (Hazard hazard : hazards) {
                if (hazard.isSafe()) {
                    continue;
                }
                if (playerKillzone.overlaps(hazard.getKillZone())) {
                    die();
                    break;
                }
            }

        }//end if dead check

    }

    @Override
    public float getSpeed(){
        float speed = super.getSpeed();
        Map tileAttributes = getCurrentTileAttributes();
        if(tileAttributes != null && "true".equals(tileAttributes.get(Tile.SLOW))){
            speed /= 4;
        }
        return speed;
    }

    @Override
    public boolean isBlocked(float x, float y) {
        boolean isBlocked = false;
        AABB newAABB = getTestAABB(x, y);
        Set<Blockable> blockables = gameContext.getBlockables();
        for(Blockable blockable : blockables){
            if(blockable.isImmaterial()){
                continue;
            }
            AABB blockableAABB = blockable.getAABB();
            if(newAABB.overlaps(blockableAABB) && this != blockable && !getAABB().overlaps(blockableAABB) && !(blockable instanceof Hazard)){
                isBlocked= true;
                break;
            }
        }
        return isBlocked;
    }

    @Override
    public void draw() {
        currentAnimation.draw(x, y);
    }

    public int getBombCount() {
        return bombCount;
    }

    public void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(Integer lives) {
        this.lives = lives;
    }

    public void decreaseLives(){
        this.lives--;
    }

    public boolean isDead(){
        return dead;
    }

    public void setDead(boolean dead){
        this.dead = dead;
    }
}
