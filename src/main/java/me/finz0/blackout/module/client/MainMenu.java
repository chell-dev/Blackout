package me.finz0.blackout.module.client;

import me.finz0.blackout.module.Module;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainMenu extends Module {
    public MainMenu() {
        super("MainMenu", Category.CLIENT);
    }

    @SubscribeEvent
    public void onOpenGui(GuiOpenEvent event){
        if(event.getGui() instanceof GuiMainMenu)
            event.setGui(new CustomMainMenu());
    }

    private static class CustomMainMenu extends GuiMainMenu {
    }

    private class Button {

        Button() {

        }
    }
}
