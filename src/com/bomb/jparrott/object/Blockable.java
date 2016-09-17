package com.bomb.jparrott.object;

import org.dyn4j.geometry.AABB;

/**
 * Created by jparrott on 11/12/2015.
 */
public interface Blockable {

    public AABB getAABB();
    public boolean isImmaterial();
    public void setImmaterial(boolean immaterial);
}
