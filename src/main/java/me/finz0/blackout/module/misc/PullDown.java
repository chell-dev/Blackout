package me.finz0.blackout.module.misc;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class PullDown extends Module {
    public PullDown() {
        super("PullDown", Category.MISC);
    }

    private final Setting<Integer> maxHeight = register("MaxHeight", 3, -1, 10);

    @Override
    public void onUpdate(){
        if(Wrapper.getPlayer() == null || Wrapper.getWorld() == null) return;

        // get block in front of player
        EnumFacing facing = Wrapper.getPlayer().moveForward < 0 ? Wrapper.getPlayer().getHorizontalFacing().getOpposite() : Wrapper.getPlayer().getHorizontalFacing();
        Vec3d vec3d = new Vec3d(Wrapper.getPlayer().getPosition().offset(facing));

        // check if there's a hole in front of the player
        RayTraceResult ray = Wrapper.getWorld().rayTraceBlocks(vec3d, vec3d.subtract(0, 3, 0), true);
        boolean b = ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK;

        // go down if the player is not in a liquid or web
        if(Wrapper.getPlayer().onGround && b && !Wrapper.getPlayer().isInWater() && !Wrapper.getPlayer().isInLava() && !Wrapper.getPlayer().isInsideOfMaterial(Material.WEB) )
            Wrapper.getPlayer().motionY = -3;
    }
}
