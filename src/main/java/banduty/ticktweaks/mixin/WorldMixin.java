package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(World.class)
public abstract class WorldMixin {
    @Unique
    private int blockEntitiesTickCounter = 0;
    @Unique
    private int entitiesTickCounter = 0;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void ticktweaks$onTickBlockEntities(CallbackInfo ci) {
        World world = (World)(Object)this;

        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        if (TickTweaks.shouldSkipTicking(world)) return;

        MinecraftServer server = serverWorld.getServer();
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
        if (tps < TickTweaks.CONFIG.configs.getEmergencyStopTps()) {
            ci.cancel();
            return;
        }

        if (TickTweaks.CONFIG.configs.getChangeTickRateBlockEntities) {
            blockEntitiesTickCounter++;

            final int CUSTOM_TICK_RATE = TickTweaks.getCustomTickRate(server, TickTweaks.CONFIG.configs.getSpecificTickRateBlockEntities());

            if (blockEntitiesTickCounter < CUSTOM_TICK_RATE) {
                ci.cancel();
            } else {
                blockEntitiesTickCounter = 0;
            }
        }
    }

    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void ticktweaks$onTickEntity(Consumer<T> tickConsumer, T entity, CallbackInfo ci) {
        World world = (World)(Object)this;

        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        if (TickTweaks.shouldSkipTicking(world)) return;

        if (entity instanceof PlayerEntity) {
            return;
        }

        entitiesTickCounter++;

        if (entity instanceof ItemEntity) {
            if (TickTweaks.CONFIG.configs.getChangeTickRateItemEntities) {
                MinecraftServer server = serverWorld.getServer();

                final int CUSTOM_TICK_RATE = TickTweaks.getCustomTickRate(server, TickTweaks.CONFIG.configs.getSpecificTickRateItemEntities());

                if (entitiesTickCounter < CUSTOM_TICK_RATE) {
                    ci.cancel();
                } else {
                    entitiesTickCounter = 0;
                }

            }
        } else if (TickTweaks.CONFIG.configs.getChangeTickRateLivingEntities) {
            if (entity.hasPassengers() && !TickTweaks.CONFIG.configs.getChangeTickRateVehicleEntities) {
                return;
            }
            if (entity instanceof HostileEntity && !TickTweaks.CONFIG.configs.getChangeTickRateHostileEntities) {
                return;
            }

            MinecraftServer server = serverWorld.getServer();
            final int CUSTOM_TICK_RATE = TickTweaks.getCustomTickRate(server, TickTweaks.CONFIG.configs.getSpecificTickRateLivingEntities());

            if (entitiesTickCounter < CUSTOM_TICK_RATE) {
                ci.cancel();
            } else {
                entitiesTickCounter = 0;
            }
        } else entitiesTickCounter = 0;
    }
}