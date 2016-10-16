package com.bomb.jparrott.game;

import com.bomb.jparrott.util.OneToManyBidiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton object used to control controller/keyboard inputs
 */
public class GameInput implements Serializable{

    private int controller;
    private int horizontalAxis;
    private int verticalAxis;
    private Map<Button, Integer> buttonMap;
    private OneToManyBidiMap<Integer, Button> keyMap;
    private transient static GameInput instance;
    private transient volatile GameContext gameContext;
    private transient volatile Input input;
    private final static transient Logger log = LogManager.getLogger(GameInput.class);

    private GameInput(){
        this.instance = this;

        //hardcode controller info for now
        this.controller = 0;
        this.horizontalAxis = 1;
        this.verticalAxis = 0;

        //default values for buttonMap
        this.buttonMap = new HashMap<>();
        buttonMap.put(Button.A, 1);
        buttonMap.put(Button.B, 2);
        buttonMap.put(Button.X, 0);
        buttonMap.put(Button.Y, 3);
        buttonMap.put(Button.START, 9);
        buttonMap.put(Button.SELECT, 8);
        buttonMap.put(Button.LB, 4);
        buttonMap.put(Button.RB, 5);

        buttonMap.put(Button.UP, -1);
        buttonMap.put(Button.DOWN, 1);
        buttonMap.put(Button.LEFT, -1);
        buttonMap.put(Button.RIGHT, 1);

        //default values for buttonMap
        this.keyMap = new OneToManyBidiMap<>();
        keyMap.put(Input.KEY_ENTER,Button.A);
        keyMap.put(Input.KEY_B, Button.B);
        keyMap.put(Input.KEY_SPACE, Button.B);
        keyMap.put(Input.KEY_X, Button.X);
        keyMap.put(Input.KEY_Y, Button.Y);
        keyMap.put(Input.KEY_P, Button.START);
        keyMap.put(Input.KEY_ESCAPE, Button.SELECT);
        keyMap.put(Input.KEY_1, Button.LB);
        keyMap.put(Input.KEY_2, Button.RB);

        keyMap.put(Input.KEY_UP, Button.UP);
        keyMap.put(Input.KEY_K, Button.UP);
        keyMap.put(Input.KEY_DOWN, Button.DOWN);
        keyMap.put(Input.KEY_J, Button.DOWN);
        keyMap.put(Input.KEY_LEFT, Button.LEFT);
        keyMap.put(Input.KEY_H, Button.LEFT);
        keyMap.put(Input.KEY_RIGHT, Button.RIGHT);
        keyMap.put(Input.KEY_L, Button.RIGHT);

        keyMap.put(Input.KEY_W, Button.UP);
        keyMap.put(Input.KEY_S, Button.DOWN);
        keyMap.put(Input.KEY_A, Button.LEFT);
        keyMap.put(Input.KEY_D, Button.RIGHT);
        initTransientVariables();
    }

    public enum Button implements Serializable{
        UP, DOWN, LEFT, RIGHT, START, SELECT, A, B, X, Y, LB, RB
    }

    public static GameInput getInstance(){

        if(instance == null){
            File inputConfig = new File(ZeldaPacMan.DIR_SAVE + "input_config.ser");
            if(inputConfig.exists()){
                try(
                        FileInputStream fIn = new FileInputStream(inputConfig);
                        ObjectInputStream ois = new ObjectInputStream(fIn);
                ){
                    instance = (GameInput)ois.readObject();
                }catch (IOException | ClassCastException | ClassNotFoundException ex) {
                    log.warn("unable to load input_config file.  Initializing new GameInput");
                    log.warn(ex);
                    instance = new GameInput();
                }
            }
            else {
                instance = new GameInput();
            }
        }

        return instance;
    }

    public boolean isDown(Button button){

        boolean isDown = false;

        for(Integer i : keyMap.getKey(button)) {
            if(input.isKeyDown(i)){
                isDown = true;
                break;
            }
        }

        //controller
        /*
        if(!isDown){
            try{
                switch (button) {
                    case UP:
                        if(get(button).equals((int) input.getAxisValue(controller, verticalAxis)))
                            isDown = true;
                        break;
                    case DOWN:
                        if(get(button).equals((int) input.getAxisValue(controller, verticalAxis)))
                            isDown = true;
                        break;
                    case LEFT:
                        if(get(button).equals((int) input.getAxisValue(controller, horizontalAxis)))
                            isDown = true;
                        break;
                    case RIGHT:
                        if(get(button).equals((int) input.getAxisValue(controller, horizontalAxis)))
                            isDown = true;
                        break;
                    default:
                        if(input.isButtonPressed(get(button), controller))
                            isDown = true;
                        break;
                }
            }catch (IndexOutOfBoundsException ex){}
        }
        */

        return isDown;
    }

    public boolean isPressed(Button button){
        boolean isPressed = false;

        for(Integer i : keyMap.getKey(button)) {
            if(input.isKeyPressed(i)){
                isPressed = true;
                break;
            }
        }

        /*
        if(!isPressed){
            try {
                switch (button) {
                    case UP:
                        if(input.isControlPressed(2, controller))
                            isPressed = true;
                        break;
                    case DOWN:
                        if(input.isControlPressed(3, controller))
                            isPressed = true;
                        break;
                    case LEFT:
                        throw new UnsupportedOperationException("isPressed(left) unsupported");
                    case RIGHT:
                        throw new UnsupportedOperationException("isPressed(right) unsupported");
                    default:
                        if(input.isControlPressed(get(button) + 4, controller))
                            isPressed = true;
                        break;
                }
            }catch (IndexOutOfBoundsException ex){}
        }
        */

        return isPressed;
    }

    /**
     * initializes transient pieces of this class.  Called during initialization and deserialization
     */
    private void initTransientVariables(){
        gameContext = GameContext.getInstance();
        input = gameContext.getInput();


    }

    /**
     * convenience method to get button mappings
     */
    public Integer get(Button button){
        return buttonMap.get(button);
    }

    /**
     * called during deserialization.  Reads object and initializes all transient variables
     * @param inputStream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException{
        inputStream.defaultReadObject();
        initTransientVariables();
    }

}
