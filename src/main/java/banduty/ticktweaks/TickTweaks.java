package banduty.ticktweaks;

import banduty.ticktweaks.configs.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class TickTweaks implements ModInitializer {
	public static final String MOD_ID = "ticktweaks";
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
		double customTps = 3.0d - (tps / 10);
		switch (TickTweaks.CONFIG.configs.getPerformanceMode()) {
			case 1: customTps = 5 - (tps / 5); break;
			case 2: customTps = 21 - tps; break;
			default: break;
		}
		return (int) (specificTickRate > 0 ? specificTickRate : customTps);
	}

	public static boolean shouldSkipTicking(World world) {
		RegistryKey<World> dimension = world.getRegistryKey();

		if (dimension == World.OVERWORLD && TickTweaks.CONFIG.configs.tickOverworld) {
			return true;
		}
		if (dimension == World.NETHER && TickTweaks.CONFIG.configs.tickNether) {
			return true;
		}
		return dimension == World.END && TickTweaks.CONFIG.configs.tickEnd;
	}
}