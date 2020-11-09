package me.finz0.blackout.module.render;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.ColorValue;
import me.finz0.blackout.setting.Setting;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class SkyColor extends Module {
    public SkyColor() {
        super("SkyColor", Category.RENDER);
        hue = 0f;
        hex = -1;
    }

    private float hue;
    private int hex;

    private final Setting<Mode> mode = register("Mode", Mode.SYNC);

    private final Setting<ColorValue> color = register("RGB", new ColorValue(50, 50, 50), b -> mode.getValue().equals(Mode.COLOR));

    private final Setting<Integer> speed = register("Speed", 2, 1, 10, b -> mode.getValue().equals(Mode.RAINBOW));

    public Vec3d getColor(){
        switch (mode.getValue()){
            case SYNC:
            default:
                Color c = Blackout.getInstance().clientSettings.getColorr(255);
                return new Vec3d(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
            case COLOR:
                Color c1 = color.getValue().getColor();
                return new Vec3d(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f);
            case RAINBOW:
                Color rainbow = new Color(hex);
                return new Vec3d(rainbow.getRed() / 255f, rainbow.getGreen() / 255f, rainbow.getBlue() / 255f);
        }
    }

    public void onUpdate(){
        if(mode.getValue().equals(Mode.RAINBOW)) {
            hue += speed.getValue() / 2000f;
            hex = Color.getHSBColor(hue, 1f, 1f).getRGB();
        }
    }

    private enum Mode {
        SYNC, COLOR, RAINBOW
    }
}
