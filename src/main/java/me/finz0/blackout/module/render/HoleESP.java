package me.finz0.blackout.module.render;

import me.finz0.blackout.module.Module;
import me.finz0.blackout.setting.ColorValue;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.RenderUtils;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;

public class HoleESP extends Module {
    public HoleESP() {
        super("HoleESP", Category.RENDER);
    }

    private HashMap<BlockPos, Boolean> holes = new HashMap<>();

    private Setting<Integer> range = register("Range", 10, 1, 20);
    private Setting<ColorValue> bedrockColor = register("Bedrock", new ColorValue(0, 255, 0));
    private Setting<ColorValue> obbyColor = register("Obby", new ColorValue(255, 0, 0));
    private Setting<Integer> alpha = register("Alpha", 27, 27, 255);

    @Override
    public void onUpdate() {
        if(Wrapper.getPlayer() == null || Wrapper.getWorld() == null) return;
        holes = new HashMap<>();
        findHoles();
    }

    @Override
    public void onRender3D() {
        if(!holes.isEmpty() && Wrapper.getPlayer() != null && Wrapper.getWorld() != null) {
            holes.forEach((blockPos, isBedrock) -> {
                Color c = isBedrock ? bedrockColor.getValue().getColor() : obbyColor.getValue().getColor();
                RenderUtils.INSTANCE.drawBox(blockPos, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, alpha.getValue() / 255f);
            });
        }
    }

    public boolean airCheck(BlockPos blockPos){
        BlockPos[] airBlocks = new BlockPos[]{
                blockPos, blockPos.add(0, 1, 0), blockPos.add(0, 2, 0)
        };

        for(BlockPos pos : airBlocks){
            if(!Wrapper.getWorld().getBlockState(pos).getMaterial().isReplaceable()) return false;
        }

        return true;
    }

    public boolean isBedrock(BlockPos blockPos) {
        BlockPos[] sides = new BlockPos[]{
                blockPos.down(), blockPos.north(), blockPos.south(), blockPos.west(), blockPos.east()
        };

        for(BlockPos pos : sides) {
            if(Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.BEDROCK) return false;
        }

        return true;
    }

    public boolean isObby(BlockPos blockPos) {
        BlockPos[] sides = new BlockPos[]{
                blockPos.down(), blockPos.north(), blockPos.south(), blockPos.west(), blockPos.east()
        };

        for(BlockPos pos : sides) {
            if(Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.BEDROCK && !obbyOrEchest(pos)) return false;
        }

        return true;
    }

    private boolean obbyOrEchest(BlockPos blockPos) {
        if(Wrapper.getWorld() == null) return false;
        return Wrapper.getWorld().getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN || Wrapper.getWorld().getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST;
    }

    //credit: 086
    private void findHoles(){
        if(Wrapper.mc.getRenderViewEntity() == null) return;

        BlockPos loc = Wrapper.mc.getRenderViewEntity().getPosition();
        float r = range.getValue().floatValue();
        int plus_y = 0;

        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = cy - (int) r; y < cy + r; y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (cy - y) * (cy - y);
                    if (dist < r * r) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        if(airCheck(l)){
                            if(isBedrock(l)) holes.put(l, true);
                            else if(isObby(l)) holes.put(l, false);
                        }
                    }
                }
            }
        }
    }
}
