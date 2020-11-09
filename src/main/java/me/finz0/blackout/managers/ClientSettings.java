package me.finz0.blackout.managers;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.client.Colors;
import me.finz0.blackout.module.client.Settings;

import java.awt.*;

// just a wrapper to get values from the client modules
public class ClientSettings {

    public Colors colors;
    public Settings settings;

    public ClientSettings(){
        colors = (Colors)Blackout.getInstance().moduleManager.getModuleByName("Colors");
        settings = (Settings) Blackout.getInstance().moduleManager.getModuleByName("Settings");
    }

    public Color getColorr(int alpha){
        if(colors.rainbow.getValue()) return Blackout.getInstance().rainbow.getColor(alpha);
        return colors.argb.getValue().getColor();
    }

    public int getColor(){
        if(colors.rainbow.getValue()) return Blackout.getInstance().rainbow.getHex();
        return colors.argb.getValue().getHex();
    }

    public int getColor(int alpha){
        if(colors.rainbow.getValue()) return Blackout.getInstance().rainbow.getColor(alpha).getRGB();
        return colors.argb.getValue().getHex(alpha);
    }

    public String getPrefix(){
        return settings.commandPrefix.getValue();
    }
}
