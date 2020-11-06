package me.finz0.blackout.mixin.mixins;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.render.SkyColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    public void setSkyColor(Entity entityIn, float partialTicks, CallbackInfoReturnable<Vec3d> cir){
        SkyColor mod = (SkyColor) Blackout.getInstance().moduleManager.getModuleByName("SkyColor");

        if(mod.isEnabled()){
            cir.cancel();
            cir.setReturnValue(mod.getColor());
        }
    }

}
