package banduty.ticktweaks;

import banduty.ticktweaks.configs.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.WeakHashMap;

public class TickTweaks implements ModInitializer {
	public static final String MOD_ID = "ticktweaks";
	public static ModConfigs CONFIG;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static long lastCacheClear = System.currentTimeMillis();

	public static final WeakHashMap<EntityType<?>, ModConfigs.PerformanceSettings.CustomActivationRange> ACTIVATION_CACHE = new WeakHashMap<>();

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ACTIVATION_CACHE.clear();
		});

		AutoConfig.register(ModConfigs.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		CONFIG = AutoConfig.getConfigHolder(ModConfigs.class).getConfig();
	}
}