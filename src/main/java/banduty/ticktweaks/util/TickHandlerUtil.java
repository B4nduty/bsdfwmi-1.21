package banduty.ticktweaks.util;

import banduty.ticktweaks.TickTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TickHandlerUtil {
    private static final Set<Entity> GLOBAL_VISIBLE_ENTITIES = ConcurrentHashMap.newKeySet();
    private static final Map<String, TagKey<EntityType<?>>> TAG_CACHE = new ConcurrentHashMap<>();
    private static long lastCacheUpdate = 0;

    public static boolean tickCancellation(MinecraftServer server, CallbackInfo ci,
                                           boolean isWithinRadius, int specificTickRate,
                                           int tickCounter, int tickingTimeOnStop) {
        final int tickRate = isWithinRadius ?
                TickRateCalculator.getCustomTickRate(server, specificTickRate) :
                tickingTimeOnStop;

        if (tickRate == 0 || tickCounter < tickRate) {
            ci.cancel();
            return false;
        }
        return true;
    }

    public static boolean isEntityWithinRadius(Entity entity, World world, int radiusThreshold) {
        final double squaredRadius = (double) radiusThreshold * radiusThreshold;
        for (PlayerEntity player : world.getPlayers()) {
            if (player.squaredDistanceTo(entity) <= squaredRadius) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOnPlayerScreen(Entity entity, World world) {
        if (!(world instanceof ServerWorld serverWorld)) return false;

        final long currentTime = world.getTime();
        if (currentTime - lastCacheUpdate > 30) {
            updateVisibleEntitiesCache(serverWorld);
            lastCacheUpdate = currentTime;
        }

        return GLOBAL_VISIBLE_ENTITIES.contains(entity);
    }

    private static void updateVisibleEntitiesCache(ServerWorld world) {
        GLOBAL_VISIBLE_ENTITIES.clear();
        final double maxDistance = 32.0;
        final double fovThreshold = 0.33;

        world.getPlayers().parallelStream().forEach(player -> {
            final Vec3d eyePos = player.getEyePos();
            final Vec3d lookVec = player.getRotationVec(1.0F);

            world.getOtherEntities(player, player.getBoundingBox().expand(maxDistance))
                    .forEach(entity -> {
                        final Vec3d toEntity = entity.getPos().subtract(eyePos).normalize();
                        if (lookVec.dotProduct(toEntity) > fovThreshold) {
                            GLOBAL_VISIBLE_ENTITIES.add(entity);
                        }
                    });
        });
    }

    public static boolean matchesEntity(LivingEntity entity, List<String> matchers) {
        if (matchers.isEmpty() || entity == null) return false;

        final EntityType<?> type = entity.getType();
        if (type == null) return false;

        final String entityId = EntityType.getId(type).toString();
        final Registry<EntityType<?>> registry = entity.getWorld()
                .getRegistryManager()
                //? if >= 1.21.5 {
                .getOrThrow(Registries.ENTITY_TYPE.getKey());
        //?} else if >= 1.19.3 && <= 1.21.4 {
        /*.get(Registries.ENTITY_TYPE.getKey());
         *///?}


        return matchers.stream().anyMatch(matcher ->
                checkMatch(matcher, entityId, registry, type)
        );
    }

    private static boolean checkMatch(String matcher, String entityId,
                                      Registry<EntityType<?>> registry,
                                      EntityType<?> type) {
        if (matcher == null || entityId == null || registry == null || type == null) {
           return false;
        }

        if (matcher.equals(entityId)) {
            return true;
        }

        if (matcher.startsWith("#")) {
            try {
                String tagString = matcher.substring(1);
                final TagKey<EntityType<?>> tagKey = TAG_CACHE.computeIfAbsent(
                        matcher,
                        m -> TagKey.of(
                                Registries.ENTITY_TYPE.getKey(),
                                Identifier.tryParse(tagString)
                        ));

                if (tagKey == null) {
                    return false;
                }

                Optional<RegistryEntryList.Named<EntityType<?>>> optional = registry
                        //? if >= 1.21.5 {
                        .getOptional(tagKey);
                //?} else if >= 1.19.3 && <= 1.21.4 {
                /*.getEntryList(tagKey);
                 *///?}

                return optional.map(registryEntries -> registryEntries.contains(registry.getEntry(type))).orElse(false);
            } catch (Exception e) {
                TickTweaks.LOGGER.error("Error checking entity tag match for {}", matcher, e);
                return false;
            }
        }

        try {
            Identifier entityIdentifier = Identifier.tryParse(entityId);
            if (entityIdentifier != null && matcher.equals(entityIdentifier.getNamespace())) {
                return true;
            }
        } catch (Exception e) {
            TickTweaks.LOGGER.error("Error checking mod ID match for {}", matcher, e);
        }

        return false;
    }
}