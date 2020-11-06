package me.finz0.blackout.module.gui;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;

public class InventoryPreview extends Module {
    public InventoryPreview() {
        super("Inventory", Category.GUI);
        INSTANCE = this;
    }

    public static InventoryPreview INSTANCE;

    public Setting<Background> background = register("Background", Background.TRANS);


    public enum Background{
        NONE, CLEAR, NORMAL, TRANS
    }
}
