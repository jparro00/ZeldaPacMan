package com.bomb.jparrott.test;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author panos
 */
public class MyGame extends BasicGame
{
    private static final int size = 32;
    private TiledMap grassMap;
    private Animation sprite, up, down, left, right;
    private float x = 32f, y = 32f;
    private float centerX = x + size / 2, centerY = y + size / 2;
    private float speed = .1f;

    /** The collision map indicating which tiles block movement - generated based on tile properties */
    private boolean[][] blocked;
    private Rectangle[][] tiles;

    public MyGame()
    {
        super("Wizard game");
    }

    public static void main(String [] arguments)
    {
        try
        {
            AppGameContainer app = new AppGameContainer(new MyGame());
            app.setDisplayMode(640, 640, false);
            app.start();
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer container) throws SlickException
    {
        SpriteSheet spriteSheet = new SpriteSheet("data/fullSpriteSheet.png", 32, 32, new Color(255, 0, 255));
        int[] uFrames = {0,4,1,4};
        int[] dFrames = {7,1,8,1};
        int[] lFrames = {2, 3, 3, 3};
        int[] rFrames = {2, 2, 3, 2};
        //Image [] movementRight = { new Image("data/s1.png"),new Image("data/s2.png") };
        int d = 150;
        int[] duration = {d,d,d,d};
        grassMap = new TiledMap("data/GrassMap.tmx");


          /*
          * false variable means do not auto update the animation.
          * By setting it to false animation will update only when
          * the user presses a key.
          */
        up = new Animation(spriteSheet, uFrames, duration);
        up.setAutoUpdate(false);
        down = new Animation(spriteSheet, dFrames, duration);
        down.setAutoUpdate(false);
        left = new Animation(spriteSheet, lFrames, duration);
        left.setAutoUpdate(false);
        right = new Animation(spriteSheet, rFrames, duration);
        right.setAutoUpdate(false);

        // Original orientation of the sprite. It will look right.
        sprite = right;

        // build a collision map based on tile properties in the TileD map
        blocked = new boolean[grassMap.getWidth()][grassMap.getHeight()];

        int trueCount = 0;
        int falseCount = 0;

        for (int xAxis=0;xAxis<grassMap.getWidth(); xAxis++)
        {
            for (int yAxis=0;yAxis<grassMap.getHeight(); yAxis++)
            {
                int tileID = grassMap.getTileId(xAxis, yAxis, 0);
                String value = grassMap.getTileProperty(tileID, "blocked", "false");
                if ("true".equals(value))
                {
                    blocked[xAxis][yAxis] = true;
                }
            }
        }
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
        Input input = container.getInput();
        if (input.isKeyDown(Input.KEY_UP) || input.isKeyDown(Input.KEY_K))
        {
            sprite = up;
            if (!isBlocked(x, y - delta * speed) && !isBlocked(x + size, y - delta * speed))
            {
                sprite.update(delta);
                // The lower the delta the slowest the sprite will animate.
                y -= speed;
            }
        }
        else if (input.isKeyDown(Input.KEY_DOWN) || input.isKeyDown((Input.KEY_J)))
        {
            sprite = down;
            if (!isBlocked(x , y + size + delta * speed) && !isBlocked(x + size , y + size + delta * speed))
            {
                sprite.update(delta);
                y += speed;
            }
        }
        else if (input.isKeyDown(Input.KEY_LEFT) || input.isKeyDown(Input.KEY_H))
        {
            sprite = left;
            if (!isBlocked(x - delta * speed, y) && !isBlocked(x - delta * speed, y + size))
            {
                sprite.update(delta);
                x -= speed;
            }
        }
        else if (input.isKeyDown(Input.KEY_RIGHT) || input.isKeyDown(Input.KEY_L))
        {
            sprite = right;
            if (!isBlocked(x + size + delta * speed, y) && !isBlocked(x + size + delta * speed, y + size))
            {
                sprite.update(delta);
                x += speed;
            }
        }
    }

    public void render(GameContainer container, Graphics g) throws SlickException
    {
        grassMap.render(0, 0);
        sprite.draw((int)x, (int)y);
    }

    private boolean isBlocked(float x, float y)
    {
        int xBlock = (int)x / size;
        int yBlock = (int)y / size;
        System.out.println("yBlock: " + yBlock);
        System.out.println("xBlock: " + xBlock);
        return blocked[xBlock][yBlock];
    }
}
