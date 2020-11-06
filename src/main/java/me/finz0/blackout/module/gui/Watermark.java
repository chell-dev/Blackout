package me.finz0.blackout.module.gui;

import me.finz0.blackout.module.Module;

public class Watermark extends Module {
    public Watermark() {
        super("Watermark", Category.GUI);
        INSTANCE = this;
    }

    public static Watermark INSTANCE;
}
