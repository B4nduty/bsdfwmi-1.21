package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    @Unique
    private int blockEntitiesTickCounter = 0;
    @Unique
    private int entitiesTickCounter = 0;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void bsDFWMI$onTickBlockEntities(CallbackInfo ci) {
        if (BsDFWMI.CONFIG.common.getTickRateBlockEntities) {
            World world = (World)(Object)this;
            if (!(world instanceof ServerWorld serverWorld)) {
                return;
            }

            blockEntitiesTickCounter++;

            MinecraftServer server = serverWorld.getServer();
            if (server != null) {
                double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
                final int CUSTOM_TICK_RATE = (int) (21 - tps);

                if (blockEntitiesTickCounter < CUSTOM_TICK_RATE) {
                    ci.cancel();
                } else {
                    blockEntitiesTickCounter = 0;
                }
            }
        }
    }

    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private void bsDFWMI$onTickEntity(CallbackInfo ci) {
        if (BsDFWMI.CONFIG.common.getTickRateEntities) {
            World world = (World)(Object)this;
            if (!(world instanceof ServerWorld serverWorld)) {
                return;
            }

            entitiesTickCounter++;

            MinecraftServer server = serverWorld.getServer();
            if (server != null) {
                double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
                final int CUSTOM_TICK_RATE = (int) (21 - tps);

                if (entitiesTickCounter < CUSTOM_TICK_RATE) {
                    ci.cancel();
                } else {
                    entitiesTickCounter = 0;
                }
            }
        }
    }
}