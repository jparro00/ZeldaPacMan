package com.bomb.jparrott.game;

import com.bomb.jparrott.map.GameMap;
import com.bomb.jparrott.object.CoinPowerUp;
import com.bomb.jparrott.object.HighScore;
import com.bomb.jparrott.object.Player;
import com.bomb.jparrott.object.PowerUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.LogSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jparrott
 */
public class ZeldaPacMan extends BasicGame
{
    public static final String DIR_SAVE = "./data/sav/";
    public static final String TILE_SLOW = "slow";
    public static final String TILE_BLOCKED = "blocked";
    public static final String TILE_PORTAL = "portal";
    public static final String TILE_GO_TO = "goTo";
    public static final String CONTEXT_PLAYER_X_BLOCK = "playerXBlock";
    public static final String CONTEXT_PLAYER_Y_BLOCK = "playerYBlock";

    private static AppGameContainer app;

    private final Logger log;
    private volatile GameContext gameContext;
    private List<GameMap> maps;
    private Iterator<GameMap> mapIterator;
    private GameMap currentMap;
    private boolean musicOn;
    private boolean soundOn;
    private boolean fullscreen;
    private boolean paused;
    private volatile GameInput gameInput;

    public ZeldaPacMan(){
        super("Zelda PacMan");
        this.log = LogManager.getLogger(this.getClass());
    }

    public static void main(String [] arguments){
        try{
            System.setProperty("java.library.path", "lib");
            //System.setProperty("org.lwjgl.librarypath", new File("lib/natives/natives-windows").getAbsolutePath());
            Music music = new Music("data/sounds/Overworld.ogg");
            music.loop();
            org.newdawn.slick.util.Log.setLogSystem(new Log());

            app = new AppGameContainer(new ScalableGame(new ZeldaPacMan(),640,480));
            //app.setDisplayMode(640, 480, false);
            app.setDisplayMode(1920, 1080, true);
            app.setShowFPS(false);
            app.setTargetFrameRate(60);
            app.start();

        }
        catch (SlickException e){
            e.printStackTrace();
        } finally {
            if(app != null){
                app.destroy();
            }
        }
    }

    @Override
    public boolean closeRequested(){

        //Save player object for the next time the player joins
        try(
                FileOutputStream fout = new FileOutputStream(DIR_SAVE + "score.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fout);
        ){
            HighScore highScore = HighScore.getInstance();
            oos.writeObject(highScore.getScore());
        }catch (IOException io){
            log.error(io);
        }

        //Save player object for the next time the player joins
        try(
                FileOutputStream fout = new FileOutputStream(DIR_SAVE + "input_config.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fout);
        ){
            GameInput gameInput = GameInput.getInstance();
            oos.writeObject(gameInput);
        }catch (IOException io){
            log.error(io);
        }

        return true;
    }

    @Override
    public void init(GameContainer container) throws SlickException{
        this.maps = new ArrayList<GameMap>();

        this.maps.add(new GameMap("data/maps/wide_screen.tmx"));
        //this.maps.add(new GameMap("data/maps/Level_01.tmx"));
        //this.maps.add(new GameMap("data/maps/Level_02.tmx"));
        //this.maps.add(new GameMap("data/maps/Level_03.tmx"));
        //this.maps.add(new GameMap("data/maps/Level_04.tmx"));

        this.mapIterator = maps.iterator();
        this.currentMap = mapIterator.next();

        gameContext = GameContext.initGameContext(app, currentMap);
        gameInput = GameInput.getInstance();

        this.musicOn = true;
        this.soundOn = true;

        List gameObjects = new ArrayList();
        gameContext.addAll(gameObjects);

    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {

        //debug restart game if out of lives
        Player player = gameContext.getPlayer();
        int playerLives = player.getLives();
        if(playerLives <= 0){
            mapIterator = maps.iterator();
            currentMap = mapIterator.next();
            gameContext = GameContext.initGameContext(container, currentMap);
        }

        //debug next level
        boolean end = true;
        Set<PowerUp> powerUps = gameContext.getPowerUps();
        for(PowerUp powerUp : powerUps){
            if(powerUp instanceof CoinPowerUp){
                end = false;
                break;
            }
        }
        if(end){
            //restart from map 0 if we get to the end
            if(!mapIterator.hasNext()){
                mapIterator = maps.iterator();
            }
            if(mapIterator.hasNext()) {
                currentMap = mapIterator.next();
                gameContext.nextLevel(currentMap);
                return;
            }
            else{
                container.exit();
            }
        }

        //debug reset game
        if(player.isDead()){
            if(gameInput.isPressed(GameInput.Button.SELECT) || gameInput.isPressed(GameInput.Button.START)){
                try{
                    if(player.getLives() > 0){
                        gameContext.restart();
                    }
                }catch (SlickException e){
                    e.printStackTrace();
                }
            }
        }


        //mute music
        if(container.getInput().isKeyPressed(Input.KEY_M)){
            musicOn = !musicOn;
            app.setMusicOn(musicOn);
        }

        //mute sound
        if(container.getInput().isKeyPressed(Input.KEY_S)){
            soundOn = !soundOn;
            app.setSoundOn(soundOn);
        }

        //set fullscreen mode
        if(container.getInput().isKeyPressed(Input.KEY_F)){
            fullscreen = !fullscreen;
            if(fullscreen){
                app.setDisplayMode(1920, 1080, true);
            }else{
                app.setDisplayMode(480, 480, false);
            }
        }

        gameContext.update(delta);
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException{
        gameContext.render();
    }

    private static class Log implements LogSystem {
        private Logger log = LogManager.getLogger();

        public Log(){}

        @Override
        public void error(String s, Throwable throwable) {
            log.error(s, throwable);
        }

        @Override
        public void error(Throwable throwable) {
            log.error(throwable);
        }

        @Override
        public void error(String s) {
            log.error(s);
        }

        @Override
        public void warn(String s) {
            log.warn(s);
        }

        @Override
        public void warn(String s, Throwable throwable) {
            log.warn(s, throwable);
        }

        @Override
        public void info(String s) {
            log.info(s);
        }

        @Override
        public void debug(String s) {
            log.debug(s);
        }
    }

}
