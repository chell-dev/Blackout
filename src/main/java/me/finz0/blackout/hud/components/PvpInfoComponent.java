package me.finz0.blackout.hud.components;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.hud.HudComponent;
import me.finz0.blackout.managers.ModuleManager;
import me.finz0.blackout.module.gui.PvpInfo;
import me.finz0.blackout.util.Wrapper;

public class PvpInfoComponent extends HudComponent<PvpInfo> {
    public PvpInfoComponent() {
        super("PvpInfo", 2, 200, PvpInfo.INSTANCE);

        /*
        caOff = new ResourceLocation(Blackout.MODID, "textures/caoff.png");
        caOn = new ResourceLocation(Blackout.MODID, "textures/caon.png");

        suOff = new ResourceLocation(Blackout.MODID, "textures/suoff.png");
        suOn = new ResourceLocation(Blackout.MODID, "textures/suon.png");

        atOff = new ResourceLocation(Blackout.MODID, "textures/atoff.png");
        atOn = new ResourceLocation(Blackout.MODID, "textures/aton.png");

        stepOff = new ResourceLocation(Blackout.MODID, "textures/stepoff.png");
        stepOn = new ResourceLocation(Blackout.MODID, "textures/stepon.png");
         */
    }

    //private ResourceLocation caOff, caOn, suOff, suOn, atOff, atOn, stepOff, stepOn;
    //private int xx;

    @Override
    public void render() {
        super.render();
        ModuleManager mngr = Blackout.getInstance().moduleManager;
        String s = Blackout.getInstance().moduleManager.getEnabledColor("AutoCrystal") + "CA  " + Blackout.getInstance().moduleManager.getEnabledColor("Surround") + "SU  " + Blackout.getInstance().moduleManager.getEnabledColor("AutoTrap") + "AT  " + Blackout.getInstance().moduleManager.getEnabledColor("Step") + "ST";
        Wrapper.getFontRenderer().drawStringWithShadow(s, x, y, -1);
        width = Wrapper.getFontRenderer().getStringWidth(s);
        /*
        xx = x;

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();

        Wrapper.mc.renderEngine.bindTexture(mngr.getModuleByName("AutoCrystal").isEnabled() ? caOn : caOff);
        Wrapper.mc.ingameGUI.drawTexturedModalRect(xx, y - 1, 0, 0, 16, 16);
        xx += 16;

        draw(mngr.getModuleByName("Surround").isEnabled() ? suOn : suOff);
        xx += 16;

        draw(mngr.getModuleByName("AutoTrap").isEnabled() ? atOn : atOff);
        xx += 16;

        draw(mngr.getModuleByName("Step").isEnabled() ? stepOn : stepOff);
        xx += 16;

        GlStateManager.disableAlpha();
         */
    }

    /*
    private void draw(ResourceLocation resourceLocation){
        Wrapper.mc.renderEngine.bindTexture(resourceLocation);
        Wrapper.mc.ingameGUI.drawTexturedModalRect(xx, y, 0, 0, 16, 16);
    }
     */
}
