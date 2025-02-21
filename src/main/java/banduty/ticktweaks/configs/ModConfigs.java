package banduty.ticktweaks.configs;

import banduty.ticktweaks.TickTweaks;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@Config(name = TickTweaks.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public class ModConfigs extends PartitioningSerializer.GlobalData {

    @ConfigEntry.Category("tickRateTime")
    @ConfigEntry.Gui.TransitiveObject()
    public TickRateTime tickRateTime = new TickRateTime();

    @ConfigEntry.Category("enableCustomTick")
    @ConfigEntry.Gui.TransitiveObject()
    public EnableCustomTick enableCustomTick  = new EnableCustomTick();

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.TransitiveObject()
    public Misc misc  = new Misc();

    @ConfigEntry.Category("stopTick")
    @ConfigEntry.Gui.TransitiveObject()
    public StopTick stopTick  = new StopTick();

    @Config(name = TickTweaks.MOD_ID + "-tickRateTime")
    public static final class TickRateTime implements ConfigData {

        @ConfigEntry.Gui.Tooltip()
        @Comment("Formula for custom tick rate calculation. Use tps as the variable for TPS.")
        public String tickRateFormula = "3 - (tps / 10)";

        public String getTickRateFormula() {
            return tickRateFormula;
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Tick Rate for Living Entities. \nSet to 0 for a TPS-based Tick Rate.")
        public int specificTickRateLivingEntities = 0;

        public int getSpecificTickRateLivingEntities() {
            return Math.max(0, specificTickRateLivingEntities);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Tick Rate for Item Entities. \nSet to 0 for a TPS-based Tick Rate.")
        public int specificTickRateItemEntities = 0;

        public int getSpecificTickRateItemEntities() {
            return Math.max(0, specificTickRateItemEntities);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Tick Rate for Block Entities. \nSet to 0 for a TPS-based Tick Rate.")
        public int specificTickRateBlockEntities = 0;

        public int getSpecificTickRateBlockEntities() {
            return Math.max(0, specificTickRateBlockEntities);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Tick Rate for Nether Portal Blocks. \nSet to 0 for a TPS-based Tick Rate.")
        public int specificTickRateNetherPortalBlocks = 0;

        public int getSpecificTickRateNetherPortalBlocks() {
            return Math.max(0, specificTickRateNetherPortalBlocks);
        }
    }

    @Config(name = TickTweaks.MOD_ID + "-enableCustomTick")
    public static final class EnableCustomTick implements ConfigData {

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Enable custom Tick Rate changes for the Overworld.")
        public boolean tickOverworld = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Enable custom Tick Rate changes for the Nether.")
        public boolean tickNether = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Enable custom Tick Rate changes for the End.")
        public boolean tickEnd = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Living Entities exempt from custom Tick Rate. \nList Living Entities IDs separated by commas (e.g., \"minecraft:zombie\", \"minecraft:skeleton\") \n You can put also #minecraft:hostile, #minecraft:passive & #minecraft:neutral")
        public List<String> blacklistedLivingEntities = List.of("#minecraft:hostile", "minecraft:ender_dragon");

        public List<String> getBlacklistedLivingEntities() {
            return blacklistedLivingEntities;
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Enable custom Tick Rate for Item Entities. \nDefault: true")
        public boolean changeTickRateItemEntities = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Enable custom Tick Rate for Block Entities. \nDefault: false")
        public boolean changeTickRateBlockEntities = false;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Enable custom Tick Rate for Nether Portal Blocks. \nDefault: true")
        public boolean changeTickRateNetherPortalBlock = true;
    }

    @Config(name = TickTweaks.MOD_ID + "-stopTick")
    public static final class StopTick implements ConfigData {

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Emergency stop TPS; halts block entity ticking.")
        public int emergencyStopTps = 2;

        public int getEmergencyStopTps() {
            return Math.clamp(emergencyStopTps, 0, 20);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Distance within players which mobs stop ticking. \nSet to 0 to disable.")
        public int stopTickingDistance = 64;

        public int getStopTickingDistance() {
            return Math.max(0, stopTickingDistance);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Change Tick Rate when stopping from distance. \nSet to 0 to completely stop ticking.")
        public int tickingTimeOnStop = 0;

        public int getTickingTimeOnStop() {
            return Math.max(0, tickingTimeOnStop);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Mobs exempt from distance-based stop. \nList mobs IDs separated by commas (e.g., \"minecraft:zombie\", \"minecraft:skeleton\") \n You can put also #minecraft:hostile, #minecraft:passive and #minecraft:neutral")
        public List<String> stopBlacklist = List.of("#minecraft:hostile", "minecraft:ender_dragon");

        public List<String> getStopBlacklist() {
            return stopBlacklist;
        }
    }

    @Config(name = TickTweaks.MOD_ID + "-misc")
    public static final class Misc implements ConfigData {

        @ConfigEntry.Gui.Tooltip(count = 3)
        @Comment("Distance to detect nearby item entities. \nDefault: 3.0 blocks. \nMin: 0.5 blocks, Max: 10 blocks. \nIf ServerCore is used, this is disabled.")
        public double distanceItemEntities = 3.0;

        public double getDistanceItemEntities() {
            return Math.clamp(distanceItemEntities, 0.5, 10);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Tick time required for mob entities to despawn. \nDefault: 600.")
        public int mobDespawnTime = 600;

        public int getMobDespawnTime() {
            return Math.max(0, mobDespawnTime);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Chance for mob entities to despawn. \nDefault: 0.00125.")
        public float mobDespawnChance = 0.00125f;

        public float getMobDespawnChance() {
            return Math.max(0, mobDespawnChance);
        }
    }
}