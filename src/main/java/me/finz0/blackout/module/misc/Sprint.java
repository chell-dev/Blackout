package me.finz0.blackout.module.misc;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.util.Wrapper;
import me.finz0.blackout.setting.Setting;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.MISC);
    }

    @SuppressWarnings("unchecked")
    private Setting<Boolean> onlyForward = register("OnlyForward", false);

    public void onUpdate(){
        if(Wrapper.getPlayer() == null) return;
        if(onlyForward.getValue()){
            if(Wrapper.getPlayer().moveForward > 0) Wrapper.getPlayer().setSprinting(true);
        } else {
            if(Wrapper.getPlayer().moveForward != 0 || Wrapper.getPlayer().moveStrafing != 0) Wrapper.getPlayer().setSprinting(true);
        }
    }
}
