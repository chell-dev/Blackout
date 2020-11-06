package me.finz0.blackout.mixin.mixins;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.render.ViewModel;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityRenderer.class, priority = Integer.MAX_VALUE)
public class MixinEntityRenderer {

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemInFirstPerson(F)V"))
    public void renderItemInFirstPerson(ItemRenderer itemRenderer, float partialTicks){
        //ViewModel mod = (ViewModel) Blackout.getInstance().moduleManager.getModuleByName("ViewModel");
        //if(mod.isEnabled())
            //GlStateManager.scale(0.1f, 0.1f, 0.1f);
            //GlStateManager.enableOutlineMode(-1);
        //GlStateManager.colorMask(true, false, true, true);
        //if(mod.isEnabled())
        //    GlStateManager.disableOutlineMode();
        //itemRenderer.renderItemInFirstPerson(partialTicks);

        //if(mod.isEnabled())
            //GlStateManager.colorMask(true, true, true, true);
    }

}
