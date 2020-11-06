package me.finz0.blackout.module.gui;

import me.finz0.blackout.module.Module;

public class ArmorWarning extends Module {
    public ArmorWarning() {
        super("ArmorWarning", Category.GUI);
        INSTANCE = this;
    }

    public static ArmorWarning INSTANCE;
}
