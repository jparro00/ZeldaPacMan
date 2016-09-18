package com.bomb.jparrott.map;

import com.bomb.jparrott.object.Blockable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.AABB;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jparrott on 11/11/2015.
 */
public class Tile implements Blockable{

    public static final String BLOCKED = "blocked";
    public static final String SLOW = "slow";
    public static final String PORTAL = "portal";
    public static final String GO_TO = "goTo";

    private final Logger log;
    private AABB aabb;
    private Map<String, Object> attributes;
    private boolean immaterial;

    public Tile(GameMap map, int xBlock, int yBlock){
        this.log = LogManager.getLogger(this.getClass());
        this.attributes = new HashMap();
        this.attributes.put("tileX", xBlock);
        this.attributes.put("tileY", yBlock);
        int tileWidth = map.getTileWidth();
        int tileHeight = map.getTileHeight();
        double minX = xBlock * tileWidth, minY = yBlock * tileHeight, maxX = (xBlock + 1) * tileWidth, maxY = (yBlock + 1) * tileHeight;
        this.aabb = new AABB(minX, minY, maxX, maxY);

        int layerCount = map.getLayerCount();
        for(int layerIndex = 0; layerIndex < layerCount; layerIndex++){
            int tileId = map.getTileId(xBlock, yBlock, layerIndex);
            Map tileAttributes = map.getTileProperties(xBlock, yBlock, layerIndex);
            if(tileAttributes != null){
                attributes.putAll(tileAttributes);
            }
        }
    }

    public boolean isBlocked(){
        boolean isBlocked = false;
        Object value = attributes.get(BLOCKED);

        if ("true".equals(value)) {
            isBlocked = true;
        }
        return isBlocked;
    }
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public AABB getAABB(){
        return aabb;
    }

    @Override
    public boolean isImmaterial() {
        return immaterial;
    }

    @Override
    public void setImmaterial(boolean immaterial) {
        this.immaterial = immaterial;

    }

    public Object put(String key, Object value){
        return attributes.put(key, value);
    }
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
}
