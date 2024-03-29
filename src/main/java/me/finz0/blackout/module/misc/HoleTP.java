package me.finz0.blackout.module.misc;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.module.render.HoleESP;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleTP extends Module {
    public HoleTP() {
        super("HoleTP", Category.MISC);
    }

    public void onUpdate(){
        if(Wrapper.getPlayer() == null || Wrapper.getWorld() == null) return;
        if(isHole(getPlayerPos().down()) && Wrapper.getPlayer().motionY <= 0 && Wrapper.getPlayer().fallDistance <= 1 && !Wrapper.mc.gameSettings.keyBindJump.isKeyDown()){
            Wrapper.getPlayer().addVelocity(0, -3, 0);
        }
    }

    private boolean isHole(BlockPos blockPos){
        HoleESP holeESP = (HoleESP) Blackout.getInstance().moduleManager.getModuleByName("HoleESP");
        return holeESP.airCheck(blockPos) && (holeESP.isBedrock(blockPos) || holeESP.isObby(blockPos));
    }

    private BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Wrapper.getPlayer().posX), Math.floor(Wrapper.getPlayer().posY), Math.floor(Wrapper.getPlayer().posZ));
    }
}
