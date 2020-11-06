package me.finz0.blackout.mixin.mixins;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.misc.ChorusTweaks;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemChorusFruit.class)
public class MixinItemChorusFruit extends ItemFood {

    public MixinItemChorusFruit(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    @Inject(method = "onItemUseFinish", at = @At("HEAD"), cancellable = true)
    private void onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> cir){
        ChorusTweaks mod = (ChorusTweaks) Blackout.getInstance().moduleManager.getModuleByName("ChorusTweaks");
        if(mod == null || !mod.isEnabled() || entityLiving != Wrapper.getPlayer()) return;
        cir.cancel();

        ItemStack itemstack = super.onItemUseFinish(stack, worldIn, entityLiving);

        if (!worldIn.isRemote) {
            double d0 = entityLiving.posX;
            double d1 = entityLiving.posY;
            double d2 = entityLiving.posZ;

            LOOP: for (int i = 0; i < 16; ++i)
            {
                if (entityLiving.isRiding())
                {
                    entityLiving.dismountRidingEntity();
                }

                double d3;
                double d4;
                double d5;

                SW: switch (mod.tpLocation.getValue()){
                    case PLAYER:
                        EntityPlayer closest = Wrapper.getPlayer();

                        for(EntityPlayer player : worldIn.playerEntities){
                            float dist = Wrapper.getPlayer().getDistance(player);
                            if(dist >= 16) continue;
                            if(closest == Wrapper.getPlayer() || dist < Wrapper.getPlayer().getDistance(closest))
                                closest = player;
                        }

                        d3 = closest.posX;
                        d4 = closest.posY;
                        d5 = closest.posZ;

                        break SW;
                    case FRIEND:
                        EntityPlayer closestest = Wrapper.getPlayer();

                        for(EntityPlayer player : worldIn.playerEntities){
                            float dist = Wrapper.getPlayer().getDistance(player);
                            if(dist >= 16 || Blackout.getInstance().playerStatus.getStatus(player.getName()) != 1) continue;
                            if(closestest == Wrapper.getPlayer() || dist < Wrapper.getPlayer().getDistance(closestest))
                                closestest = player;
                        }

                        d3 = closestest.posX;
                        d4 = closestest.posY;
                        d5 = closestest.posZ;

                        break SW;
                    case UP:
                        for(int j = 0; j < 5; ++j){
                            double x = Wrapper.getPlayer().posX;
                            double y = Wrapper.getPlayer().posY;
                            double z = Wrapper.getPlayer().posZ;

                            if (entityLiving.attemptTeleport(x, y + j, z))
                            {
                                if(!mod.noSound.getValue()) {
                                    worldIn.playSound((EntityPlayer) null, d0, d1, d2, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                    entityLiving.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                                }

                                if(!mod.noCooldown.getValue())
                                    ((EntityPlayer)entityLiving).getCooldownTracker().setCooldown(this, 20);

                                cir.setReturnValue(itemstack);
                                return;
                            }
                        }
                    case RANDOM:
                    default:
                        d3 = entityLiving.posX + (entityLiving.getRNG().nextDouble() - 0.5D) * 16.0D;
                        d4 = MathHelper.clamp(entityLiving.posY + (double)(entityLiving.getRNG().nextInt(16) - 8), 0.0D, (double)(worldIn.getActualHeight() - 1));
                        d5 = entityLiving.posZ + (entityLiving.getRNG().nextDouble() - 0.5D) * 16.0D;
                        break SW;
                }

                if (entityLiving.attemptTeleport(d3, d4, d5))
                {
                    if(!mod.noSound.getValue()) {
                        worldIn.playSound((EntityPlayer) null, d0, d1, d2, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        entityLiving.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                    }

                    if(!mod.noCooldown.getValue())
                        ((EntityPlayer)entityLiving).getCooldownTracker().setCooldown(this, 20);

                    cir.setReturnValue(itemstack);
                    return;
                }

            }


        }

        cir.setReturnValue(itemstack);
    }
}
