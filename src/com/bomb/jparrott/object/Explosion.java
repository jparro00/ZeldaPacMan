package com.bomb.jparrott.object;

import org.dyn4j.geometry.AABB;

/**
 * Created by jparrott on 11/14/2015.
 */
public class Explosion implements Hazard, Destroyable{

    private float x, y;
    private int width, height;
    private boolean destroyed;
    private boolean safe;

    public Explosion(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Explosion(AABB killZone){
        this.x = (float)killZone.getMinX();
        this.y = (float)killZone.getMinY();
        this.width = (int)(killZone.getMaxX() - killZone.getMinX());
        this.height = (int)(killZone.getMaxY() - killZone.getMinY());
    }

    @Override
    public AABB getKillZone() {
        AABB aabb = new AABB(x, y, x + width, y + height);
        return aabb;
    }

    @Override
    public boolean isSafe() {
        return safe;
    }

    @Override
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
