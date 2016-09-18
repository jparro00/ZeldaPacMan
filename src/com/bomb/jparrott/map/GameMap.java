package com.bomb.jparrott.map;

import com.bomb.jparrott.object.GameObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.AABB;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jparrott on 11/11/2015.
 */
public class GameMap extends TiledMap implements TileBasedMap{

    public final static int RENDERABLE_LAYER_LOWER = 1;
    public final static int RENDERABLE_LAYER_UPPER = 2;
    private Tile[][] tiles;
    private final Logger log;

    public GameMap(String tmxFile) throws SlickException{
        super(tmxFile);
        this.log = LogManager.getLogger(this.getClass());
        this.tiles = new Tile[width][height];

        for (int xAxis = 0; xAxis < width; xAxis++){
            for (int yAxis = 0; yAxis < height; yAxis++){
                this.tiles[xAxis][yAxis] = new Tile(this, xAxis, yAxis);
            }
        }
    }
    
    public Map getTileAttributes(GameObject gameObject){
        int x = gameObject.getXBlock();
        int y = gameObject.getYBlock();
        return getTileAttributes(x, y);
    }
    public Map getTileAttributes(int x, int y){
        Map attributes = new HashMap();

        try{
            Tile tile = tile = tiles[x][y];
            attributes = tile.getAttributes();
        } catch (ArrayIndexOutOfBoundsException ai){
            ai.printStackTrace();
        }

        return attributes;
    }
    public AABB getTileAABB(int xBlock, int yBlock){
        if(xBlock > width)
            throw new IllegalArgumentException("xBlock cannot be greater than map width");
        if(yBlock > height)
            throw new IllegalArgumentException("yBlock cannot be greater than map height");

        Tile tile = tiles[xBlock][yBlock];
        AABB aabb = tile.getAABB();
        return aabb;
    }

    public Properties getTileProperties(int x, int y, int layerIndex){
        Properties properties = new Properties();
        int tileId = getTileId(x, y, layerIndex);
        int tileSetCount = getTileSetCount();
        for(int i = 0; i < tileSetCount; i++){
            TileSet tileSet = getTileSet(i);
            Map tileProperties = tileSet.getProperties(tileId);
            if(tileProperties != null){
                properties.putAll(tileProperties);
            }
        }
        return properties;
    }
    public void renderLowerLayer(){
        render(0,0,RENDERABLE_LAYER_LOWER);
    }
    public void renderUpperLayer(){
        render(0, 0, RENDERABLE_LAYER_UPPER);
    }

    /**
     * returns true if the tile a x,y coordinates has the attribute "blocked".  If the tile is outside of the map,
     * return true
     *
     * @param xBlock
     * @param yBlock
     * @return
     */
    public boolean isBlocked(int xBlock, int yBlock){
        if(xBlock > getWidth() || yBlock > getHeight()){
            return true;
        }

        boolean isBlocked = false;
        Tile tile = tiles[xBlock][yBlock];
        isBlocked = tile.isBlocked();

        return isBlocked;
    }

    public int getWidthInPixels(){
        return width * tileWidth;
    }

    public int getHeightInPixels(){
        return height * tileHeight;
    }

    @Override
    public int getWidthInTiles() {
        return getWidth();
    }

    @Override
    public int getHeightInTiles() {
        return getHeight();
    }

    @Override
    public void pathFinderVisited(int x, int y) {
        //throw new UnsupportedOperationException("pathFinderVisited() not implemented yet");
    }

    @Override
    public boolean blocked(PathFindingContext context, int tx, int ty) {
        return isBlocked(tx, ty);
    }

    @Override
    public float getCost(PathFindingContext context, int tx, int ty) {
        float cost = 1;
        Map tileAttributes = getTileAttributes(tx, ty);
        //TODO: this only applies to the player
        if("true".equals(tileAttributes.get(Tile.SLOW))){
            cost *= 2;
        }
        return cost;
    }

    public Tile[][] getTiles(){
        return tiles;
    }
}
