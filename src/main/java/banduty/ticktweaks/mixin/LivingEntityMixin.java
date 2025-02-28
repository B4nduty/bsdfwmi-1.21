package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import banduty.ticktweaks.util.TickRateCalculator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private static final TrackedData<Integer> TICK_TIME;
    @Unique
    private final LivingEntity livingEntity = ((LivingEntity) (Object) this);

    static {
        TICK_TIME = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    @Unique private boolean tickTimeBoolean;

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomPropertiesToDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity) return;
        builder.add(TICK_TIME, 0);
    }

    @Inject(method = "onTrackedDataSet", at = @At("TAIL"))
    private void markCustomPropertiesAsSet(TrackedData<?> data, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity) return;
        if (TICK_TIME.equals(data)) {
            this.tickTimeBoolean = true;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickEntity(CallbackInfo ci) {
        if (this.tickTimeBoolean) {
            this.tickTimeBoolean = false;
        }
        World world = livingEntity.getWorld();
        if (!(world instanceof ServerWorld serverWorld) || livingEntity instanceof PlayerEntity) return;
        if (TickRateCalculator.shouldSkipTicking(serverWorld)) return;

        MinecraftServer server = serverWorld.getServer();
        List<String> blacklistedMobs = TickTweaks.CONFIG.stopTick.getStopBlacklist();
        List<String> blacklistedLivingEntities = TickTweaks.CONFIG.enableCustomTick.getBlacklistedLivingEntities();
        int radiusThreshold = TickTweaks.CONFIG.stopTick.getStopTickingDistance();

        boolean isOutsideRadius = !blacklistedMobs.contains(livingEntity.getType().toString())
                && TickHandlerUtil.isEntityWithinRadius(livingEntity, world, radiusThreshold);
        boolean entityIsBlacklisted = TickHandlerUtil.isEntityBlacklisted(livingEntity, blacklistedLivingEntities);
        setTickTime(getTickTime() + 1);
        if (entityIsBlacklisted || TickHandlerUtil.tickCancellation(server, ci, isOutsideRadius,
                TickTweaks.CONFIG.tickRateTime.getSpecificTickRateLivingEntities(), getTickTime())) setTickTime(0);
    }

    @Unique
    private int getTickTime() {
        return ((LivingEntity) (Object) this).getDataTracker().get(TICK_TIME);
    }

    @Unique
    private void setTickTime(int tickTime) {
        ((LivingEntity) (Object) this).getDataTracker().set(TICK_TIME, tickTime);
    }
}