package banduty.bsdfwmi.configs;

import banduty.bsdfwmi.BsDFWMI;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = BsDFWMI.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public class ModConfigs extends PartitioningSerializer.GlobalData {

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject()
    public Configs configs = new Configs();

    @Config(name = BsDFWMI.MOD_ID)
    public static final class Configs implements ConfigData {
        @ConfigEntry.Gui.Tooltip(count = 3)
        @Comment("Change mode " +
                "0 = Weak Mode" +
                "1 = Intermediate Mode" +
                "2 = Strong Mode" +
                "| Default: 0")
        int performanceMode = 0;

        public int getPerformanceMode() {
            return Math.max(0, Math.min(performanceMode, 2));
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate for Overworld")
        public boolean tickOverworld = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate for Nether")
        public boolean tickNether = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate for End")
        public boolean tickEnd = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Change Tick Rate for Living Entities | Default: true" +
                "Doesn’t affect Player Entities")
        public boolean getChangeTickRateLivingEntities = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Change Tick Rate for Player's Vehicle Entities | Default: false" +
                "Only when player is mounted")
        public boolean getChangeTickRateVehicleEntities = false;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate for Hostile Entities | Default: true")
        public boolean getChangeTickRateHostileEntities = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Specific Tick Rate for Living Entities" +
                "If set to 0, it will do the Tick Rate based on TPS")
        int specificTickRateLivingEntities = 0;

        public int getSpecificTickRateLivingEntities() {
            return Math.max(0, specificTickRateLivingEntities);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate based on TPS for Item Entities | Default: true")
        public boolean getChangeTickRateItemEntities = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Specific Tick Rate for Item Entities" +
                "If set to 0, it will do the Tick Rate based on TPS")
        int specificTickRateItemEntities = 0;

        public int getSpecificTickRateItemEntities() {
            return Math.max(0, specificTickRateItemEntities);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate based on TPS for Block Entities | Default: false")
        public boolean getChangeTickRateBlockEntities = false;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Specific Tick Rate for Block Entities" +
                "If set to 0, it will do the Tick Rate based on TPS")
        int specificTickRateBlockEntities = 0;

        public int getSpecificTickRateBlockEntities() {
            return Math.max(0, specificTickRateBlockEntities);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate based on TPS for Nether Portal Blocks | Default: true")
        public boolean getChangeTickRateNetherPortalBlock = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Specific Tick Rate for Nether Portal Blocks" +
                "If set to 0, it will do the Tick Rate based on TPS")
        int specificTickRateNetherPortalBlocks = 0;

        public int getSpecificTickRateNetherPortalBlocks() {
            return Math.max(0, specificTickRateNetherPortalBlocks);
        }

        @ConfigEntry.Gui.Tooltip(count = 3)
        @Comment("Distance to Detect Other Item Entities | Default: 3.0 blocks" +
                "Min Distance: 0.5 blocks / Max Distance: 10 blocks" +
                "If there is ServerCore, this is disabled, change it in ServerCore configs")
        double distanceItemEntities = 3.0;

        public double getDistanceItemEntities() {
            return Math.min(10, Math.max(0.5, distanceItemEntities));
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Max Spawn Mobs from Spawners | Default: 6 Mobs")
        int maxSpawnerMobs = 6;

        public int getMaxSpawnerMobs() {
            return Math.max(0, maxSpawnerMobs);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Spawn Range around a Spawner Block | Default: 4 Blocks")
        int spawnerRange = 4;

        public int getSpawnerRange() {
            return Math.max(0, spawnerRange);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Monster SpawnGroup Max Capacity | Default: 70")
        int monsterSpawnGroupCapacity = 70;

        public int getMonsterSpawnGroupCapacity() {
            return Math.max(0, monsterSpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Creature SpawnGroup Max Capacity | Default: 10")
        int creatureSpawnGroupCapacity = 10;

        public int getCreatureSpawnGroupCapacity() {
            return Math.max(0, creatureSpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Ambient SpawnGroup Max Capacity | Default: 15")
        int ambientSpawnGroupCapacity = 15;

        public int getAmbientSpawnGroupCapacity() {
            return Math.max(0, ambientSpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Axolotls / Underground Water Creature / Water Creature SpawnGroup Max Capacity | Default: 5")
        int value5SpawnGroupCapacity = 5;

        public int getValue5SpawnGroupCapacity() {
            return Math.max(0, value5SpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Water Ambient SpawnGroup Max Capacity | Default: 20")
        int waterAmbientSpawnGroupCapacity = 20;

        public int getWaterAmbientSpawnGroupCapacity() {
            return Math.max(0, waterAmbientSpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Tick Time required for Mob Entities to Despawn | Default: 600")
        int mobDespawnTime = 20;

        public int getMobDespawnTime() {
            return Math.max(0, mobDespawnTime);
        }

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Chance required for Mob Entities to Despawn | Default: 600")
        int mobDespawnChance = 20;

        public int getMobDespawnChance() {
            return Math.max(0, mobDespawnChance);
        }
    }
}