package banduty.bsdfwmi;

import banduty.bsdfwmi.configs.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;

public class BsDFWMI implements ModInitializer {
	public static final String MOD_ID = "bsdfwmi";
	public static ModConfigs CONFIG;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfigs.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		CONFIG = AutoConfig.getConfigHolder(ModConfigs.class).getConfig();
	}
}