package me.finz0.blackout.module.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.command.Command;
import me.finz0.blackout.event.PacketSendEvent;
import me.finz0.blackout.module.Module;
import me.finz0.blackout.util.KillEventHelper;
import me.finz0.blackout.util.RenderUtils;
import me.finz0.blackout.util.Wrapper;
import me.finz0.blackout.setting.Setting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AutoCrystal extends Module {
    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT);
    }

    boolean isActive = false;

    private Setting<Boolean> rotateS = register("Rotate", false);

    private Setting<Double> hitRange = register("HitRange", 6d, 0.0d, 10.0d);
    private Setting<Double> wallsRange = register("HitWallRange", 3.2d, 0.0d, 10.0d, b -> hitRange.getValue() > 0);
    private Setting<Integer> tickDelay = register("HitDelay", 1, 0, 20);
    private Setting<Integer> hitAttempts = register("HitAttempts", 0, 0, 10, b -> hitRange.getValue() > 0);
    private Setting<HitMode> hitMode = register("HitMode", HitMode.Any, b -> hitRange.getValue() > 0);

    public enum HitMode {
        Any, PreferOwn, OnlyOwn
    }

    private Setting<Double> placeRange = register("PlaceRange", 5.5d, 0.0d, 10.0d);
    private Setting<Integer> placeDelay = register("PlaceDelay", 1, 0, 20);
    //private Setting<Double> placeWallRange = register("PlaceWallRg", 3.2d, 0.0d, 10.0d, b -> placeRange.getValue() > 0);
    private Setting<Boolean> autoSwitch = register("AutoSwitch", false, b -> placeRange.getValue() > 0);
    private Setting<Boolean> noGappleSwitch = register("NoGapSwitch", true, b -> autoSwitch.getValue() && autoSwitch.isVisible());
    private Setting<Double> minDmg = register("MinDmg", 4.2d, 0.0d, 20.0d, b -> placeRange.getValue() > 0);
    private Setting<Double> facePlaceHp = register("FacePlaceHP", 8.0d, 0.0d, 20.0d, b -> placeRange.getValue() > 0);
    private Setting<Double> maxSelfDmg = register("MaxSelfDmg", 6.0d, 0.0d, 20.0d, b -> placeRange.getValue() > 0);
    private Setting<Double> enemyRange = register("EnemyRange", 10.0d, 0.0d, 20.0d, b -> placeRange.getValue() > 0);
    private Setting<Boolean> predict = register("Resolver", false, b -> placeRange.getValue() > 0);

    private Setting<Boolean> toggleMsgs = register("ToggleMsgs", true);
    private Setting<Integer> espAlpha = register("EspAlpha", 50, 0, 255);

    private int tickCounter = 0;
    private int placeTickCounter = 0;

    private int attempts = 0; // hit attempts for the HitAttempts setting
    private EntityEnderCrystal lastHitCrystal = null; // for HitAttempts setting

    private List<EntityEnderCrystal> placedCrystals;
    private List<AxisAlignedBB> interacted;

    private BlockPos placePos = null, renderPos = null;

    private boolean switchCooldown = false; // for AutoSwitch

    public EntityPlayer target = null;
    private EntityPlayer possibleTarget = null;
    private EntityPlayer lastTarget = null;

    private boolean lastTickPlaced = false;

    @Override
    public void onUpdate(){
        if(Wrapper.getPlayer() == null || Wrapper.getWorld() == null || Wrapper.getPlayer().getHealth() <= 0 || Wrapper.getPlayer().isDead) return;

        if(!interacted.isEmpty()) {
            for (AxisAlignedBB bb : interacted) {
                for (Entity e : Wrapper.getWorld().getEntitiesWithinAABBExcludingEntity(null, bb)) {
                    if (e instanceof EntityEnderCrystal) {
                        EntityEnderCrystal crystal = (EntityEnderCrystal) e;
                        if(!placedCrystals.contains(crystal)) placedCrystals.add(crystal);
                        break;
                    }
                }
            }
        }

        boolean rotate = rotateS.getValue();
        boolean shouldHit = hitRange.getValue() > 0;

        if(placeRange.getValue() > 0 && (!lastTickPlaced || !shouldHit)){

            if(!switchCooldown) { // if we're not switching to crystals
                placePos = null;
                renderPos = null;
                possibleTarget = null;
                target = null;
                float dmg = 0;
                for (EntityPlayer player : Wrapper.getWorld().playerEntities.stream()
                        .filter(p -> !p.equals(Wrapper.getPlayer()))
                        .filter(p -> Wrapper.getPlayer().getDistance(p) <= enemyRange.getValue())
                        .filter(p -> p.getHealth() > 0).filter(p -> !p.isDead)
                        .filter(p -> Blackout.getInstance().playerStatus.getStatus(p.getName()) != 1) // 1 = friend
                        .sorted(Comparator.comparing(p -> Blackout.getInstance().playerStatus.isEnemyInRange(enemyRange.getValue()) ? Blackout.getInstance().playerStatus.getStatus(p.getName()) : Wrapper.getPlayer().getDistance(p)))
                        .collect(Collectors.toList())) {

                    for (BlockPos pos : findCrystalBlocks()) {
                        double addX = 0;
                        double addZ = 0;

                        if(predict.getValue()) {
                            double x = player.motionX;
                            double z = player.motionZ;
                            if (x >= 0.9 || z >= 0.9) {
                                addX += x*2;
                                addZ += z*2;
                            }
                        }
                        float d = calculateDamage(pos, player, addX, addZ);

                        if (d <= dmg || (d < minDmg.getValue() && player.getHealth() + player.getAbsorptionAmount() > facePlaceHp.getValue()) || calculateDamage(pos, Wrapper.getPlayer()) > maxSelfDmg.getValue())
                            continue;

                        dmg = d;
                        placePos = pos;
                        possibleTarget = player;
                    }
                }
            }

            switchCooldown = false;

            if(placePos != null) {

                // check if the player is holding crystals
                boolean offHand = Wrapper.getPlayer().getHeldItemOffhand().getItem() instanceof ItemEndCrystal;
                boolean mainHand = Wrapper.getPlayer().getHeldItemMainhand().getItem() instanceof ItemEndCrystal;

                // switch to crystals
                if (!offHand && !mainHand && autoSwitch.getValue() && (!noGappleSwitch.getValue() || !isEatingGap())) {
                    for (int i = 0; i < 9; i++) {
                        if (Wrapper.getPlayer().inventory.getStackInSlot(i).getItem() instanceof ItemEndCrystal) {
                            Wrapper.getPlayer().inventory.currentItem = i;
                            switchCooldown = true;
                            return;
                        }
                    }
                }

                if (offHand || mainHand) {

                    if(placeDelay.getValue() != 0 && placeTickCounter < placeDelay.getValue()){
                        placeTickCounter++;
                    } else {
                        placeTickCounter = 0;

                        renderPos = placePos;// set BlockPos to render esp at
                        if (possibleTarget != null) {
                            target = possibleTarget;
                            lastTarget = target;
                            KillEventHelper.INSTANCE.addTarget(target);
                            //KdTrackerAPI.instance.addKillTarget(target);
                        }
                        isActive = true;
                        if (rotate) rotateTo(placePos.getX() + .5, placePos.getY() - .5, placePos.getZ() + .5);
                        Wrapper.getPlayer().connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0)); // place crystal
                        AxisAlignedBB bb = new AxisAlignedBB(placePos.up());
                        interacted.add(bb);
                        //Vec3d vec = new Vec3d(placePos.getX(), placePos.getY(), placePos.getZ());
                        //Wrapper.mc.playerController.processRightClickBlock(Wrapper.getPlayer(), Wrapper.getWorld(), placePos, EnumFacing.UP, vec, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                        if (rotate) resetRotation();
                        lastTickPlaced = true;
                        isActive = false;
                        return;
                    }

                }
            }
        }

        lastTickPlaced = false;

        if(shouldHit) {

            Predicate<? super Entity> predicate;

            switch (hitMode.getValue()) {
                case Any:
                default:
                    predicate = (Predicate<Entity>) entity -> true;
                    break;
                case OnlyOwn:
                    predicate = (Predicate<Entity>) entity -> placedCrystals.contains((EntityEnderCrystal) entity);
                    break;
                case PreferOwn:
                    predicate = (Predicate<Entity>) entity -> !placedValidCrystal() || placedCrystals.contains((EntityEnderCrystal) entity);
                    break;
            }

            EntityEnderCrystal crystal = (EntityEnderCrystal) Wrapper.getWorld().loadedEntityList.stream() // stream all entities in render distance
                    .filter(e -> e instanceof EntityEnderCrystal) // ignore if its not a crystal
                    .filter(predicate)
                    .filter(e -> isValid((EntityEnderCrystal) e))
                    .filter(e -> hitAttempts.getValue() <= 0 || attempts < hitAttempts.getValue() || lastHitCrystal == null || e != lastHitCrystal) // HitAttempts check
                    .min(Comparator.comparing(e -> lastTarget == null || lastTarget.getHealth() <= 0 || lastTarget.isDead ? Wrapper.getPlayer().getDistance(e) : lastTarget.getDistance(e))).orElse(null); // get closest

            if(crystal != null){

                if(tickDelay.getValue() != 0){
                    if(tickCounter >= tickDelay.getValue()){
                        tickCounter = 0;
                    } else {
                        tickCounter++;
                        return;
                    }
                }

                isActive = true;
                if(rotate) rotateTo(crystal); // rotate to the crystal

                Wrapper.mc.playerController.attackEntity(Wrapper.getPlayer(), crystal);
                Wrapper.getPlayer().swingArm(EnumHand.MAIN_HAND);

                if (lastHitCrystal == crystal) {
                    attempts++;
                } else {
                    lastHitCrystal = crystal;
                    attempts = 1;
                }

                if(rotate) resetRotation();
                isActive = false;
                //return; // return so we don't try to hit and place on the same tick
            }
        }
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PacketSendEvent event){
        if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock){
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();

            AxisAlignedBB bb = new AxisAlignedBB(packet.getPos().up());
            interacted.add(bb);
        }
    }

    @Override
    public void onRender3D(){
        if(espAlpha.getValue() > 0 && Wrapper.getPlayer() != null && Wrapper.getWorld() != null){
            if(target != null)
                RenderUtils.INSTANCE.drawBoundingBox(target.getRenderBoundingBox(), Blackout.getInstance().clientSettings.getColor(espAlpha.getValue()), 1f);
            if(renderPos != null)
                RenderUtils.INSTANCE.drawBox(renderPos, Blackout.getInstance().clientSettings.getColor(espAlpha.getValue()));
        }
    }

    @Override
    public void onEnable(){
		// reset variables
        attempts = 0;
        tickCounter = tickDelay.getValue();
        placeTickCounter = placeDelay.getValue();
        lastHitCrystal = null;
        placePos = null;
        renderPos = null;
        switchCooldown = false;
        possibleTarget = null;
        target = null;
        lastTarget = null;
        isActive = false;
        placedCrystals = new ArrayList<>();
        interacted = new ArrayList<>();

        if(toggleMsgs.getValue())
            Command.sendClientMessage(getName() + ChatFormatting.GREEN + " ON", false);
    }

    @Override
    public void onDisable(){
		// reset variables
        attempts = 0;
        tickCounter = tickDelay.getValue();
        placeTickCounter = placeDelay.getValue();
        lastHitCrystal = null;
        placePos = null;
        renderPos = null;
        switchCooldown = false;
        possibleTarget = null;
        target = null;
        lastTarget = null;
        isActive = false;
        placedCrystals = null;
        interacted = null;

        if(toggleMsgs.getValue())
            Command.sendClientMessage(getName() + ChatFormatting.RED + " OFF", false);
    }

    private boolean isValid(EntityEnderCrystal crystal){
        boolean a = crystal != null && !crystal.isDead;
        return a && (Wrapper.getPlayer().canEntityBeSeen(crystal) ? Wrapper.getPlayer().getDistance(crystal) <= hitRange.getValue() : Wrapper.getPlayer().getDistance(crystal) <= wallsRange.getValue());
    }

    private boolean placedValidCrystal(){
        if(placedCrystals.isEmpty()) return false;

        for(EntityEnderCrystal crystal : placedCrystals){
            if(isValid(crystal)) return true;
        }
        return false;
    }

    private boolean isEatingGap(){
        return Wrapper.getPlayer().getHeldItemMainhand().getItem() instanceof ItemAppleGold && Wrapper.getPlayer().isHandActive();
    }

    private void rotateTo(double x, double y, double z) {
        Blackout.getInstance().rotationManager.rotate(x, y, z);
    }

    private void rotateTo(Entity target){
        rotateTo(target.posX, target.posY, target.posZ);
    }

    private void resetRotation(){
        Blackout.getInstance().rotationManager.reset();
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (Wrapper.getWorld().getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || Wrapper.getWorld().getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                && Wrapper.getWorld().getBlockState(boost).getBlock() == Blocks.AIR
                && Wrapper.getWorld().getBlockState(boost2).getBlock() == Blocks.AIR
                && Wrapper.getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                && Wrapper.getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    private BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Wrapper.getPlayer().posX), Math.floor(Wrapper.getPlayer().posY), Math.floor(Wrapper.getPlayer().posZ));
    }

    /*
    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange.getValue().floatValue(), placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    private static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }
     */

    private List<BlockPos> findCrystalBlocks(){
        NonNullList<BlockPos> circleblocks = NonNullList.create();

        BlockPos loc = getPlayerPos();
        float r = placeRange.getValue().floatValue();
        //int h = placeRange.getValue().intValue();
        //int plus_y = 0;

        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (cy - (int) r); y < (cy + r); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));
                    if (dist < r * r) {
                        BlockPos l = new BlockPos(x, y, z);
                        if(canPlaceCrystal(l)) {
                            //EntityPlayer p = Wrapper.getPlayer();
                            //boolean visible = Wrapper.getWorld().rayTraceBlocks(new Vec3d(p.posX, p.posY + (double)p.getEyeHeight(), p.posZ), new Vec3d(l.getX(), l.getY(), l.getZ()), false, true, false) == null;
                            //if(!visible || p.getDistance(l.getX(), l.getY(), l.getZ()) <= placeWallRange.getValue())
                                circleblocks.add(l);
                        }
                    }
                }
            }
        }
        return circleblocks;
    }

    private float calculateDamage(double posX, double posY, double posZ, Entity entity, double addX, double addZ) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = getDistance(entity.posX + addX, entity.posY, entity.posZ + addZ, posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;
        /*if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase) entity,getDamageMultiplied(damage));*/
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(Wrapper.getWorld(), null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }
    
    private double getDistance(double x, double y, double z, double x2, double y2, double z2){
        double d0 = x - x2;
        double d1 = y - y2;
        double d2 = z - z2;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    private float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
            //   damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private float getDamageMultiplied(float damage) {
        int diff = Wrapper.getWorld().getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    //private float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
    //    return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    //}

    private float calculateDamage(BlockPos blockPos, Entity entity){
        return calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, entity, 0, 0);
    }

    private float calculateDamage(BlockPos blockPos, Entity entity, double addX, double addZ){
        return calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, entity, addX, addZ);
    }
}
