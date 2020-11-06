package me.finz0.blackout.module.gui;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;

public class Players extends Module {
    public Players() {
        super("Players", Category.GUI);
        INSTANCE = this;
    }

    public static Players INSTANCE;

    public Setting<Align> align = register("Align", Align.RIGHT);

    public enum Align{
        LEFT, RIGHT
    }
}
