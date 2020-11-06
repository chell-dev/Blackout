package me.finz0.blackout.module.render;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.Module;
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

    private final Setting<Integer> r = register("Red", 50, 0, 255, b -> mode.getValue().equals(Mode.COLOR));
    private final Setting<Integer> g = register("Green", 50, 0, 255, b -> mode.getValue().equals(Mode.COLOR));
    private final Setting<Integer> b = register("Blue", 50, 0, 255, b -> mode.getValue().equals(Mode.COLOR));

    private final Setting<Integer> speed = register("Speed", 2, 1, 10, b -> mode.getValue().equals(Mode.RAINBOW));

    public Vec3d getColor(){
        switch (mode.getValue()){
            case SYNC:
            default:
                Color c = Blackout.getInstance().clientSettings.getColorr(255);
                return new Vec3d(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
            case COLOR:
                return new Vec3d(r.getValue() / 255f, g.getValue() / 255f, b.getValue() / 255f);
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
