package me.finz0.blackout.module.misc;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;

public class SwingAnim extends Module {
    public SwingAnim() {
        super("SwingAnim", Category.MISC);
    }

    private final Setting<Boolean> mainHand = register("MainHand", true);
    private final Setting<Integer> main = register("MainProgress", 10, 0, 10, b -> mainHand.getValue());

    private final Setting<Boolean> offHand = register("OffHand", true);
    private final Setting<Integer> off = register("OffProgress", 10, 0, 10, b -> offHand.getValue());

    @Override
    public void onUpdate() {
        if(Wrapper.getPlayer() == null) return;

        if(mainHand.getValue()) {
            Wrapper.mc.entityRenderer.itemRenderer.equippedProgressMainHand = main.getValue() / 10f;

            if(Wrapper.mc.entityRenderer.itemRenderer.itemStackMainHand != Wrapper.getPlayer().getHeldItemMainhand())
                Wrapper.mc.entityRenderer.itemRenderer.itemStackMainHand = Wrapper.getPlayer().getHeldItemMainhand();
        }

        if(offHand.getValue()) {
            Wrapper.mc.entityRenderer.itemRenderer.equippedProgressOffHand = off.getValue() / 10f;

            if(Wrapper.mc.entityRenderer.itemRenderer.itemStackOffHand != Wrapper.getPlayer().getHeldItemOffhand())
                Wrapper.mc.entityRenderer.itemRenderer.itemStackOffHand = Wrapper.getPlayer().getHeldItemOffhand();
        }
    }
}
