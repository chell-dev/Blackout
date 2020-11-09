package me.finz0.blackout.setting;

import org.lwjgl.input.Keyboard;

public class Bind {
    private int keyCode;

    public Bind(int keyCode){
        this.keyCode = keyCode;
    }

    public int getKeyCode(){
        return keyCode;
    }

    public String getKeyName(){
        return Keyboard.getKeyName(keyCode);
    }

    public int setKey(int keyCode){
        this.keyCode = keyCode;
        return keyCode;
    }
}
