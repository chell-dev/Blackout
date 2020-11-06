package me.finz0.blackout.module.combat;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;

public class Reach extends Module {
    public Reach() {
        super("Reach", Category.COMBAT);
    }

    public Setting<Float> distance = register("Distance", 5.5f, 0.0f, 10.0f); // make sure the compiler doesnt think the values are doubles
}
