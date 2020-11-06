package me.finz0.blackout.hud.components;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.finz0.blackout.hud.HudComponent;
import me.finz0.blackout.module.gui.ArmorWarning;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ArmorWarningComponent extends HudComponent<ArmorWarning> {
    public ArmorWarningComponent() {
        super("ArmorWarning", 200, 100, ArmorWarning.INSTANCE);
        width = Wrapper.getFontRenderer().getStringWidth("LOW ARMOR");
        height = Wrapper.getFontRenderer().FONT_HEIGHT;
        MinecraftForge.EVENT_BUS.register(this); // so we can listen for the ClientTickEvent
    }

    private boolean isArmorLow = false;

    @Override
    public void renderInGui(int mouseX, int mouseY) {
        super.renderInGui(mouseX, mouseY);
        if(!isArmorLow && !isInvisible())
            Wrapper.getFontRenderer().drawStringWithShadow("Armor Warning", x, y, -1);
    }

    @Override
    public void render(){
        super.render();
        if(isArmorLow)
            Wrapper.getFontRenderer().drawStringWithShadow(ChatFormatting.BOLD + "LOW ARMOR", x, y, 0xffff0000);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        if(Wrapper.getPlayer() == null || isInvisible()) return;
        isArmorLow = false;
		// check all armor the player is wearing
        for(ItemStack stack : Wrapper.getPlayer().inventory.armorInventory){
			// calculate damage percentage
            double dmg = stack.getItemDamage();
            double max = stack.getMaxDamage();
            if(dmg <= 0d || max <= 0d) continue;

            double percent = dmg / max;
            percent *= 100d; // percentage of the damage, NOT the durability

			// if damage is more than 65% break the loop
            if(percent > 65d){
                isArmorLow = true;
                break;
            }
        }
    }
}
