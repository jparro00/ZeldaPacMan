package com.bomb.jparrott.test;

import org.dyn4j.geometry.AABB;

import java.util.Map;
import java.util.Properties;

/**
 * Created by jparrott on 11/8/2015.
 */
public class Location {

    private Properties properties;
    private AABB aabb;

    public Location(){
        this.properties = new Properties();
    }

    public Location(Properties properties){
        this.properties = properties;
    }

    public void putAllProperties(Map properties){
        this.properties.putAll(properties);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
