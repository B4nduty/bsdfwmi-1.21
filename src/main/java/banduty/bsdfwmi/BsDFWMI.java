package banduty.bsdfwmi;

import banduty.bsdfwmi.configs.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class BsDFWMI implements ModInitializer {
	public static final String MOD_ID = "bsdfwmi";
	public static ModConfigs CONFIG;

	@Override
	public void onInitialize() {
	}

	public static void initialize() {
		AutoConfig.register(ModConfigs.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		CONFIG = AutoConfig.getConfigHolder(ModConfigs.class).getConfig();
	}

	public static int getCustomTickRate(MinecraftServer server, int specificTickRate) {
		double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
		double custom_tps = 3.0d - (tps / 10);
		if (BsDFWMI.CONFIG.configs.getPerformanceMode() == 1) custom_tps = 5 - (tps / 5);
		if (BsDFWMI.CONFIG.configs.getPerformanceMode() == 2) custom_tps = 21 - tps;
		return (int) (specificTickRate > 0 ? specificTickRate : custom_tps);
	}

	public static boolean shouldSkipTicking(World world) {
		RegistryKey<World> dimension = world.getRegistryKey();

		if (dimension == World.OVERWORLD && BsDFWMI.CONFIG.configs.tickOverworld) {
			return true;
		}
		if (dimension == World.NETHER && BsDFWMI.CONFIG.configs.tickNether) {
			return true;
		}
		return dimension == World.END && BsDFWMI.CONFIG.configs.tickEnd;
	}
}