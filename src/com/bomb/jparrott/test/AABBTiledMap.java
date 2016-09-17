package com.bomb.jparrott.test;

import org.dyn4j.geometry.AABB;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jparrott on 10/26/2015.
 */
public class AABBTiledMap extends TiledMap implements TileBasedMap{

    private AABB[][] blocked;
    private Location[][] locations;

    public AABBTiledMap(String tmxFile) throws SlickException{
        super(tmxFile);
        int width = getWidth();
        int height = getHeight();
        this.blocked = new AABB[width][height];
        this.locations = new Location[width][height];

        for (int xAxis = 0; xAxis < width; xAxis++){
            for (int yAxis = 0; yAxis < height; yAxis++){

                int layerCount = getLayerCount();
                Properties properties = new Properties();
                properties.put("tileX", xAxis);
                properties.put("tileY", yAxis);
                this.locations[xAxis][yAxis] = new Location(properties);

                for(int layerIndex = 0; layerIndex < layerCount; layerIndex++){
                    int tileId = getTileId(xAxis, yAxis, layerIndex);
                    Properties tileProperties = getTileProperties(xAxis, yAxis, layerIndex);
                    if(tileProperties != null){
                        this.locations[xAxis][yAxis].putAllProperties(tileProperties);
                    }
                }

                int tileWidth = getTileWidth();
                int tileHeight = getTileHeight();

                double minX = xAxis * tileWidth, minY = yAxis * tileHeight, maxX = (xAxis + 1) * tileWidth, maxY = (yAxis + 1) * tileHeight;

                Properties props = locations[xAxis][yAxis].getProperties();
                if ("true".equals(props.get(ZeldaGameTest.TILE_BLOCKED))){
                    this.blocked[xAxis][yAxis] = new AABB(minX, minY,maxX, maxY);
                }
            }
        }
    }
    public Properties getLocationProperties(Collider collider){
        int x = collider.getXBlock();
        int y = collider.getYBlock();
        return getLocationProperties(x, y);
    }
    public Properties getLocationProperties(int x, int y){
        Location location = locations[x][y];
        Properties properties = location.getProperties();
        return properties;
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

    public AABB[][] getBlocked() {
        return blocked;
    }

    public void setBlocked(AABB[][] blocked) {
        this.blocked = blocked;
    }

    public static AABB getTileAABB(int xBlock, int yBlock, int size){
        double minX = xBlock * size, minY = yBlock * size, maxX = (xBlock + 1) * size, maxY = (yBlock + 1) * size;
        AABB aabb = new AABB(minX, minY, maxX, maxY);
        return aabb;
    }

    public void render(){
        List<Layer> layers = this.layers;
        for(Layer layer : layers){

        }

    }

    public Layer getLayer(int index){
        return (Layer)this.layers.get(index);
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
        if(tx > getWidth() || ty > getHeight()){
            return false;
        }
        String isBlocked = null;
        Properties locationProperties = getLocationProperties(tx, ty);
        isBlocked = (String)locationProperties.get(ZeldaGameTest.TILE_BLOCKED);
        isBlocked = isBlocked != null ? isBlocked : "false";
        return Boolean.valueOf(isBlocked);
    }

    @Override
    public float getCost(PathFindingContext context, int tx, int ty) {
        float cost = 1;
        Properties locationProperties = getLocationProperties(tx, ty);
        //TODO: this only applies to the player
        if("true".equals(locationProperties.get(ZeldaGameTest.TILE_SLOW))){
            cost *= 2;
        }
        return cost;
    }
}
