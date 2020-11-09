package me.finz0.blackout.managers;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.module.client.*;
import me.finz0.blackout.module.combat.*;
import me.finz0.blackout.module.gui.*;
import me.finz0.blackout.module.misc.*;
import me.finz0.blackout.module.render.*;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.*;

public class ModuleManager {
    private static HashMap<String, Module> modules;
    private static List<Module> enabledModules;

    public ModuleManager(){
        enabledModules = new ArrayList<>();
        modules = new HashMap<>();

		// register modules here
        new ViewModel();
        new SkyColor();
        new PullDown();
        new GuiMove();
        new AutoTrap();
        new KillAura();
        new AntiDesync();
        new FastUse();
        new Step();
        new Chat();
        new SwingAnim();
        new HoleESP();
        new ArmorWarning();
        new OffhandCrystal();
        new AutoTotem();
        new Surround();
        new SoundEffects();
        new NoRender();
        new DiscordRpcModule();
        new Players();
        new BlockHighlight();
        new Reach();
        new AutoCrystal();
        new InventoryPreview();
        new HoleTP();
        new OffhandMode();
        new PvpInfo();
        new Watermark();
        new ClickGuiModule();
        new Sprint();
        new Colors();
        new Settings();

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void register(Module module, String name){
        modules.put(name.toLowerCase(), module);
    }

    @Nullable
    public Module getModuleByName(String name){
        return modules.getOrDefault(name.toLowerCase(), null);
    }

    public ChatFormatting getEnabledColor(String module){
        Module m = getModuleByName(module);
        return m != null && m.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED;
    }

    public HashMap<String, Module> getModules(){
        return modules;
    }

    public List<Module> getEnabledModules(){
        return enabledModules;
    }

    public List<Module> getModulesInCategory(Module.Category category){
        List<Module> list = new ArrayList<>();
        modules.forEach((name, module) ->{
            if(module.getCategory().equals(category))
                list.add(module);
        });
        return list;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        for(Module m : enabledModules){
            m.onUpdate();
        }
    }

    //@SubscribeEvent
    //public void onServerTick(TickEvent.ServerTickEvent event){
    //}

    @SubscribeEvent
    public void onRenderGameOverLay(RenderGameOverlayEvent event){
        if(event instanceof RenderGameOverlayEvent.Pre) {
            for(Module m : enabledModules){
                m.preRender2D();
            }
        } else if(event instanceof RenderGameOverlayEvent.Post){
            if(event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR))
                for(Module m : enabledModules){
                    m.postRender2D();
                }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event){
        for(Module m : enabledModules){
            m.onRender3D();
        }
    }

    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event){
        if(Keyboard.getEventKey() != 0){
            modules.forEach((name, m) ->{
                if(m.bind.getValue().getKeyCode() == Keyboard.getEventKey()) {
                    if(m.getBindBehaviour().equals(Module.BindBehaviour.TOGGLE)){
                        if (Keyboard.getEventKeyState()) m.toggle();
                    } else {
                        m.toggle();
                    }
                }
            });
        }
    }
}
