package com.bomb.jparrott.test;

import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Renderable;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author panos
 */
public class ZeldaGameTest extends BasicGame
{
    public static final String TILE_SLOW = "slow";
    public static final String TILE_BLOCKED = "blocked";
    public static final String TILE_PORTAL = "portal";
    public static final String TILE_GO_TO = "goTo";
    public static final String CONTEXT_TILE_SIZE = "tileSize";
    public static final String CONTEXT_PLAYER_X_BLOCK = "playerXBlock";
    public static final String CONTEXT_PLAYER_Y_BLOCK = "playerYBlock";
    public static final int MAP_LAYER_LOWER = 1;
    public static final int MAP_LAYER_UPPER = 2;

    private static final int size = 32;
    private AABBTiledMap map;
    private Player player;
    private List<Enemy> enemies;
    private boolean paused;
    private float x = 32f, y = 32f;
    private float centerX = x + size / 2, centerY = y + size / 2;
    private float speed = .2f;
    private Map context;
    private Set<Renderable> lowerRenderables;
    private Set<Renderable> upperRenderables;

    //debug
    private Animation bomb;

    /** The collision map indicating which tiles block movement - generated based on tile properties */
    private AABB[][] blocked;

    public ZeldaGameTest()
    {
        super("Zelda PacMan");
    }

    public static void main(String [] arguments)
    {
        boolean test = false;
        Object value = test;
        System.out.println(value instanceof Boolean);
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        try
        {
            AppGameContainer app = new AppGameContainer(new ZeldaGameTest());
            app.setDisplayMode(480, 480, false);
            app.setShowFPS(false);
            app.setTargetFrameRate(60);
            app.start();
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        initContext();

        paused = false;
        SpriteSheet playerSpriteSheet = new SpriteSheet("data/playerSpriteSheet.png", 32, 32, new Color(255, 0, 255));
        SpriteSheet enemySpriteSheet= new SpriteSheet("data/bug.png", 32, 32, new Color(255, 0, 255));
        player = new Player(map, context, playerSpriteSheet, 32, 150, 20, x, y, speed);
        enemies = new ArrayList();

        //initialize enemies
        Enemy enemy1 = new Enemy(map, 20, context, enemySpriteSheet, 32, 150,30,400,32,.1f);
        Enemy enemy2 = new Enemy(map, 100, context, enemySpriteSheet, 32, 150,30,400,96,.1f);
        enemy1.setDefaultMovement(Enemy.DEFAULT_MOVEMENT_HORIZONTAL);
        enemy2.setDefaultMovement(Enemy.DEFAULT_MOVEMENT_FOLLOW);
        enemies.add(enemy1);
        enemies.add(enemy2);

        // build a collision map based on tile properties in the TileD map
        blocked = map.getBlocked();
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {

        player.setCurrentTileProperties(map.getLocationProperties(player));
        for(Enemy enemy : enemies){
            enemy.setCurrentTileProperties(map.getLocationProperties(enemy));
        }

        Input input = container.getInput();
        if(input.isKeyDown(Input.KEY_SPACE)){
            init(container);
            paused = false;
            container.setPaused(paused);
        }

        if(isOverlappingWithEnemy(player)) {
            container.pause();
            paused = true;
        }

        if(!paused){
            player.move(input, blocked, delta);
            context.put(CONTEXT_PLAYER_X_BLOCK, player.getXBlock());
            context.put(CONTEXT_PLAYER_Y_BLOCK, player.getYBlock());
            for(Enemy enemy : enemies){
                enemy.defaultMove(blocked, delta);
            }
        }

    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException
    {
        //render the base map layer
        map.render(0, 0, ZeldaGameTest.MAP_LAYER_LOWER);

        //render all the objects
        player.draw();
        for(Enemy enemy : enemies){
            enemy.draw();
        }

        //render the upper map layer
        map.render(0,0,ZeldaGameTest.MAP_LAYER_UPPER);
    }

    public void initContext() throws SlickException{
        context = new HashMap();
        map = new AABBTiledMap("data/Level_01.tmx");
        context.put(CONTEXT_TILE_SIZE, 32);
        context.put("map", map);
    }
    public void initContext(Map context){
        this.context = context;
    }

    public boolean isOverlappingWithEnemy(Collider collider){
        boolean isOverlapping = false;
        for(Enemy enemy : enemies){
            if(collider.getAABB().overlaps(enemy.getAABB())){
                isOverlapping = true;
                break;
            }
        }
        return isOverlapping;
    }
    public int getHeight(){
        return map.getHeight();
    }
    public int getWidth(){
        return map.getWidth();
    }

}
