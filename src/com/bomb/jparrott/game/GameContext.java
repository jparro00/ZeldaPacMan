package com.bomb.jparrott.game;

import com.bomb.jparrott.map.GameMap;
import com.bomb.jparrott.map.Tile;
import com.bomb.jparrott.object.Blockable;
import com.bomb.jparrott.object.Bomb;
import com.bomb.jparrott.object.BombPowerUp;
import com.bomb.jparrott.object.CoinPowerUp;
import com.bomb.jparrott.object.HeartContainer;
import com.bomb.jparrott.object.Movement;
import com.bomb.jparrott.object.PowerUp;
import com.bomb.jparrott.object.Destroyable;
import com.bomb.jparrott.object.Enemy;
import com.bomb.jparrott.object.GameObject;
import com.bomb.jparrott.object.Hazard;
import com.bomb.jparrott.object.Movable;
import com.bomb.jparrott.object.Player;
import com.bomb.jparrott.object.Renderable;
import com.bomb.jparrott.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Color;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by jparrott on 11/11/2015.
 */
public class GameContext {

    private static GameContext instance = null;

    private GameContainer container;
    private GameMap map;
    private final Logger log;

    //TODO: remove all object types and maintain them all in a single gameObjects set
    private Set<GameObject> gameObjects;
    private Set<Blockable> blockables;
    private Set<Renderable> renderables;
    private Set<Hazard> hazards;
    private Set<Enemy> enemies;
    private Set<PowerUp> powerUps;
    private Player player;
    private Image fog;

    private GameContext(GameContainer container, GameMap map) throws SlickException{

        /*
         * not sure if this is the right way to do this... I was getting an NPE
         * when I called getInstance in some of the objects below that I am creating
         * so I just initialize instance with this
         */
        this.instance = this;

        this.log = LogManager.getLogger(this.getClass());
        this.map = map;
        this.container = container;
        this.gameObjects = new HashSet<GameObject>();
        this.blockables = new HashSet<Blockable>();
        this.renderables = new HashSet<Renderable>();
        this.hazards = new HashSet<Hazard>();
        this.enemies = new HashSet<Enemy>();
        this.powerUps = new HashSet<PowerUp>();
        this.fog = new Image("data/images/fog.png");

        Tile[][] tiles = map.getTiles();
        for (int xAxis = 0; xAxis < tiles.length; xAxis++) {
            for (int yAxis = 0; yAxis < tiles[xAxis].length; yAxis++) {
                Tile tile = tiles[xAxis][yAxis];
                Map<String, Object> tileAttributes = tile.getAttributes();
                if(tile.isBlocked()){
                    blockables.add(tile);
                }
                if("coin".equals(tileAttributes.get("objectStart"))){
                    this.add(new CoinPowerUp(xAxis, yAxis));
                }
                if("bomb".equals(tileAttributes.get("objectStart"))){
                    this.add(new BombPowerUp(xAxis, yAxis));
                }
                if("player".equals(tileAttributes.get("objectStart"))){
                    this.add(new Player(xAxis, yAxis));
                }
                if("enemy".equals(tileAttributes.get("objectStart"))){
                    this.add(new Enemy(xAxis, yAxis, Movement.FOLLOW));
                }
            }
        }

        //add the heart containers for each of player lives
        for(int i = 0; i < player.getLives(); i++){
            add(new HeartContainer(i, 0));
        }

    }

    public static GameContext getInstance() {
        if(instance == null){
            System.out.println("GameContext is being accessed and has not been initialized.  GameContext == " + instance);
        }

        return instance;
    }

    public static GameContext initGameContext(GameContainer gameContainer, GameMap map) throws SlickException {
        instance = new GameContext(gameContainer, map);
        return instance;
    }

    public void update(int delta){

        //update all objects that are currently part of gameObjects
        Set<GameObject> tempGameObjects = new HashSet<GameObject>(gameObjects);
        for(GameObject gameObject : tempGameObjects){
            gameObject.update(delta);
        }

        cleanupDestroyedObjects();

    }

