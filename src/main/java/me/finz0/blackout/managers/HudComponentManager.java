package me.finz0.blackout.managers;

import com.google.common.collect.Lists;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.gui.ClickGUI;
import me.finz0.blackout.hud.HudComponent;
import me.finz0.blackout.hud.components.*;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class HudComponentManager {
    private List<HudComponent> components;



    public HudComponentManager(){
        components = Lists.newArrayList(
                new WatermarkComponent(),
                new PvpInfoComponent(),
                new OffhandModeComponent(),
                new InventoryComponent(),
                new PlayersComponent(),
                new ArmorWarningComponent()
				// register hud components here
        );
        MinecraftForge.EVENT_BUS.register(this);
    }

    public List<HudComponent> getComponents(){
        return components;
    }

    @Nullable
    public HudComponent getComponentByName(String name){
        for(HudComponent component : components){
            if(component.getName().equalsIgnoreCase(name))
                return component;
        }
        return null;
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Post event){
        if(event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR) && !(Wrapper.mc.currentScreen instanceof ClickGUI)){
            for(HudComponent c : components){
                if(c.isInvisible()) continue;
                c.render();
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event){
        Blackout.getInstance().resolution = new ScaledResolution(Wrapper.mc);
    }

}
