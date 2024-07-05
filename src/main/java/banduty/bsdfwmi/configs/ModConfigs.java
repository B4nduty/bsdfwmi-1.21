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
    }
}