package me.finz0.blackout.module.misc;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;

public class Step extends Module {
    public Step() {
        super("Step", Category.MISC);
    }

    private Setting<Mode> mode = register("Mode", Mode.VANILLA);
	private Setting<Float> height = register("Height", 2f, 0.1f, 3f);

    private float oldStepHeight = -1f;

    @Override
    public void onEnable(){
        if(Wrapper.getPlayer() != null){
            oldStepHeight = Wrapper.getPlayer().stepHeight;
            Wrapper.getPlayer().stepHeight = height.getValue();
        }
    }

    @Override
    public void onDisable(){
        if(mode.getValue().equals(Mode.VANILLA) && Wrapper.getPlayer() != null && oldStepHeight != -1f)
            Wrapper.getPlayer().stepHeight = oldStepHeight;
			oldStepHeight = -1f;
    }

    public enum Mode{
        VANILLA
    }
}
