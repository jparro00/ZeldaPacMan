package com.bomb.jparrott.object;

import org.dyn4j.geometry.AABB;

/**
 * Created by jparrott on 11/12/2015.
 */
public interface Hazard {

    public AABB getKillZone();
    public boolean isSafe();
    public void setSafe(boolean immaterial);
}
