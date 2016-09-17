package com.bomb.jparrott.object;

/**
 * Created by jparrott on 11/12/2015.
 */
public interface Renderable extends Comparable<Renderable>{
    public void draw();
    public int getRenderableLayer();
}
