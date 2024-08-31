package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    double distanceItemEntities = BsDFWMI.CONFIG.common.getDistanceItemEntities();

    @ModifyArg(method = "tryMerge()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"), index = 0)
    private double bsDFWMI$tryMergeX(double x) {
        if (FabricLoader.getInstance().isModLoaded("servercore")) return x;
        return distanceItemEntities;
    }

    @ModifyArg(method = "tryMerge()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"), index = 2)
    private double bsDFWMI$tryMergeZ(double z) {
        if (FabricLoader.getInstance().isModLoaded("servercore")) return z;
        return distanceItemEntities;
    }

    @Unique
    private int entityTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void bsDFWMI$onTick(CallbackInfo ci) {
        if (BsDFWMI.CONFIG.common.getTickRateItemEntities) {
            entityTickCounter++;

            MinecraftServer server = this.getWorld().getServer();

            if (server != null) {
                final int CUSTOM_TICK_RATE = getCustomTickRate(server);

                entityTickCounter++;
                if (entityTickCounter < CUSTOM_TICK_RATE) {
                    ci.cancel();
                } else {
                    entityTickCounter = 0;
                }
            }

        }
    }

    @Unique
    private static int getCustomTickRate(MinecraftServer server) {
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
        double custom_tps = 3.0d - (tps / 10);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 1) custom_tps = 5 - (tps / 5);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 2) custom_tps = 21 - tps;
        int specificItemEntityTickRate = BsDFWMI.CONFIG.common.getSpecificTickRateItemEntities();
        return (int) (specificItemEntityTickRate > 0 ? (21 - tps) * specificItemEntityTickRate : custom_tps);
    }
}