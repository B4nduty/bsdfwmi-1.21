package banduty.bsdfwmi.mixin;

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
    private void onTickBlockEntities(CallbackInfo ci) {
        World world = (World)(Object)this;
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        blockEntitiesTickCounter++;

        MinecraftServer server = serverWorld.getServer();
        if (server != null) {
            double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
            final int CUSTOM_TICK_RATE = (int) ((20 / Math.pow(tps, 3)) * 400);

            if (blockEntitiesTickCounter < CUSTOM_TICK_RATE) {
                ci.cancel();
            } else {
                blockEntitiesTickCounter = 0;
            }
        }
    }

    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private void onTickEntity(CallbackInfo ci) {
        World world = (World)(Object)this;
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        entitiesTickCounter++;

        MinecraftServer server = serverWorld.getServer();
        if (server != null) {
            double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
            final int CUSTOM_TICK_RATE = (int) ((20 / Math.pow(tps, 3)) * 400);

            if (entitiesTickCounter < CUSTOM_TICK_RATE) {
                ci.cancel();
            } else {
                entitiesTickCounter = 0;
            }
        }
    }
}