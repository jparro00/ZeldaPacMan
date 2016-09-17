package com.bomb.jparrott.object;

import org.dyn4j.geometry.AABB;

/**
 * Created by jparrott on 11/12/2015.
 */
public interface Movable {
    
    public boolean isBlocked(float x, float y);
    public void move(Direction direction, int delta);
    public AABB getTestAABB(float x, float y);

}