    public void render(){
        map.renderLowerLayer();

        Renderable[] renderables = new Renderable[] {};
        renderables = this.renderables.toArray(renderables);
        Util.quickSort(renderables, 0, renderables.length - 1);
        for(Renderable renderable : renderables){
            renderable.draw();
        }

        map.renderUpperLayer();

        //draw fog over screen if player is dead
        if(player.isDead()){
            fog.draw(0, 0, new Color(1, 1, 1, 0.5f));
        }

    }

    /**
     * attempts to add a give object to any of the appropriate object sets (gameObjects, blockables, renderables,
     * hazards, enemies and player)
     *
     * @param object
     * @return true if object was successfully added to any of the respective sets
     */
    public boolean add(Object object){
        boolean added = false;

        if(object instanceof GameObject){
            added = gameObjects.add((GameObject)object) || added;
        }
        if(object instanceof Blockable){
            added = blockables.add((Blockable)object) || added;
        }
        if(object instanceof Renderable){
            added = renderables.add((Renderable)object) || added;
        }
        if(object instanceof Hazard){
            added = hazards.add((Hazard)object) || added;
        }
        if(object instanceof Enemy){
            added = enemies.add((Enemy)object) || added;
        }
        if(object instanceof PowerUp){
            added = powerUps.add((PowerUp)object) || added;
        }
        if(object instanceof Player){
            added = true;
            this.player = (Player)object;
        }
        return added;
    }

    /**
     * removes all instances of a given class from all object sets
     * @param c
     */
    public void purge(Object o){

        Class c = o.getClass();
        Iterator iterator;

        if(o instanceof GameObject){
            iterator = gameObjects.iterator();
            while (iterator.hasNext()){
                Object object = iterator.next();
                Class objectClass = object.getClass();
                if(c.equals(objectClass)){
                    iterator.remove();
                }
            }
        }
        if(o instanceof Blockable){
            iterator = blockables.iterator();
            while (iterator.hasNext()){
                Object object = iterator.next();
                Class objectClass = object.getClass();
                if(c.equals(objectClass)){
                    iterator.remove();
                }
            }
        }
        if(o instanceof Renderable){
            //debug
            iterator = renderables.iterator();
            while (iterator.hasNext()){
                Object object = iterator.next();
                Class objectClass = object.getClass();
                if(c.equals(objectClass)){
                    iterator.remove();
                }
            }
        }
        if(o instanceof Hazard){
            iterator = hazards.iterator();
            while (iterator.hasNext()){
                Object object = iterator.next();
                Class objectClass = object.getClass();
                if(c.equals(objectClass)){
                    iterator.remove();
                }
            }
        }
        if(o instanceof Enemy){
            iterator = enemies.iterator();
            while (iterator.hasNext()){
                Object object = iterator.next();
                Class objectClass = object.getClass();
                if(c.equals(objectClass)){
                    iterator.remove();
                }
            }
        }
        if(o instanceof PowerUp){
            iterator = powerUps.iterator();
            while (iterator.hasNext()){
                Object object = iterator.next();
                Class objectClass = object.getClass();
                if(c.equals(objectClass)){
                    iterator.remove();
                }
            }
        }
        if(o instanceof Player){
            this.player = null;
        }

    }

    /**
     * removes any object marked as destroyed from all of the appropriate game object sets
     */
    private void cleanupDestroyedObjects(){

        //cleanup gameObjects
        Iterator iter = gameObjects.iterator();
        while(iter.hasNext()){
            Object next = iter.next();
            if(next instanceof Destroyable && ((Destroyable) next).isDestroyed()){
                iter.remove();
            }
        }
        //cleanup blockables
        iter = blockables.iterator();
        while(iter.hasNext()){
            Object next = iter.next();
            if(next instanceof Destroyable && ((Destroyable) next).isDestroyed()){
                iter.remove();
            }
        }
        //cleanup renderables
        iter = renderables.iterator();
        while(iter.hasNext()){
            Object next = iter.next();
            if(next instanceof Destroyable && ((Destroyable) next).isDestroyed()){
                iter.remove();
            }
        }
        //cleanup hazards
        iter = hazards.iterator();
        while(iter.hasNext()){
            Object next = iter.next();
            if(next instanceof Destroyable && ((Destroyable) next).isDestroyed()){
                iter.remove();
            }
        }

        iter = powerUps.iterator();
        while(iter.hasNext()){
            Object next = iter.next();
            if(next instanceof PowerUp && ((PowerUp) next).isDestroyed()){
                iter.remove();
            }
        }

    }

