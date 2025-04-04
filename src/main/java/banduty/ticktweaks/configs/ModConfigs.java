package banduty.ticktweaks.configs;

import banduty.ticktweaks.TickTweaks;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Config(name = TickTweaks.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public class ModConfigs extends PartitioningSerializer.GlobalData {
    // Core Tick Control
    @ConfigEntry.Category("coreTickSettings")
    @ConfigEntry.Gui.TransitiveObject()
    public CoreTickSettings coreTickSettings = new CoreTickSettings();

    // Entity Tick Control
    @ConfigEntry.Category("entityTickSettings")
    @ConfigEntry.Gui.TransitiveObject()
    public EntityTickSettings entityTickSettings = new EntityTickSettings();

    // Performance Optimization
    @ConfigEntry.Category("performanceSettings")
    @ConfigEntry.Gui.TransitiveObject()
    public PerformanceSettings performanceSettings = new PerformanceSettings();

    // Emergency Systems
    @ConfigEntry.Category("emergencySettings")
    @ConfigEntry.Gui.TransitiveObject()
    public EmergencySettings emergencySettings = new EmergencySettings();

    // ========== Core Tick Settings ==========
    @Config(name = TickTweaks.MOD_ID + "-coreTickSettings")
    public static final class CoreTickSettings implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        @Comment("Base formula for dynamic tick rate calculation.\nUse 'tps' as the TPS variable.")
        public String tickRateFormula = "3 - (tps / 10)";

        public String getTickRateFormula() {
            return tickRateFormula;
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("List of dimensions where custom ticking is enabled")
        public List<String> enabledDimensions = List.of(
                "minecraft:overworld",
                "minecraft:the_nether",
                "minecraft:the_end"
        );

        public boolean isDimensionEnabled(RegistryKey<World> dimension) {
            return enabledDimensions.contains(dimension.getValue().toString());
        }
    }

    // ========== Entity Tick Settings ==========
    @Config(name = TickTweaks.MOD_ID + "-entityTickSettings")
    public static final class EntityTickSettings implements ConfigData {
        // Living Entities
        @ConfigEntry.Gui.CollapsibleObject
        public LivingEntitySettings livingEntities = new LivingEntitySettings();

        // Item Entities
        @ConfigEntry.Gui.CollapsibleObject
        public ItemEntitySettings itemEntities = new ItemEntitySettings();

        // Block Entities
        @ConfigEntry.Gui.CollapsibleObject
        public BlockEntitySettings blockEntities = new BlockEntitySettings();

        public static class LivingEntitySettings implements ConfigData {
            @ConfigEntry.Gui.Tooltip()
            @Comment("Fixed tick rate for living entities (0 for dynamic TPS-based rate)")
            public int fixedTickRate = 0;

            @ConfigEntry.Gui.Tooltip(count = 0)
            @Comment("Reduced tick rate when entities are not visible")
            public int nonVisibleTickRate = 5;

            @ConfigEntry.Gui.Tooltip(count = 0)
            @Comment("List of living entities exempt from tick adjustments")
            public List<String> exemptEntities = List.of("#minecraft:hostile", "minecraft:ender_dragon");

            public int getFixedTickRate() {
                return Math.max(0, fixedTickRate);
            }

            public int getNonVisibleTickRate() {
                return Math.max(0, nonVisibleTickRate);
            }

            public List<String> getExemptEntities() {
                return exemptEntities;
            }
        }

        public static class ItemEntitySettings implements ConfigData {
            @ConfigEntry.Gui.Tooltip()
            @Comment("Fixed tick rate for item entities (0 for dynamic TPS-based rate)")
            public int fixedTickRate = 0;

            @ConfigEntry.Gui.Tooltip(count = 0)
            @Comment("Enable Dynamic tick rate for items")
            public boolean enabled = true;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Detection range for nearby items (blocks)")
            public double detectionRange = 3.0;

            public int getFixedTickRate() {
                return Math.max(0, fixedTickRate);
            }

            public double getDetectionRange() {
                return Math.clamp(detectionRange, 0.5, 10);
            }
        }

        public static class BlockEntitySettings implements ConfigData {
            @ConfigEntry.Gui.Tooltip()
            @Comment("Fixed tick rate for block entities (0 for dynamic TPS-based rate)")
            public int fixedTickRate = 0;

            @ConfigEntry.Gui.Tooltip(count = 0)
            @Comment("Enable Dynamic tick rate for block entities")
            public boolean enabled = false;

            public int getFixedTickRate() {
                return Math.max(0, fixedTickRate);
            }
        }
    }

    // ========== Performance Settings ==========
    @Config(name = TickTweaks.MOD_ID + "-performanceSettings")
    public static final class PerformanceSettings implements ConfigData {
        // Activation Ranges
        @ConfigEntry.Gui.CollapsibleObject
        public ActivationRangeSettings activationRanges = new ActivationRangeSettings();

        // Mob Despawn
        @ConfigEntry.Gui.CollapsibleObject
        public MobDespawnSettings mobDespawn = new MobDespawnSettings();

        @ConfigEntry.Gui.Tooltip()
        @Comment("Duration to Cache Custom Activation Range settings (seconds)")
        public int settingsCacheTime = 300;

        public int getSettingsCacheTime() {
            return Math.max(0, settingsCacheTime);
        }

        public static class ActivationRangeSettings {
            @ConfigEntry.Gui.Tooltip(count = 0)
            @Comment("Enable custom activation ranges")
            public boolean enabled = false;

            @ConfigEntry.Gui.CollapsibleObject
            public DefaultActivationRange defaultRange = new DefaultActivationRange(16, -1, -1);

            @ConfigEntry.Gui.Tooltip(count = 0)
            public List<CustomActivationRange> customRanges = new ArrayList<>(List.of(
                    new CustomActivationRange(
                            "farm",
                            20, 20, 20,
                            List.of("minecraft:cow", "minecraft:chicken", "minecraft:pig")
                    ),
                    new CustomActivationRange(
                            "hostile",
                            48, -1, 20,
                            List.of("#minecraft:hostile")
                    )
            ));

            public boolean isEnabled() {
                return enabled;
            }

            public DefaultActivationRange getDefaultRange() {
                return defaultRange;
            }

            public List<CustomActivationRange> getCustomRanges() {
                return customRanges;
            }
        }

        public static class DefaultActivationRange {
            public int range;
            public int tickInterval;
            public int wakeupInterval;

            public DefaultActivationRange() {
                this(16, -1, -1);
            }

            public DefaultActivationRange(int range, int tickInterval, int wakeupInterval) {
                this.range = range;
                this.tickInterval = tickInterval;
                this.wakeupInterval = wakeupInterval;
            }

            public int getRange() {
                return range;
            }

            public int getTickInterval() {
                return tickInterval;
            }

            public int getWakeupInterval() {
                return wakeupInterval;
            }
        }

        public static class CustomActivationRange {
            public String name;
            public int range;
            public int tickInterval;
            public int wakeupInterval;
            public List<String> entities;

            public CustomActivationRange() {
                this("", 0, 0, 0, List.of());
            }

            public CustomActivationRange(String name, int range, int tickInterval,
                                         int wakeupInterval, List<String> entities) {
                this.name = name;
                this.range = range;
                this.tickInterval = tickInterval;
                this.wakeupInterval = wakeupInterval;
                this.entities = entities;
            }

            public String getName() {
                return name;
            }

            public int getRange() {
                return range;
            }

            public int getTickInterval() {
                return tickInterval;
            }

            public int getWakeupInterval() {
                return wakeupInterval;
            }

            public List<String> getEntities() {
                return entities;
            }
        }

        public static class MobDespawnSettings {
            @ConfigEntry.Gui.Tooltip(count = 0)
            @Comment("Time in ticks before mobs despawn (600 = 30 seconds)")
            public int despawnTime = 600;

            @ConfigEntry.Gui.Tooltip(count = 0)
            @Comment("Chance per tick for mobs to despawn")
            public float despawnChance = 0.00125f;

            public int getDespawnTime() {
                return Math.max(0, despawnTime);
            }

            public float getDespawnChance() {
                return Math.max(0, despawnChance);
            }
        }
    }

    // ========== Emergency Settings ==========
    @Config(name = TickTweaks.MOD_ID + "-emergencySettings")
    public static final class EmergencySettings implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        @Comment("TPS threshold for emergency systems (block entity freeze)")
        public int tpsThreshold = 2;

        public int getTpsThreshold() {
            return Math.max(0, tpsThreshold);
        }
    }
}