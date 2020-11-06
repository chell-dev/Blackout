package me.finz0.blackout.mixin.mixins;

import me.finz0.blackout.Blackout;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public class MixinGuiChat extends GuiScreen {
    @Shadow
    protected GuiTextField inputField;

    @Inject(method = "drawScreen", at= @At("HEAD"), cancellable = true)
    private void drawOutline(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        if(inputField.getText().startsWith(Blackout.getInstance().clientSettings.getPrefix())){
            int color = Blackout.getInstance().clientSettings.getColor(155);
            //drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
            int x = 2;
            int y = height - 14;
            int w = width - 2;
            int h = height - 2;
            Gui.drawRect(x - 1, y, x, h, color); // left
            Gui.drawRect(w, y, w + 1, h, color); // right
            Gui.drawRect(x - 1, y - 1, w + 1, y, color); // top
            Gui.drawRect(x - 1, h, w + 1, h + 1, color); // bottom
        }
    }
}