    /**
     * takes a collection of objects and attempts to add them to gameObjects, blockables, renderables, hazards
     * enemies and player
     * Any objects that are not of one of the respective types will be ignored.
     *
     * @param objects
     * @return true if any of the objects are successfully added
     */
    public boolean addAll(Collection objects){
        boolean added = false;

        for(Object object : objects){
            added = add(object) || added;
        }

        return added;
    }

    public GameContext newMap(GameMap map) throws SlickException{
        GameContext newGameContext = new GameContext(container, map);
        newGameContext.purge(player);
        newGameContext.setPlayer(player);

        //TODO: clean this purge method up. the object parameter is just a bandaid
        newGameContext.purge(new HeartContainer(0, 0));

        //add the heart containers for each of player lives
        for(int i = 0; i < this.player.getLives(); i++){
            newGameContext.add(new HeartContainer(i, 0));
        }

        return newGameContext;
    }

    public GameContext restart() throws SlickException {
        GameContext newGameContext = new GameContext(container, map);
        initGameContext(container, map);
        Set<PowerUp> remainingPowerUps = powerUps;
        Set<PowerUp> powerUps = newGameContext.getPowerUps();

        //mark all these objects for destruction
        for(PowerUp powerUp : powerUps){
            powerUp.setDestroyed(true);
        }
        newGameContext.addAll(remainingPowerUps);

        this.player.revive();
        newGameContext.setPlayer(this.player);

        //TODO: clean this purge method up. the object parameter is just a bandaid
        newGameContext.purge(new HeartContainer(0, 0));

        //add the heart containers for each of player lives
        for(int i = 0; i < this.player.getLives(); i++){
            newGameContext.add(new HeartContainer(i, 0));
        }

        return newGameContext;
    }

    public void refreshHeartContainers() throws SlickException{
        //TODO: clean this purge method up. the object parameter is just a bandaid
        purge(new HeartContainer(0, 0));

        //add the heart containers for each of player lives
        for(int i = 0; i < this.player.getLives(); i++){
            add(new HeartContainer(i, 0));
        }
    }
    public boolean isCollidingWithBlockables(Movable movable, AABB aabb){
        boolean isColliding = false;
        for(Blockable blockable : blockables){
            if(blockable.isImmaterial()){
                continue;
            }
            AABB blockableAABB = blockable.getAABB();
            if(aabb.overlaps(blockableAABB) && movable != blockable){
                isColliding = true;
                break;
            }
        }
        return isColliding;
    }

    public int getTileWidth(){
        return map.getTileWidth();
    }

    public int getTileHeight(){
        return map.getTileHeight();
    }

    public GameMap getMap(){
        return map;
    }

    public Set<Blockable> getBlockables(){
        return blockables;
    }

    public Set<Hazard> getHazards(){
        return hazards;
    }

    public Set<Renderable> getRenderables(){
        return renderables;
    }

    public Set<GameObject> getGameObjects(){
        return gameObjects;
    }

    public Set<PowerUp> getPowerUps(){
        return powerUps;
    }

    public Input getInput(){
        return container.getInput();
    }

    public int getPlayerXBlock(){
        return player.getXBlock();
    }

    public int getPlayerYBlock(){
        return player.getYBlock();
    }

    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player player){
        if(this.player instanceof Player){
            Iterator iterator = renderables.iterator();
            while(iterator.hasNext()){
                Renderable renderable = (Renderable)iterator.next();
                if(renderable instanceof Player){
                    iterator.remove();
                    //break;
                }
            }
            iterator = gameObjects.iterator();
            while(iterator.hasNext()){
                GameObject gameObject = (GameObject)iterator.next();
                if(gameObject instanceof Player){
                    iterator.remove();
                }
            }

            int xBlock = this.player.getXBlock();
            int yBlock = this.player.getYBlock();
            player.setXBlock(xBlock);
            player.setYBlock(yBlock);
            this.player = player;
            this.renderables.add(player);
            this.gameObjects.add(player);
        }
        else{
            this.player = player;
        }

    }
}
