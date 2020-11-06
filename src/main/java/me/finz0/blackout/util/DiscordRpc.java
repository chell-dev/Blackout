package me.finz0.blackout.util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.finz0.blackout.Blackout;

// https://github.com/MinnDevelopment/java-discord-rpc/blob/master/examples/java/ml/yuuki/chris/rpc/examples/WindowTest.java
public class DiscordRpc {
    public DiscordRpc(){
        INSTANCE = this;
        lib = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
        handlers = new DiscordEventHandlers();
    }

    private Thread thread = null;
    public String state = "";
    public static DiscordRpc INSTANCE;

    private DiscordRPC lib;
    private DiscordRichPresence presence;
    private DiscordEventHandlers handlers;

    private void init(){
        lib.Discord_Initialize("718760043738038304" /* discord app id */, handlers, true, "");

        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details = "";
        presence.state = state;
        presence.largeImageKey = "img"; // image names you uploaded to the discord dev thing
		//presence.smallImageKey = "trans"; // image names you uploaded to the discord dev thing
		//presence.smallImageText = "trans rights!"; // text that shows when you hover over the small image
        lib.Discord_UpdatePresence(presence);
    }

    public void start(){
        init();

        thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                if (!presence.state.equals(state)) {
                    presence.state = state;
                    lib.Discord_UpdatePresence(presence);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "RPC-Callback-Handler");

        thread.start();
        Blackout.getInstance().log.info("Started Discord RPC");
    }

    public void stop(){
        if(thread != null && thread.isAlive() && !thread.isInterrupted()) {
            thread.interrupt();
            lib.Discord_Shutdown();
            thread = null;
            Blackout.getInstance().log.info("Stopped Discord RPC");
        }
    }
}
