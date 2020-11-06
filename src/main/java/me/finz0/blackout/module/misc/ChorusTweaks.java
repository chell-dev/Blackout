package me.finz0.blackout.module.misc;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;

public class ChorusTweaks extends Module {
    public ChorusTweaks() {
        super("ChorusTweaks", Category.MISC);
    }

    public final Setting<Boolean> noCooldown = register("NoCooldown", false);
    public final Setting<Boolean> noSound = register("NoSound", false);
    public final Setting<TpLocation> tpLocation = register("TpLocation", TpLocation.RANDOM);

    public enum TpLocation{
        RANDOM, PLAYER, FRIEND, UP
    }
}
