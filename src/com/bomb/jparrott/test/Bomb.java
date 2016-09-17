package com.bomb.jparrott.test;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Renderable;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

import java.io.ObjectInputValidation;

/**
 * Created by jparrott on 11/7/2015.
 */
public class Bomb implements Renderable{

    private float x, y;

    public Bomb(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, float x, float y) {
        float radius = 16f;
        Shape circle = new Circle(x, y, radius);
//        g.draw

    }

    @Override
    public void draw(float x, float y) {

    }
}
