package me.finz0.blackout.mixin.mixins;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(GuiNewChat.class)
@SideOnly(Side.CLIENT)
public class MixinGuiNewChat extends Gui {
    @Shadow
    @Final
    private static final Logger LOGGER = LogManager.getLogger();

    private Minecraft mc = Wrapper.mc;

    @Shadow
    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly){}

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void chatBkg(int left, int top, int right, int bottom, int color){
        if(!Blackout.getInstance().moduleManager.getModuleByName("Chat").isEnabled())
            Gui.drawRect(left, top, right, bottom, color);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int highlightName(FontRenderer fontRenderer, String text, float x, float y, int color){
        boolean b = Blackout.getInstance().moduleManager.getModuleByName("Chat").isEnabled()
                    && Wrapper.getPlayer() != null
                    && text.toLowerCase().contains(Wrapper.getPlayer().getName().toLowerCase())
                    && text.length() > 11
                    && !text.substring(11).startsWith(Wrapper.getPlayer().getName()); // substring = timestamp and '<'
        return fontRenderer.drawStringWithShadow(b ? ChatFormatting.BOLD + text : text, x, y, b ? Blackout.getInstance().clientSettings.getColor() : color);
    }

	// chat timestamps, also not perfect
    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"), cancellable = true)
    private void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId, CallbackInfo ci){
        if(!Blackout.getInstance().moduleManager.getModuleByName("Chat").isEnabled()) return;

        ci.cancel();

        String time = new SimpleDateFormat("HH:mm ").format(new Date());
        ITextComponent orig = chatComponent;
        chatComponent = new TextComponentString(time).appendSibling(orig);

        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", (Object)chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
}
