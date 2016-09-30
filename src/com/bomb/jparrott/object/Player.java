package com.bomb.jparrott.object;

import com.bomb.jparrott.animation.AnimationFactory;
import com.bomb.jparrott.game.GameInput;
import com.bomb.jparrott.game.SoundManager;
import com.bomb.jparrott.map.GameMap;
import com.bomb.jparrott.map.Tile;
import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
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
    public final static int LIFE_GAIN_THRESHOLD = 20000;

    private int bombCount;
    private int coinCount;
    private int score;
    private int lives;
    private boolean dead;
    private volatile GameInput gameInput;

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
    public Player(int xBlock, int yBlock, int width, int height, float speed, int killZoneWidth, int killZoneHeight) throws SlickException{
        super(xBlock, yBlock, width, height, speed, killZoneWidth, killZoneHeight);
        this.renderableLayer = DEFAULT_RENDERABLE_LAYER;
        this.bombCount = DEFAULT_BOMB_COUNT;
        lives = DEFAULT_LIVES_COUNT;
        gameInput = GameInput.getInstance();
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
    public Player(int xBlock, int yBlock, int width, int height, float speed) throws SlickException{
        this(xBlock, yBlock, width, height, speed, width, height);
    }

    /**
     * constructor with default initializers
     *
     * @param gameContext
     */
    public Player(int xBlock, int yBlock) throws SlickException{
        this(xBlock, yBlock, 28, 28, 0.15f, 24, 24);
    }

    /**
     * Create a Bomb object with centerX and cetnerY equalling that of player
     */
    public void dropBomb(){
        if(bombCount > 0){
            Bomb bomb;
            try{
                bomb = new Bomb(x, y);
                gameContext.add(bomb);
                bombCount--;
                SoundManager.play("drop_bomb");
            }catch (SlickException se){
                System.out.println("Caught exception while trying to create Bomb");
                se.printStackTrace();
            }
        }
    }

    public void die(){
        if(!dead){
            dead = true;
            SoundManager.play("die");

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
            addScore(50);
        }
        if(powerUp instanceof CoinPowerUp){
            coinCount++;
            addScore(10);
        }

        powerUp.setDestroyed(true);
    }

    /**
     * movement assist to help player navigate through the tiles without bumping into corners.
     * returns true if the player was moved by this method
     *
     * @param direction
     * @param delta
     * @return
     */
    public boolean movementAssist(Direction direction, int delta){

        boolean isAssisted = false;

        //enclosing everything in a try-catch, as we could conceivably run into an ArrayIndexOutOfBoundsException
        try{
            float newX = x;
            float newY = y;

            int tileWidth = gameContext.getTileWidth();
            int tileHeight = gameContext.getTileHeight();
            float tileCenterX = ((getXBlock() * tileWidth) + (tileWidth / 2));
            float tileCenterY = ((getYBlock() * tileHeight) + (tileHeight / 2));
            float centerX = getCenterX();
            float centerY = getCenterY();
            int xBlock = getXBlock();
            int yBlock = getYBlock();
            GameMap map = gameContext.getMap();

            this.direction = direction;
            switch (direction){
                case NORTH:
                    newY -= (getSpeed() * delta);

                    //if the player is blocked by an obstacle
                    if(isBlocked(newX, newY)){

                        //check if the path NORTH is clear
                        if(!map.isBlocked(xBlock, yBlock - 1)){

                            //if blocked on right corner, move left
                            if(tileCenterX < centerX){
                                isAssisted = true;
                                move(Direction.WEST, delta, xBlock - 1, yBlock);
                            }
                            //else blocked on left corner, move right
                            else{
                                isAssisted = true;
                                move(Direction.EAST, delta, xBlock + 1, yBlock);
                            }
                        }

                        //else check if path NORTH-WEST is clear
                        else if(tileCenterX > centerX) {
                            if(!map.isBlocked(xBlock - 1, yBlock) && !map.isBlocked(xBlock - 1, yBlock - 1)){
                                isAssisted = true;
                                move(Direction.WEST, delta, xBlock - 2, yBlock);
                            }
                        }
                        //else check if path NORTH-EAST is clear
                        else if(tileCenterX < centerX) {
                            if(!map.isBlocked(xBlock + 1, yBlock) && !map.isBlocked(xBlock + 1, yBlock - 1)){
                                isAssisted = true;
                                move(Direction.EAST, delta, xBlock + 2, yBlock);
                            }
                        }
                    }
                    break;
                case SOUTH:
                    newY += (getSpeed() * delta);
                    if(isBlocked(newX, newY)){
                        if(!map.isBlocked(xBlock, yBlock + 1)){
                            if(tileCenterX < centerX){
                                isAssisted = true;
                                move(Direction.WEST, delta, xBlock - 1, yBlock);
                            }
                            else{
                                isAssisted = true;
                                move(Direction.EAST, delta, xBlock + 1, yBlock);
                            }
                        }
                        else if(tileCenterX > centerX) {
                            if(!map.isBlocked(xBlock - 1, yBlock) && !map.isBlocked(xBlock - 1, yBlock + 1)){
                                isAssisted = true;
                                move(Direction.WEST, delta, xBlock - 2, yBlock);
                            }
                        }
                        else if(tileCenterX < centerX) {
                            if(!map.isBlocked(xBlock + 1, yBlock) && !map.isBlocked(xBlock + 1, yBlock + 1)){
                                isAssisted = true;
                                move(Direction.EAST, delta, xBlock + 2, yBlock);
                            }
                        }
                    }
                    break;
                case WEST:
                    newX -= (getSpeed() * delta);
                    if(isBlocked(newX, newY)){
                        if(!map.isBlocked(xBlock - 1, yBlock)){
                            if(tileCenterY < centerY){
                                isAssisted = true;
                                move(Direction.NORTH, delta, xBlock, yBlock - 1);
                            }
                            else{
                                isAssisted = true;
                                move(Direction.SOUTH, delta, xBlock, yBlock + 1);
                            }
                        }
                        else if(tileCenterY > centerY) {
                            if(!map.isBlocked(xBlock, yBlock - 1) && !map.isBlocked(xBlock - 1, yBlock - 1)){
                                isAssisted = true;
                                move(Direction.NORTH, delta, xBlock, yBlock - 2);
                            }
                        }
                        else if(tileCenterY < centerY) {
                            if(!map.isBlocked(xBlock, yBlock + 1) && !map.isBlocked(xBlock - 1, yBlock + 1)){
                                isAssisted = true;
                                move(Direction.SOUTH, delta, xBlock, yBlock + 2);
                            }
                        }
                    }
                    break;
                case EAST:
                    newX += (getSpeed() * delta);
                    if(isBlocked(newX, newY)){
                        if(!map.isBlocked(xBlock + 1, yBlock)){
                            if(tileCenterY < centerY){
                                isAssisted = true;
                                move(Direction.NORTH, delta, xBlock, yBlock - 1);
                            }
                            else{
                                isAssisted = true;
                                move(Direction.SOUTH, delta, xBlock, yBlock + 1);
                            }
                        }
                        else if(tileCenterY > centerY) {
                            if(!map.isBlocked(xBlock, yBlock - 1) && !map.isBlocked(xBlock + 1, yBlock - 1)){
                                isAssisted = true;
                                move(Direction.NORTH, delta, xBlock, yBlock - 2);
                            }
                        }
                        else if(tileCenterY < centerY) {
                            if(!map.isBlocked(xBlock, yBlock + 1) && !map.isBlocked(xBlock + 1, yBlock + 1)){
                                isAssisted = true;
                                move(Direction.SOUTH, delta, xBlock, yBlock + 2);
                            }
                        }
                    }
                    break;
            }
        }catch (ArrayIndexOutOfBoundsException ex){
            log.warn(ex);
        }

        return isAssisted;
    }

    @Override
    public void move(Direction direction, int delta){
        if(!movementAssist(direction, delta)){
            super.move(direction, delta);
        }
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
            if (gameInput.isDown(GameInput.Button.UP)) {
                direction = Direction.NORTH;
                move(direction, delta);
            } else if (gameInput.isDown(GameInput.Button.DOWN)) {
                direction = Direction.SOUTH;
                move(direction, delta);
            } else if (gameInput.isDown(GameInput.Button.LEFT)) {
                direction = Direction.WEST;
                move(direction, delta);
            } else if (gameInput.isDown(GameInput.Button.RIGHT)) {
                direction = Direction.EAST;
                move(direction, delta);
            }

            //drop bomb if user presses space and there is not another bomb on screen
            Set<GameObject> gameObjects = gameContext.getGameObjects();
            if (gameInput.isDown(GameInput.Button.B)) {
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

    public int getScore(){
        return score;
    }
    public void addScore(int points){
        int previousScore = score;
        score += points;

        //gain life for getting over 5000
        if(previousScore < 5000 && score >= 5000){
            addLife();
        }

        int livesGained = ((int)score/LIFE_GAIN_THRESHOLD) - ((int)previousScore/LIFE_GAIN_THRESHOLD);
        for(; livesGained > 0; livesGained--){
            addLife();
        }
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
    public void addLife(){

        SoundManager.play(("gain_life"));

        lives++;
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
}
