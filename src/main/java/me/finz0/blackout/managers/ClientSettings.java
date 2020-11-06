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
        return new Color(colors.red.getValue(), colors.green.getValue(), colors.blue.getValue(), alpha);
    }

    public int getColor(){
        if(colors.rainbow.getValue()) return Blackout.getInstance().rainbow.getHex();
        return new Color(colors.red.getValue(), colors.green.getValue(), colors.blue.getValue()).getRGB();
    }

    public int getColor(int alpha){
        if(colors.rainbow.getValue()) return Blackout.getInstance().rainbow.getColor(alpha).getRGB();
        return new Color(colors.red.getValue(), colors.green.getValue(), colors.blue.getValue(), alpha).getRGB();
    }

    public String getPrefix(){
        return settings.commandPrefix.getValue();
    }
}
