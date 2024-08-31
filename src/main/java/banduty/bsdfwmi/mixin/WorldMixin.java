package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(World.class)
public abstract class WorldMixin {
    @Unique
    private int blockEntitiesTickCounter = 0;
    @Unique
    private int entitiesTickCounter = 0;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void bsDFWMI$onTickBlockEntities(CallbackInfo ci) {
        World world = (World)(Object)this;
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        if (BsDFWMI.CONFIG.common.getTickRateBlockEntities) {
            blockEntitiesTickCounter++;

            MinecraftServer server = serverWorld.getServer();
            if (server != null) {
                final int CUSTOM_TICK_RATE = getCustomTickRateBlockEntities(server);

                blockEntitiesTickCounter++;
                if (blockEntitiesTickCounter < CUSTOM_TICK_RATE) {
                    ci.cancel();
                } else {
                    blockEntitiesTickCounter = 0;
                }
            }
        }
    }

    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void bsDFWMI$onTickEntity(Consumer<T> tickConsumer, T entity, CallbackInfo ci) {
        World world = (World)(Object)this;
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        if (entity instanceof PlayerEntity) {
            return;
        }
        if (BsDFWMI.CONFIG.common.getTickRateMobEntities) {
            entitiesTickCounter++;

            MinecraftServer server = serverWorld.getServer();
            if (server != null) {
                final int CUSTOM_TICK_RATE = getCustomTickRateMobEntities(server);

                entitiesTickCounter++;
                if (entitiesTickCounter < CUSTOM_TICK_RATE) {
                    ci.cancel();
                } else {
                    entitiesTickCounter = 0;
                }
            }
        }
    }

    @Unique
    private static int getCustomTickRateBlockEntities(MinecraftServer server) {
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
        double custom_tps = 3.0d - (tps / 10);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 1) custom_tps = 5 - (tps / 5);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 2) custom_tps = 21 - tps;
        int specificBlockEntityTickRate = BsDFWMI.CONFIG.common.getSpecificTickRateBlockEntities();
        return (int) (specificBlockEntityTickRate > 0 ? (21 - tps) * specificBlockEntityTickRate : custom_tps);
    }

    @Unique
    private static int getCustomTickRateMobEntities(MinecraftServer server) {
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
        double custom_tps = 3.0d - (tps / 10);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 1) custom_tps = 5 - (tps / 5);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 2) custom_tps = 21 - tps;
        int specificBlockEntityTickRate = BsDFWMI.CONFIG.common.getSpecificTickRateMobEntities();
        return (int) (specificBlockEntityTickRate > 0 ? (21 - tps) * specificBlockEntityTickRate : custom_tps);
    }
}