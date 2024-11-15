package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickRateCalculator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(World.class)
public abstract class WorldMixin {
    @Unique
    private int blockEntitiesTickCounter = 0;
    @Unique
    private int entitiesTickCounter = 0;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void ticktweaks$onTickBlockEntities(CallbackInfo ci) {
        World world = (World) (Object) this;
        if (!(world instanceof ServerWorld serverWorld) || TickRateCalculator.shouldSkipTicking(world)) return;

        MinecraftServer server = serverWorld.getServer();
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);

        if (tps < TickTweaks.CONFIG.stopTick.getEmergencyStopTps()) {
            ci.cancel();
            return;
        }

        if (TickTweaks.CONFIG.enableCustomTick.changeTickRateBlockEntities) {
            blockEntitiesTickCounter++;
            int customTickRate = TickRateCalculator.getCustomTickRate(server, TickTweaks.CONFIG.tickRateTime.getSpecificTickRateBlockEntities());

            if (blockEntitiesTickCounter < customTickRate) {
                ci.cancel();
            } else {
                blockEntitiesTickCounter = 0;
            }
        }
    }

    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void ticktweaks$onTickEntity(Consumer<T> tickConsumer, T entity, CallbackInfo ci) {
        World world = (World) (Object) this;
        if (!(world instanceof ServerWorld serverWorld) || TickRateCalculator.shouldSkipTicking(world) || entity instanceof PlayerEntity) return;

        MinecraftServer server = serverWorld.getServer();
        entitiesTickCounter++;

        boolean changeTickRateItemEntities = TickTweaks.CONFIG.enableCustomTick.changeTickRateItemEntities;
        int tickingTimeOnStop = TickTweaks.CONFIG.stopTick.getTickingTimeOnStop();
        int radiusThreshold = TickTweaks.CONFIG.stopTick.getStopTickingDistance();
        List<String> blacklistedMobs = TickTweaks.CONFIG.stopTick.getStopBlacklist();
        List<String> blacklistedLivingEntities = TickTweaks.CONFIG.enableCustomTick.getBlacklistedLivingEntities();

        boolean isOutsideRadius = !blacklistedMobs.contains(entity.getType().toString())
                && !isWithinRadiusOfPlayer(entity, world, radiusThreshold);

        if (entity instanceof ItemEntity && changeTickRateItemEntities) {
            handleCustomTickRate(server, ci, isOutsideRadius, tickingTimeOnStop,
                    TickTweaks.CONFIG.tickRateTime.getSpecificTickRateItemEntities());
        } else if (entity instanceof LivingEntity) {
            if (isLivingEntityBlacklisted(entity, blacklistedLivingEntities)) return;
            handleCustomTickRate(server, ci, isOutsideRadius, tickingTimeOnStop,
                    TickTweaks.CONFIG.tickRateTime.getSpecificTickRateLivingEntities());
        } else {
            entitiesTickCounter = 0;
        }
    }

    @Unique
    private boolean isWithinRadiusOfPlayer(Entity entity, World world, int radiusThreshold) {
        double squaredRadius = Math.pow(radiusThreshold, 2);
        return world.getPlayers().stream()
                .anyMatch(player -> player.squaredDistanceTo(entity) <= squaredRadius);
    }

    @Unique
    private void handleCustomTickRate(MinecraftServer server, CallbackInfo ci, boolean isOutsideRadius, int tickingTimeOnStop, int specificTickRate) {
        int tickRate = TickRateCalculator.getCustomTickRate(server, specificTickRate);
        int customTickRate = isOutsideRadius && TickTweaks.CONFIG.stopTick.getStopTickingDistance() > 0 ? tickingTimeOnStop : tickRate;

        if (entitiesTickCounter < customTickRate || customTickRate == 0) {
            ci.cancel();
        }
        entitiesTickCounter = 0;
    }

    @Unique
    private boolean isLivingEntityBlacklisted(Entity entity, List<String> blacklistedLivingEntities) {
        String entityType = entity.getType().toString();
        if (blacklistedLivingEntities.contains(entityType) && !entity.hasPassengers()) return true;
        if (entity instanceof HostileEntity || entity instanceof SlimeEntity) {
            return blacklistedLivingEntities.contains("#minecraft:hostile");
        } else if (entity instanceof Angerable) {
            return blacklistedLivingEntities.contains("#minecraft:neutral");
        } else if (entity instanceof PassiveEntity) {
            return blacklistedLivingEntities.contains("#minecraft:passive");
        }
        return false;
    }
}