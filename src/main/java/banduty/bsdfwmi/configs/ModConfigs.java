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
        @Comment("Max Ground Stack | Default: 512")
        int maxGroundStack = 512;

        public int getMaxGroundStack() {
            return Math.max(0, maxGroundStack);
        }
    }
}