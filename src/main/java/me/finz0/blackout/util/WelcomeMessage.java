package me.finz0.blackout.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WelcomeMessage {
    private final ITextComponent message;
    public WelcomeMessage(ITextComponent message){
        this.message = message;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        if(Wrapper.getPlayer() != null){
            Wrapper.getPlayer().sendMessage(new TextComponentString("</blackout> ").appendSibling(message));
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
