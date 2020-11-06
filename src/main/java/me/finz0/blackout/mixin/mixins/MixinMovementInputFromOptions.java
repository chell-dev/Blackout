package me.finz0.blackout.mixin.mixins;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MovementInputFromOptions.class, priority = Integer.MAX_VALUE)
public class MixinMovementInputFromOptions {
    @Redirect(method = "updatePlayerMoveState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
    private boolean isKeyDown(KeyBinding keyBinding){
        if(Wrapper.mc.currentScreen != null && !(Wrapper.mc.currentScreen instanceof GuiChat) && Blackout.getInstance().moduleManager.getModuleByName("GuiMove").isEnabled())
            return Keyboard.isKeyDown(keyBinding.getKeyCode());
        return keyBinding.isKeyDown();
    }
}
