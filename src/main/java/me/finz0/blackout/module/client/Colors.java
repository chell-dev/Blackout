package me.finz0.blackout.module.client;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.ColorValue;
import me.finz0.blackout.setting.Setting;

public class Colors extends Module {
    public Colors() {
        super("Colors", Category.CLIENT);
    }

    public final Setting<Boolean> rainbow = register("Rainbow", true);
    public final Setting<ColorValue> argb = register("RGB", new ColorValue(255, 255, 255), p -> !rainbow.getValue());
    //public Setting<Integer> red = register("Red", 255, 0, 255);
    //public Setting<Integer> green = register("Green", 255, 0, 255);
    //public Setting<Integer> blue = register("Blue", 255, 0, 255);
    public final Setting<Integer> rainbowSpeed = register("RainbowSpeed", 2, 1, 10, b -> rainbow.getValue());
}
