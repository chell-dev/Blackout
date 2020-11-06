package me.finz0.blackout.module.gui;

import me.finz0.blackout.module.Module;

public class PvpInfo extends Module {
    public PvpInfo() {
        super("PvpInfo", Category.GUI);
        INSTANCE = this;
    }

    public static PvpInfo INSTANCE;
}
