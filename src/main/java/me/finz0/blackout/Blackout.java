package me.finz0.blackout;

import me.finz0.blackout.gui.ClickGUI;
import me.finz0.blackout.managers.*;
import me.finz0.blackout.managers.SettingManager;
import me.finz0.blackout.util.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.IOException;

// tip: if you're gonna actually code your own stuff and not just skid looking at minecraft's source code is very helpful
@Mod(modid = Blackout.MODID, name = Blackout.MODNAME, version = Blackout.MODVER)
public class Blackout {
    public static final String MODID = "blackout";
    public static final String MODNAME = "Blackout";
    public static final String MODVER = "2.0";

    public Logger log = LogManager.getLogger(MODNAME);

    private static Blackout instance;

    public ScaledResolution resolution;

    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public RainbowManager rainbow;
    public ClientSettings clientSettings;
    public SettingManager settingManager;
    public PlayerStatus playerStatus;
    public HudComponentManager hudComponentManager;
    public ClickGUI clickGUI;
    public RotationManager rotationManager;

    public Blackout() {
        instance = this;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
		// initialize everything - note the order of this matters in some cases e.g. you have to init modules before hud components
        resolution = new ScaledResolution(Wrapper.mc);
        new DiscordRpc();
        playerStatus = new PlayerStatus();
        rainbow = new RainbowManager();
        settingManager = new SettingManager();
        moduleManager = new ModuleManager();
        clientSettings = new ClientSettings();
        commandManager = new CommandManager();
        hudComponentManager = new HudComponentManager();
        clickGUI = new ClickGUI();
        rotationManager = new RotationManager();
        new RenderUtils();
        new EntityUtils();
        new KillEventHelper();
        Config config = new Config();

		// load config
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
            ITextComponent main = new TextComponentString("There was an error while loading the config. Check your settings and keybinds! ")
                    .setStyle(new Style().setColor(TextFormatting.RED).setBold(true));
            ITextComponent clickable = new TextComponentString("(click here for details)")
                    .setStyle(new Style().setColor(TextFormatting.GRAY)
                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clientSettings.getPrefix() + "clientmessage " + e)));
            new WelcomeMessage(main.appendSibling(clickable));
        }

		// add a shutdown hook to save config
        Runtime.getRuntime().addShutdownHook(new Thread(MODNAME + " shutdown hook"){
            @Override
            public void run(){
                try {
                    config.save();
                    log.info("Saved config");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Display.setTitle("block game");
        log.info(toString() + " initialized");
    }

	// used to access non-static fields and methods
    public static Blackout getInstance(){
        return instance;
    }

	// you can use Blackout.getInstance().toString(); to get "Blackout 2.0"
    @Override
    public String toString(){
        return MODNAME + " " + MODVER;
    }
}
