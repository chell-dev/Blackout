package me.finz0.blackout.module.misc;

import joptsimple.internal.Strings;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.util.DiscordRpc;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.module.combat.AutoCrystal;
import me.finz0.blackout.util.Wrapper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class DiscordRpcModule extends Module {
    public DiscordRpcModule() {
        super("DiscordRPC", Category.MISC);
    }

    private int ticksSinceLastInput = 0;

    @Override
    public void onUpdate(){
        DiscordRpc rpc = DiscordRpc.INSTANCE;
        ticksSinceLastInput++;
        if(ticksSinceLastInput / 20 > 120){
            rpc.state = "AFK";
            return;
        }
        AutoCrystal autoCrystal = (AutoCrystal) Blackout.getInstance().moduleManager.getModuleByName("AutoCrystal");
        if(autoCrystal.target != null){
            rpc.state = "Fighting " + autoCrystal.target.getName();
            return;
        }
        if(Wrapper.mc.isIntegratedServerRunning()){
            rpc.state = "Playing Singleplayer";
            return;
        }
        if(Wrapper.mc.getCurrentServerData() != null && !Strings.isNullOrEmpty(Wrapper.mc.getCurrentServerData().serverIP)){
            rpc.state = "Playing " + Wrapper.mc.getCurrentServerData().serverIP;
            return;
        }
        rpc.state = "Main Menu";
    }

    @SubscribeEvent
    public void onInput(InputEvent event){
        ticksSinceLastInput = 0;
    }

    @Override
    protected void onEnable() {
        DiscordRpc.INSTANCE.start();
    }

    @Override
    protected void onDisable() {
        DiscordRpc.INSTANCE.stop();
    }
}
