package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
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
    private void bsDFWMI$onTickBlockEntities(CallbackInfo ci) {
        World world = (World)(Object)this;

        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        if (BsDFWMI.shouldSkipTicking(world)) return;

        if (BsDFWMI.CONFIG.configs.getChangeTickRateBlockEntities) {
            blockEntitiesTickCounter++;

            MinecraftServer server = serverWorld.getServer();
            if (server != null) {
                final int CUSTOM_TICK_RATE = BsDFWMI.getCustomTickRate(server, BsDFWMI.CONFIG.configs.getSpecificTickRateBlockEntities());

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

        if (BsDFWMI.shouldSkipTicking(world)) return;

        if (entity instanceof PlayerEntity) {
            return;
        }

        if (entity instanceof ItemEntity) {
            if (BsDFWMI.CONFIG.configs.getChangeTickRateItemEntities) {
                entitiesTickCounter++;

                MinecraftServer server = serverWorld.getServer();

                if (server != null) {
                    final int CUSTOM_TICK_RATE = BsDFWMI.getCustomTickRate(server, BsDFWMI.CONFIG.configs.getSpecificTickRateItemEntities());

                    entitiesTickCounter++;
                    if (entitiesTickCounter < CUSTOM_TICK_RATE) {
                        ci.cancel();
                    } else {
                        entitiesTickCounter = 0;
                    }
                }

            }
        } else if (BsDFWMI.CONFIG.configs.getChangeTickRateLivingEntities) {
            if (entity.hasPassengers() && !BsDFWMI.CONFIG.configs.getChangeTickRateVehicleEntities) {
                return;
            }
            if (entity instanceof HostileEntity && !BsDFWMI.CONFIG.configs.getChangeTickRateHostileEntities) {
                return;
            }

            entitiesTickCounter++;

            MinecraftServer server = serverWorld.getServer();
            if (server != null) {
                final int CUSTOM_TICK_RATE = BsDFWMI.getCustomTickRate(server, BsDFWMI.CONFIG.configs.getSpecificTickRateLivingEntities());

                entitiesTickCounter++;
                if (entitiesTickCounter < CUSTOM_TICK_RATE) {
                    ci.cancel();
                } else {
                    entitiesTickCounter = 0;
                }
            }
        }
    }
}