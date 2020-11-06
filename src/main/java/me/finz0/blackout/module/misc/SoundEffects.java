package me.finz0.blackout.module.misc;

import com.google.common.collect.Lists;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.event.PlayerKillEvent;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;

public class SoundEffects extends Module {
    public SoundEffects() {
        super("SoundEffects", Category.MISC);

        Blackout.getInstance().log.info("[SoundEffects] Registering Sounds");

        effects = Lists.newArrayList(
                registerS("godlike"),
                registerS("holyshit"),
                registerS("impressive"),
                registerS("ownage"),
                registerS("perfect"),
                registerS("wickedsick")
        );
		//hitmarker = registerS("skeet");

        Blackout.getInstance().log.info("[SoundEffects] Successfully Registered Sounds");
    }

    private final List<SoundEvent> effects;
	//private final SoundEvent hitmarker;
	
	private final Setting<Boolean> kills = register("Kills", true);
	//private final Setting<Boolean> hit = register("HitMarker", true);
	//private final Setting<Integer> hitVolume = register("HitVolume", 5, 1, 10);

    @SubscribeEvent
    public void onKill(PlayerKillEvent event){
        if(Wrapper.getPlayer() != null && kills.getValue()) Wrapper.getPlayer().playSound(randomSound(), 1f, 1f);
    }

    /*
    private Entity lastHitEntity = null;
    private Float lastHitEntityHp = null;

    @SubscribeEvent
    public void preAttackEntity(PacketSendEvent event){
        if(Wrapper.getPlayer() == null || Wrapper.getWorld() == null || event instanceof PacketSendEvent.Post) return;
        if(!(event.getPacket() instanceof CPacketUseEntity)) return;

        CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
        if(!packet.getAction().equals(CPacketUseEntity.Action.ATTACK)) return;

        Entity target = packet.getEntityFromWorld(Wrapper.getWorld());

        lastHitEntity = target;

        if(target instanceof EntityLiving){
            lastHitEntityHp = ((EntityLiving) target).getHealth();
        }
    }

    @SubscribeEvent
    public void postAttackEntity(PacketSendEvent.Post event){
        if(Wrapper.getPlayer() == null || Wrapper.getWorld() == null) return;
        if(!(event.getPacket() instanceof CPacketUseEntity)) return;

        CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
        if(!packet.getAction().equals(CPacketUseEntity.Action.ATTACK)) return;

        Entity target = packet.getEntityFromWorld(Wrapper.getWorld());
        if(target == null || target != lastHitEntity) return;

        if(target instanceof EntityLiving){
            if(target.isDead || ((EntityLiving) target).getHealth() < lastHitEntityHp)
                Wrapper.getPlayer().playSound(hitmarker, hitVolume.getValue() / 10f, (float)Wrapper.randomDouble(0.99, 1));
        } else {
            if(target.isDead)
                Wrapper.getPlayer().playSound(hitmarker, hitVolume.getValue() / 10f, (float)Wrapper.randomDouble(0.99, 1));
        }
    }
     */

    private SoundEvent randomSound(){
        return effects.get(Wrapper.randomInt(0, effects.size()));
    }

    private SoundEvent registerS(String name){
        SoundEvent soundEvent = new SoundEvent(new ResourceLocation(Blackout.MODID, name));
        soundEvent.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(soundEvent);
        return soundEvent;
    }
}
