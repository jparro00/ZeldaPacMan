package com.bomb.jparrott.game;

import com.bomb.jparrott.map.GameMap;
import com.bomb.jparrott.object.CoinPowerUp;
import com.bomb.jparrott.object.HighScore;
import com.bomb.jparrott.object.Player;
import com.bomb.jparrott.object.PowerUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

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

    public ZeldaPacMan(){
        super("Zelda PacMan");
        this.log = LogManager.getLogger(this.getClass());
    }

    public static void main(String [] arguments){
        try{
            System.setProperty("java.library.path", "lib");
            System.setProperty("org.lwjgl.librarypath", new File("lib/natives/natives-windows").getAbsolutePath());
            Music music = new Music("data/sounds/Overworld.ogg");
            music.loop();

            app = new AppGameContainer(
                    new ScalableGame(new ZeldaPacMan(),480,480));
            app.setDisplayMode(480, 480, false);
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
        System.out.println("highScore: " + HighScore.getScore());
        //Save player object for the next time the player joins
        try(
                FileOutputStream fout = new FileOutputStream(".\\data\\sav\\score.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fout);
        ){
            HighScore highScore = HighScore.getInstance();
            oos.writeObject(highScore.getScore());
        }catch (IOException io){
            log.error(io);
        }
        return true;
    }

    @Override
    public void init(GameContainer container) throws SlickException{
        this.maps = new ArrayList<GameMap>();

        this.maps.add(new GameMap("data/maps/Level_01.tmx"));
        this.maps.add(new GameMap("data/maps/Level_02.tmx"));
        this.maps.add(new GameMap("data/maps/Level_03.tmx"));
        this.maps.add(new GameMap("data/maps/Level_04.tmx"));

        this.mapIterator = maps.iterator();
        this.currentMap = mapIterator.next();

        gameContext = GameContext.initGameContext(app, currentMap);

        this.musicOn = true;
        this.soundOn = true;

        List gameObjects = new ArrayList();
        gameContext.addAll(gameObjects);

    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {

        //pause game if it doesn't have focus
        if(!paused){
            container.setPaused(!container.hasFocus());
        }

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
            if(mapIterator.hasNext()){
                currentMap = mapIterator.next();
                gameContext.initMap(currentMap);
                return;
            }
            else{
                container.exit();
            }
        }

        //debug reset game
        if(container.getInput().isKeyPressed(Input.KEY_ESCAPE)){
            try{
                if(player.getLives() > 0){
                    gameContext.restart();
                }
            }catch (SlickException e){
                e.printStackTrace();
            }
        }

        //pause
        if(container.getInput().isKeyPressed(Input.KEY_P)){
            paused = !paused;
            container.setPaused(paused);
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

        if(!paused){
            gameContext.update(delta);
        }
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException{

        gameContext.render();
    }

}
