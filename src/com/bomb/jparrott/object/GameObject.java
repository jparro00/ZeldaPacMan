package com.bomb.jparrott.object;

import com.bomb.jparrott.game.GameContext;
import com.bomb.jparrott.map.GameMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.AABB;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import sun.security.krb5.internal.crypto.Des;

import java.util.Map;
import java.util.Properties;

/**
 * Created by jparrott on 11/11/2015.
 */
public abstract class GameObject implements Renderable, Destroyable{

    public final static int DEFAULT_RENDERABLE_LAYER = 1;

    protected final Logger log;
    protected float x, y;
    protected int width, height;
    protected boolean destroyed;
    protected Map<String, Object> attributes;
    protected volatile GameContext gameContext;
    protected int renderableLayer;

    protected GameObject(float x, float y, int width, int height) {
        this.log = LogManager.getLogger(this.getClass());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gameContext = GameContext.getInstance();
        this.renderableLayer = DEFAULT_RENDERABLE_LAYER;
    }

    public abstract void update(int delta);

    /**
     * puts an object on this GameObject's attribute map
     *
     * @param key
     * @param value
     * @return
     */
    public Object put(String key, Object value){
        return attributes.put(key, value);
    }

    /**
     * gets an object from this GameObject's attribute map
     * @param key
     * @return
     */
    public Object get(String key){
        return attributes.get(key);
    }

    /**
     * returns boolean value of entry from attribute map if key exists and is of type boolean.  Else if it
     * is of type String, return value if it equals (?i:true|false).  For all other cases return false
     *
     * @param key
     * @return
     */
    public boolean getBoolean(String key){
        boolean returnBoolean = false;
        Object value = attributes.get(key);
        returnBoolean = value instanceof Boolean ? (Boolean)value : false;

        //if this value is a boolean, return value
        if(value instanceof Boolean){
            returnBoolean = (Boolean)value;
        }
        //if this value is a String, if it equals (?i:true|false) return value, else return false
        else if(value instanceof String){
            String stringValue = (String)value;
            if("true".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue)){
                returnBoolean = Boolean.valueOf(stringValue);
            }
            else{
                returnBoolean = false;
            }
        }
        //for all other cases return false
        else{
            returnBoolean = false;
        }
        return returnBoolean;
    }

    /**
     * returns a String if key exists on attribute map and is of type String.  Else if it is of type boolean,
     * return String equivalent, else return null
     *
     * @param key
     * @return
     */
    public String getString(String key){
        String returnString = null;
        Object value = attributes.get(key);
        returnString = value instanceof String ? (String)value : null;
        if(value instanceof String){
            returnString = (String)value;
        }
        else if(value instanceof Boolean){
            returnString = String.valueOf(value);
        }
        else{
            returnString = null;
        }

        return returnString;
    }

    public AABB getAABB(){
        AABB aabb;
        double minX = x, minY = y, maxX = x + width, maxY = y + height;
        aabb = new AABB(minX, minY, maxX, maxY);
        return aabb;
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
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
    @Override
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public GameContext getGameContext() {
        return gameContext;
    }
    public float getCenterX() {
        return x + (width / 2);
    }
    public float getCenterY() {
        return y + (height / 2);
    }
    public void setCenterX(float x){
        this.x = x - (width / 2);
    }
    public void setCenterY(float y){
        this.y = y - (height / 2);
    }

    /**
     * returns the y coordinate of the block at the center of this object
     * @return
     */
    public int getYBlock(){
        int tileHeight = gameContext.getTileHeight();
        int yBlock = (int)(getCenterY() / tileHeight);
        return yBlock;
    }

    /**
     * returns the x coordinate of the block at the center of this object
     *
     * @return
     */
    public int getXBlock(){
        int tileWidth = gameContext.getTileWidth();
        int xBlock = (int)(getCenterX() / tileWidth);
        return xBlock;
    }

    /**
     * sets centerX of this object to the centerX of the specified xBlock
     *
     * @param x
     */
    public void setXBlock(int x){
        int tileWidth = gameContext.getTileWidth();
        float centerX = (x * tileWidth) + (tileWidth / 2);
        setCenterX(centerX);
    }

    /**
     * sets centerY of this object to the centerY of the specified YBlock
     *
     * @param x
     */
    public void setYBlock(int y){
        int tileHeight = gameContext.getTileHeight();
        setCenterY((y * tileHeight) + (tileHeight / 2));
    }

    /**
     * returns the tileAttributes of the tile at the center of this object
     *
     * @return
     */
    public Map<String, Object> getCurrentTileAttributes(){
        Map<String, Object> tileAttributes;
        GameMap map = gameContext.getMap();
        tileAttributes = map.getTileAttributes(getXBlock(), getYBlock());
        return tileAttributes;
    }

    @Override
    public int getRenderableLayer(){
        return renderableLayer;
    }

    public void setRenderableLayer(int renderableLayer){
        this.renderableLayer = renderableLayer;
    }

    @Override
    public int compareTo(Renderable renderable){
        int compareInt;
        int thisRenderableLayer = getRenderableLayer();
        int otherRenderableLayer = renderable.getRenderableLayer();

        if(thisRenderableLayer < otherRenderableLayer){
            compareInt = -1;
        }
        else if(thisRenderableLayer == otherRenderableLayer){
            compareInt = 0;
        }
        else{
            compareInt = 1;
        }

        return compareInt;
    }

}
