package me.finz0.blackout.module.render;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ViewModel extends Module {

    public ViewModel() {
        super("ViewModel", Category.RENDER);
    }

    public final Setting<Integer> fov = register("FOV", 90, 0, 200);
    public final Setting<Boolean> item = register("Item", false);
    public final Setting<Boolean> texture = register("Texture", true);
    public final Setting<Integer> red = register("Red", 255, 0, 255, b -> !texture.getValue());
    public final Setting<Integer> green = register("Green", 255, 0, 255, b -> !texture.getValue());
    public final Setting<Integer> blue = register("Blue", 255, 0, 255, b -> !texture.getValue());
    public final Setting<Integer> alpha = register("Alpha", 255, 0, 255);

    public enum Mode {
        TEXTURE, COLOR, WF
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event){
        //GlStateManager.colorMask(true, true, true, true);
        //RenderUtils.INSTANCE.prepare();
        //GlStateManager.enableOutlineMode(-1);
        //GlStateManager.disableColorMaterial();
        //GL11.glColor4f(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f, alpha.getValue() / 255f);
        //GlStateManager.enableCull();
        //if(texture.getValue())
            GlStateManager.enableTexture2D();
    }

}
