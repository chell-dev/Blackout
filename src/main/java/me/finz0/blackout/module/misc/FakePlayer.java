package me.finz0.blackout.module.misc;

import com.mojang.authlib.GameProfile;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class FakePlayer extends Module {
    public FakePlayer() {
        super("FakePlayer", Category.MISC);
    }

    public void onUpdate(){
        if(Wrapper.getWorld() == null || Wrapper.getPlayer() == null) return;
        Entity entity = Wrapper.getWorld().getEntityByID(58310);
        if(entity instanceof EntityOtherPlayerMP) {
            EntityOtherPlayerMP player = (EntityOtherPlayerMP) entity;
            Vec3d v = Wrapper.getPlayer().getPositionVector();
            Double[] d = Blackout.getInstance().rotationManager.calculateLookAt(v.x, v.y, v.z, player);
            player.rotationYawHead = d[0].floatValue();
            player.rotationPitch = d[1].floatValue();
        }
    }

    public void onEnable(){
        if(Wrapper.getWorld() == null || Wrapper.getPlayer() == null) return;
        EntityOtherPlayerMP entity = new EntityOtherPlayerMP(Wrapper.getWorld(), new GameProfile(null, "shitass"));
        entity.copyLocationAndAnglesFrom(Wrapper.getPlayer());

        Wrapper.getWorld().addEntityToWorld(58310, entity);
    }

    public void onDisable(){
        if(Wrapper.getWorld() == null || Wrapper.getWorld().getEntityByID(58310) == null) return;
        Wrapper.getWorld().removeEntityFromWorld(58310);
    }
}
