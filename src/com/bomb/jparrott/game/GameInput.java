package com.bomb.jparrott.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.newdawn.slick.Input;

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
    private transient Map<Button, Boolean> buttonState;
    private transient static GameInput instance;
    private transient volatile GameContext gameContext;
    private transient volatile Input input;
    private final static transient Logger log = LogManager.getLogger(GameInput.class);

    private GameInput(){
        this.instance = this;

        //hardcode controller info for now
        this.controller = 3;
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
                }
            }
            else {
                instance = new GameInput();
            }
        }

        return instance;
    }

    public boolean isPressed(Button button){

        boolean isPressed = false;

        switch (button) {
            case UP:
                if(input.isKeyPressed(Input.KEY_UP))
                    isPressed = true;
                else if(input.isKeyDown(Input.KEY_K))
                    isPressed = true;
                break;
            case DOWN:
                if(input.isKeyPressed(Input.KEY_DOWN))
                    isPressed = true;
                else if(input.isKeyDown(Input.KEY_J))
                    isPressed = true;
                break;
            case LEFT:
                if (input.isKeyDown(Input.KEY_LEFT))
                    isPressed = true;
                else if(input.isKeyDown(Input.KEY_H))
                    isPressed = true;
                break;
            case RIGHT:
                if (input.isKeyDown(Input.KEY_RIGHT))
                    isPressed = true;
                else if(input.isKeyDown(Input.KEY_L))
                    isPressed = true;
                break;
            case START:
                if(input.isKeyDown(Input.KEY_P))
                    isPressed = true;
                break;
            case SELECT:
                if(input.isKeyDown(Input.KEY_ESCAPE))
                    isPressed = true;
                break;
            case A:
                break;
            case B:
                if (input.isKeyDown(Input.KEY_SPACE))
                    isPressed = true;
                break;
            case X:
                break;
            case Y:
                break;
            case LB:
                break;
            case RB:
                break;
        }

        //controller
        try{
            switch (button) {
                case UP:
                    if(get(button).equals((int)input.getAxisValue(controller, verticalAxis)))
                        isPressed = true;
                    break;
                case DOWN:
                    if(get(button).equals((int)input.getAxisValue(controller, verticalAxis)))
                        isPressed = true;
                    break;
                case LEFT:
                    if(get(button).equals((int)input.getAxisValue(controller, horizontalAxis)))
                        isPressed = true;
                    break;
                case RIGHT:
                    if(get(button).equals((int)input.getAxisValue(controller, horizontalAxis)))
                        isPressed = true;
                    break;
                case START:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
                case SELECT:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
                case A:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
                case B:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
                case X:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
                case Y:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
                case LB:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
                case RB:
                    if(input.isButtonPressed(get(button), controller))
                        isPressed = true;
                    break;
            }

            //update buttonState
            buttonState.put(button, isPressed);

        }catch (IndexOutOfBoundsException ex){}

        return isPressed;
    }

    /**
     * initializes transient pieces of this class.  Called during initialization and deserialization
     */
    private void initTransientVariables(){
        gameContext = GameContext.getInstance();
        input = gameContext.getInput();

        //init the buttonState
        buttonState = new HashMap<>();
        for(Button button : buttonMap.keySet()){
            buttonState.put(button, isPressed(button));
        }

    }

    /**
     * check if a particular button has been pressed and released (similar to awt events)
     * @param button
     * @return
     */
    public boolean released(Button button){

        boolean wasPressed = buttonState.get(button);

        //note that button state is updated by isPressed
        boolean isPressed = isPressed(button);

        return wasPressed == true && isPressed == false;
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
