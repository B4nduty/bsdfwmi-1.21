package banduty.ticktweaks.util;

import banduty.ticktweaks.TickTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class TickHandlerUtil {

    public static boolean validateAndCancelTick(ServerWorld serverWorld, CallbackInfo ci, int specificTickRate, int tickCounter) {
        if (TickRateCalculator.shouldSkipTicking(serverWorld)) return true;

        MinecraftServer server = serverWorld.getServer();
        int tickRate = TickRateCalculator.getCustomTickRate(server, specificTickRate);
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);

        if (tickCounter < tickRate || tps < TickTweaks.CONFIG.stopTick.getEmergencyStopTps()) {
            ci.cancel();
            return false;
        }
        return true;
    }

    public static boolean handleTickCancellation(MinecraftServer server, CallbackInfo ci, boolean isOutsideRadius, int specificTickRate, int tickCounter) {
        int tickRate = isOutsideRadius ? TickTweaks.CONFIG.stopTick.getTickingTimeOnStop() : TickRateCalculator.getCustomTickRate(server, specificTickRate);
        if (tickCounter < tickRate || tickRate == 0) {
            ci.cancel();
            return false;
        }
        return true;
    }

    public static boolean isEntityWithinRadius(Entity entity, World world, int radiusThreshold) {
        double squaredRadius = Math.pow(radiusThreshold, 2);
        return world.getPlayers().stream().noneMatch(player -> player.squaredDistanceTo(entity) <= squaredRadius);
    }

    public static boolean isEntityBlacklisted(Entity entity, List<String> blacklistedEntities) {
        String entityType = entity.getType().toString();
        if (blacklistedEntities.contains(entityType) && !entity.hasPassengers()) return true;

        if (entity instanceof HostileEntity || entity instanceof SlimeEntity) {
            return blacklistedEntities.contains("#minecraft:hostile");
        } else if (entity instanceof Angerable) {
            return blacklistedEntities.contains("#minecraft:neutral");
        } else if (entity instanceof PassiveEntity) {
            return blacklistedEntities.contains("#minecraft:passive");
        }
        return false;
    }
}