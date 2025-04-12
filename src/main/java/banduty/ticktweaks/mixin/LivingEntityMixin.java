package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.configs.ModConfigs;
import banduty.ticktweaks.util.TickHandlerUtil;
import banduty.ticktweaks.util.TickRateCalculator;
import net.minecraft.entity.EntityType;
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

import java.util.Collections;
import java.util.List;

import static banduty.ticktweaks.TickTweaks.ACTIVATION_CACHE;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private static final TrackedData<Integer> TICK_TIME;
    @Unique
    private final LivingEntity livingEntity = (LivingEntity) (Object) this;
    @Unique
    private int wakeupInterval = 0;
    @Unique
    private ModConfigs.PerformanceSettings.CustomActivationRange customActivationRange;

    static {
        TICK_TIME = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    //? if >= 1.19.3 && <= 1.20.4 {
    /*@Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomPropertiesToDataTracker(CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity) return;
        ((LivingEntity) (Object) this).getDataTracker().startTracking(TICK_TIME, 0);
    }
    *///?}

    //? if >= 1.20.5 {
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomPropertiesToDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity) return;
        builder.add(TICK_TIME, 0);
    }
    //?}

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickEntity(CallbackInfo ci) {
        World world = livingEntity.getWorld();
        if (!(world instanceof ServerWorld serverWorld) || livingEntity instanceof PlayerEntity ||
                TickRateCalculator.shouldSkipTicking(serverWorld) || livingEntity.getHealth() <= 0.0F) return;

        ModConfigs.PerformanceSettings.ActivationRangeSettings activationSettings = TickTweaks.CONFIG.performanceSettings.activationRanges;
        int currentTickTime = getTickTime();

        if (activationSettings.isEnabled() && customActivationRange != null) {
            int wakeupInt = customActivationRange.getWakeupInterval();
            if (wakeupInt > 0 && wakeupInterval < wakeupInt) {
                wakeupInterval++;
                int tickInt = customActivationRange.getTickInterval();
                if (currentTickTime < tickInt) {
                    setTickTime(currentTickTime + 1);
                    ci.cancel();
                    return;
                }
            }
        } else {
            ModConfigs.PerformanceSettings.DefaultActivationRange defaultActivationRange = TickTweaks.CONFIG.performanceSettings.activationRanges.getDefaultRange();
            int wakeupInt = defaultActivationRange.getWakeupInterval();
            if (wakeupInt > 0 && wakeupInterval < wakeupInt) {
                wakeupInterval++;
                int tickInt = defaultActivationRange.getTickInterval();
                if (currentTickTime < tickInt) {
                    setTickTime(currentTickTime + 1);
                    ci.cancel();
                    return;
                }
            }
        }

        setTickTime(0);
        wakeupInterval = 0;

        if (activationSettings.isEnabled()) {
            handleCustomActivationWithCache(serverWorld, ci, currentTickTime);
            if (ci.isCancelled()) return;
        }

        ModConfigs.PerformanceSettings.DefaultActivationRange defaultRange = activationSettings.getDefaultRange();
        int globalDistance = defaultRange.getRange();
        List<String> exemptEntities = TickTweaks.CONFIG.entityTickSettings.livingEntities.getExemptEntities();
        boolean isExempt = TickHandlerUtil.matchesEntity(livingEntity, exemptEntities);

        if (globalDistance > 0 && !isExempt) {
            boolean withinRadius = TickHandlerUtil.isEntityWithinRadius(livingEntity, world, globalDistance);
            boolean onScreen = TickHandlerUtil.isOnPlayerScreen(livingEntity, world);
            int tickRate = onScreen ? TickTweaks.CONFIG.entityTickSettings.livingEntities.getFixedTickRate() :
                    TickTweaks.CONFIG.entityTickSettings.livingEntities.getNonVisibleTickRate();

            MinecraftServer server = serverWorld.getServer();
            boolean shouldCancel = TickHandlerUtil.tickCancellation(server, ci, withinRadius, tickRate, currentTickTime, defaultRange.getTickInterval());
            if (shouldCancel) {
                setTickTime(0);
                return;
            }
        }

        setTickTime(currentTickTime + 1);
    }

    @Unique
    private void handleCustomActivationWithCache(ServerWorld world, CallbackInfo ci, int currentTickTime) {
        customActivationRange = getActivationTypeForEntity();

        boolean withinRadius = TickHandlerUtil.isEntityWithinRadius(livingEntity, world, customActivationRange.getRange());
        int tickInt = customActivationRange.getTickInterval();

        if ((tickInt < 0 || currentTickTime < tickInt) && !withinRadius) {
            ci.cancel();
            return;
        }

        setTickTime(0);
    }

    @Unique
    private ModConfigs.PerformanceSettings.CustomActivationRange getActivationTypeForEntity() {
        EntityType<?> type = livingEntity.getType();
        return ACTIVATION_CACHE.computeIfAbsent(type, t -> {
            ModConfigs.PerformanceSettings.ActivationRangeSettings settings = TickTweaks.CONFIG.performanceSettings.activationRanges;
            List<ModConfigs.PerformanceSettings.CustomActivationRange> customRanges = settings.getCustomRanges();

            for (ModConfigs.PerformanceSettings.CustomActivationRange custom : customRanges) {
                if (TickHandlerUtil.matchesEntity(livingEntity, custom.getEntities())) {
                    return custom;
                }
            }

            ModConfigs.PerformanceSettings.DefaultActivationRange defaultRange = settings.getDefaultRange();
            return new ModConfigs.PerformanceSettings.CustomActivationRange(
                    "default",
                    defaultRange.getRange(),
                    defaultRange.getTickInterval(),
                    defaultRange.getWakeupInterval(),
                    Collections.emptyList()
            );
        });
    }

    @Unique
    private int getTickTime() {
        return livingEntity.getDataTracker().get(TICK_TIME);
    }

    @Unique
    private void setTickTime(int tickTime) {
        livingEntity.getDataTracker().set(TICK_TIME, tickTime);
    }
}