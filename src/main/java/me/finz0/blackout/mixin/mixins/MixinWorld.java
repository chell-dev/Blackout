package me.finz0.blackout.mixin.mixins;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.render.SkyColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class MixinWorld {

    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getSkyColor(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d setSkyColor(WorldProvider worldProvider, Entity cameraEntity, float partialTicks){
        SkyColor mod = (SkyColor) Blackout.getInstance().moduleManager.getModuleByName("SkyColor");

        if(mod.isEnabled()){
            return mod.getColor();
        } else {
            return worldProvider.getSkyColor(cameraEntity, partialTicks);
        }
    }

}
