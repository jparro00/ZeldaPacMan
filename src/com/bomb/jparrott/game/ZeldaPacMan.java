package com.bomb.jparrott.game;

import com.bomb.jparrott.map.GameMap;
import com.bomb.jparrott.object.CoinPowerUp;
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
import org.newdawn.slick.SlickException;

import java.io.File;
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

            app = new AppGameContainer(new ZeldaPacMan());
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
//                player.decreaseLives();
                if(player.getLives() > 0){
                    gameContext.restart();
                }
//                gameContext = new GameContext(container, currentMap);
            }catch (SlickException e){
                e.printStackTrace();
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

        gameContext.update(delta);
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException{

        gameContext.render();
    }

}
