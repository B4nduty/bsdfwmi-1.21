package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.configs.ModConfigs;
import banduty.ticktweaks.util.TickHandlerUtil;
import banduty.ticktweaks.util.TickRateCalculator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import java.util.Map;
import java.util.WeakHashMap;

import static banduty.ticktweaks.TickTweaks.ACTIVATION_CACHE;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private static final Map<LivingEntity, Integer> TICK_TIME_MAP = Collections.synchronizedMap(new WeakHashMap<>());
    @Unique
    private final LivingEntity livingEntity = (LivingEntity) (Object) this;
    @Unique
    private static final Map<LivingEntity, Integer> WAKEUP_INTERVAL_MAP = Collections.synchronizedMap(new WeakHashMap<>());
    @Unique
    private ModConfigs.PerformanceSettings.CustomActivationRange customActivationRange;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickEntity(CallbackInfo ci) {
        World world = livingEntity.getWorld();
        if (!(world instanceof ServerWorld serverWorld) || livingEntity instanceof PlayerEntity ||
                TickRateCalculator.shouldSkipTicking(serverWorld) || livingEntity.getHealth() <= 0.0F) return;

        ModConfigs.PerformanceSettings.ActivationRangeSettings activationSettings = TickTweaks.CONFIG.performanceSettings.activationRanges;
        int currentTickTime = TICK_TIME_MAP.getOrDefault(livingEntity, 0);
        int wakeupInterval = WAKEUP_INTERVAL_MAP.getOrDefault(livingEntity, 0);

        if (activationSettings.isEnabled() && customActivationRange != null) {
            int wakeupInt = customActivationRange.getWakeupInterval();
            if (wakeupInt > 0 && wakeupInterval < wakeupInt) {
                WAKEUP_INTERVAL_MAP.put(livingEntity, wakeupInterval + 1);
                int tickInt = customActivationRange.getTickInterval();
                if (currentTickTime < tickInt) {
                    TICK_TIME_MAP.put(livingEntity, currentTickTime + 1);
                    ci.cancel();
                    return;
                }
            }
        } else {
            ModConfigs.PerformanceSettings.DefaultActivationRange defaultActivationRange = TickTweaks.CONFIG.performanceSettings.activationRanges.getDefaultRange();
            int wakeupInt = defaultActivationRange.getWakeupInterval();
            if (wakeupInt > 0 && wakeupInterval < wakeupInt) {
                WAKEUP_INTERVAL_MAP.put(livingEntity, wakeupInterval + 1);
                int tickInt = defaultActivationRange.getTickInterval();
                if (currentTickTime < tickInt) {
                    TICK_TIME_MAP.put(livingEntity, currentTickTime + 1);
                    ci.cancel();
                    return;
                }
            }
        }

        TICK_TIME_MAP.put(livingEntity, 0);
        WAKEUP_INTERVAL_MAP.put(livingEntity, 0);

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
                TICK_TIME_MAP.put(livingEntity, 0);
                return;
            }
        }

        TICK_TIME_MAP.put(livingEntity, currentTickTime + 1);
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

        TICK_TIME_MAP.put(livingEntity, 0);
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
}