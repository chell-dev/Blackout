package me.finz0.blackout.module.render;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HitMarkers extends Module {
    public HitMarkers() {
        super("HitMarkers", Category.RENDER);
    }

    private Setting<Integer> setting = register("AliveTime", 10, 1, 20);
    //private Setting<Integer> x = register("X", 10, -1000, 1000); // debug
    //private Setting<Integer> y = register("Y", 10, -1000, 1000); // debug

    private int aliveTime = 0;
    private ScaledResolution res;
    private final ResourceLocation texture = new ResourceLocation("blackout", "textures/hitmarker.png");

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event){
        if(event.getEntity() == Wrapper.getPlayer())
            aliveTime = setting.getValue();
    }

    @Override
    public void onUpdate(){
        if(aliveTime > 0) {
            aliveTime--;
            res = new ScaledResolution(Wrapper.mc);
        }
    }

    @Override
    public void postRender2D(){
        //if(res == null) return;
        //int x = res.getScaledWidth() / 2;
        //int y = res.getScaledHeight() / 2;

        //GlStateManager.pushMatrix();
        //GlStateManager.rotate(45, 0, 0, 1f);

        //Gui.drawRect(x - 7, y, x - 1, y + 1, -1);
        //Gui.drawRect(x.getValue() - 7, y.getValue(), x.getValue() - 1, y.getValue() + 1, -1);

        //GlStateManager.popMatrix();

        if(aliveTime > 0 && res != null){
            GlStateManager.pushMatrix();
            //GlStateManager.rotate(45, 0, 0, 1f);
            GlStateManager.enableAlpha();
            Wrapper.mc.getTextureManager().bindTexture(texture);
            GlStateManager.color(1f, 1f, 1f,1f);
            //Wrapper.mc.ingameGUI.drawTexturedModalRect((res.getScaledWidth() / 2) - 8, (res.getScaledHeight() / 2) - 8, 0, 0, 16, 16);
            // why no worky :(
            Wrapper.mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 16, 16);
            GlStateManager.popMatrix();
        }
    }
}
