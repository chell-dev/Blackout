package me.finz0.blackout.module.gui;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Bind;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module {
    public ClickGuiModule() {
        super("ClickGUI", Category.GUI);
        bind.setValue(new Bind(Keyboard.KEY_P));
    }

    @Override
    public void onEnable(){
        if(Wrapper.mc.currentScreen != null)
            Wrapper.mc.displayGuiScreen(null);

        Blackout.getInstance().resolution = new ScaledResolution(Wrapper.mc);
        Wrapper.mc.displayGuiScreen(Blackout.getInstance().clickGUI);

        disable();
    }
}
