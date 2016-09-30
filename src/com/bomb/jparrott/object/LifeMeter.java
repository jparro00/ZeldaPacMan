package com.bomb.jparrott.object;

import com.bomb.jparrott.game.GameContext;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jparrott on 9/29/2016.
 */
public class LifeMeter extends GameObject{

    private List<HeartContainer> heartContainers;
    private volatile GameContext gameContext;

    public LifeMeter(){
        super(0, 0, 32, 32);
        this.heartContainers = new ArrayList<>();
        this.gameContext = GameContext.getInstance();
    }

    @Override
    public void draw(){
        for(HeartContainer heartContainer : heartContainers){
            heartContainer.draw();
        }
    }

    public void update(int delta) {
        int lives = gameContext.getPlayer().getLives();

        try{
            while(heartContainers.size() < lives){
                heartContainers.add(new HeartContainer(heartContainers.size(), 0));
            }
            while(heartContainers.size() > lives){
                heartContainers.remove(heartContainers.size() - 1);
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

}
