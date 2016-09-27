package com.bomb.jparrott.gui;

import com.bomb.jparrott.game.GameContext;
import com.bomb.jparrott.game.GameInput;
import com.bomb.jparrott.game.ZeldaPacMan;
import com.bomb.jparrott.object.Renderable;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jparrott on 9/25/2016.
 */
public class Menu implements Renderable{
    private Rectangle rectangle;
    private ShapeFill shapeFill;
    private Graphics graphics;
    private int unitHeight;
    private int unitWidth;
    private List<Item> items;
    float x, y;
    float width, height;
    transient Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);
    transient TrueTypeFont trueTypeFont = new TrueTypeFont(font, true);
    private int selected;
    private volatile GameContext gameContext;
    private volatile GameInput gameInput;
    private boolean visible;

    public Menu(){
        this.gameContext = GameContext.getInstance();
        this.gameInput = GameInput.getInstance();
        this.unitHeight = GameContext.getInstance().getMap().getTileHeight();
        this.unitWidth = GameContext.getInstance().getMap().getTileWidth();
        this.x = unitWidth * 3;
        this.y = unitHeight * 4;
        this.width = unitWidth * 9;
        this.height = unitHeight * 7;
        this.rectangle = new Rectangle(x, y, width, height);
        this.graphics = new Graphics();
        this.items = new ArrayList<>();
        this.selected = 0;
        this.items.add(new Item("Music on/off", Action.MUSIC));
        this.items.add(new Item("Sound on/off", Action.SOUND));
        this.items.add(new Item("Fullscreen on/off", Action.FULLSCREEN));
    }

    public void up(){
        if(selected > 0){
            selected--;
        }
    }
    public void down(){
        if(items.size() > selected + 1){
            selected++;
        }
    }
    public void update(){
        if(!visible){
            return;
        }

        if(gameInput.isPressed(GameInput.Button.DOWN)){
            down();
        }
        else if(gameInput.isPressed(GameInput.Button.UP))
            up();

        if(GameInput.getInstance().isPressed(GameInput.Button.A)){
            items.get(selected).toggle();
        }
        for(Item item : items){
            item.update();
        }
    }

    public void toggle(){
        visible = !visible;
    }

    public enum Action{
        SOUND, MUSIC, FULLSCREEN
    }

    @Override
    public void draw() {
        if(!visible){
            return;
        }
        graphics.setColor(Color.black);
        graphics.fill(rectangle);
        for(Item item : items){
            item.draw();
        }
    }

    @Override
    public int getRenderableLayer(){
        return 2;
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

    public boolean isVisible(){
        return visible;
    }

    public class Item{
        public final static int BUFFER = 5;

        private Action action;
        private String text;
        private float itemX, itemY;

        public Item(String text, Action action){
            this.text = text;
            this.action = action;
        }

        public void toggle(){

            switch (action){

                case SOUND:
                    gameContext.toggleSound();
                    break;
                case MUSIC:
                    gameContext.toggleMusic();
                    break;
                case FULLSCREEN:
                    try{
                        gameContext.toggleFullscreen();
                    } catch (SlickException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        public boolean isSelected(){
            return getIndex() == selected;
        }

        public int getIndex(){
            int index = items.indexOf(this);
            return index;
        }

        public void update(){
            itemX = x;
            itemY = y + (getIndex() * unitHeight);
        }

        public void draw(){
            if(isSelected()){
                drawSelected();
            }else{
                trueTypeFont.drawString(itemX + BUFFER, itemY, text, Color.white);
            }
        }

        public void drawSelected(){
            graphics.setColor(Color.white);
            graphics.drawRect(itemX, itemY, width, unitHeight);
            graphics.drawRect(itemX + 1, itemY + 1, width - 2, unitHeight - 2);
            graphics.drawRect(itemX + 2, itemY + 2, width - 4, unitHeight - 4);

            trueTypeFont.drawString(itemX + BUFFER, itemY, text, Color.white);

        }

    }
}





