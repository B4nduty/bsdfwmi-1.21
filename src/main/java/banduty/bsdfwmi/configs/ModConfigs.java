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
    public Common common = new Common();

    @Config(name = BsDFWMI.MOD_ID + "-common")
    public static final class Common implements ConfigData {
        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Enable Stronger Optimization mode | Default: false")
        public boolean getStrongerPerformance = false;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate based on TPS for Entities | Default: true")
        public boolean getTickRateEntities = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate based on TPS for Item Entities | Default: true")
        public boolean getTickRateItemEntities = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate based on TPS for Block Entities | Default: true")
        public boolean getTickRateBlockEntities = true;

        @ConfigEntry.Gui.Tooltip(count = 0)
        @Comment("Change Tick Rate based on TPS for Nether Portal Blocks | Default: true")
        public boolean getTickRateNetherPortalBlock = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Max Ground Stack | Default: 512" +
                "Max Items Limit: 1024 items")
        int maxGroundStack = 512;

        public int getMaxGroundStack() {
            return Math.min(1024, Math.max(0, maxGroundStack));
        }

        @ConfigEntry.Gui.Tooltip(count = 3)
        @Comment("Distance to Detect Other Item Entities | Default: 3.0 blocks" +
                "Min Distance: 0.5 blocks / Max Distance: 10 blocks" +
                "If ServerCore is Installed this will be disabled, so you need to change from ServerCore configs")
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

        @ConfigEntry.Gui.Tooltip()
        @Comment("Monster SpawnGroup Max Capacity | Default: 70" +
                "After Changes you need to Restart the Server")
        int monsterSpawnGroupCapacity = 70;

        public int getMonsterSpawnGroupCapacity() {
            return Math.max(0, monsterSpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Creature SpawnGroup Max Capacity | Default: 10" +
                "After Changes you need to Restart the Server")
        int creatureSpawnGroupCapacity = 10;

        public int getCreatureSpawnGroupCapacity() {
            return Math.max(0, creatureSpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Ambient SpawnGroup Max Capacity | Default: 15" +
                "After Changes you need to Restart the Server")
        int ambientSpawnGroupCapacity = 15;

        public int getAmbientSpawnGroupCapacity() {
            return Math.max(0, ambientSpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Axolotls / Underground Water Creature / Water Creature SpawnGroup Max Capacity | Default: 5" +
                "After Changes you need to Restart the Server")
        int value5SpawnGroupCapacity = 5;

        public int getValue5SpawnGroupCapacity() {
            return Math.max(0, value5SpawnGroupCapacity);
        }

        @ConfigEntry.Gui.Tooltip()
        @Comment("Water Ambient SpawnGroup Max Capacity | Default: 20" +
                "After Changes you need to Restart the Server")
        int waterAmbientSpawnGroupCapacity = 20;

        public int getWaterAmbientSpawnGroupCapacity() {
            return Math.max(0, waterAmbientSpawnGroupCapacity);
        }
    }
}