package me.finz0.blackout.setting;

import java.awt.*;

public class ColorValue {
    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public ColorValue(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public ColorValue(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
    }

    public ColorValue(int argb){
        Color color = new Color(argb);
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = color.getAlpha();
    }

    public ColorValue(int rgb, int alpha){
        Color color = new Color(rgb);
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = alpha;
    }

    public int getRed() {
        return r;
    }

    public int getGreen() {
        return g;
    }

    public int getBlue() {
        return b;
    }

    public int getAlpha() {
        return a;
    }

    public Color getColor(){
        return new Color(r, g, b, a);
    }

    public Color getColor(int alpha){
        return new Color(r, g, b, alpha);
    }

    public int getHex(){
        return getColor().getRGB();
    }

    public int getHex(int alpha){
        return getColor(alpha).getRGB();
    }

}
