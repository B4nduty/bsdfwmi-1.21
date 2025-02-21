package banduty.ticktweaks;

import banduty.ticktweaks.command.*;
import banduty.ticktweaks.configs.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class TickTweaks implements ModInitializer {
	public static final String MOD_ID = "ticktweaks";
	public static ModConfigs CONFIG;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfigs.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		CONFIG = AutoConfig.getConfigHolder(ModConfigs.class).getConfig();

		CommandRegistrationCallback.EVENT.register(TickRateFormulaCommand::register);
		CommandRegistrationCallback.EVENT.register(SpecificTickRateCommand::register);
		CommandRegistrationCallback.EVENT.register(EnableTickRateCommand::register);
		CommandRegistrationCallback.EVENT.register(StopTickCommand::register);
		CommandRegistrationCallback.EVENT.register(MiscCommand::register);
		CommandRegistrationCallback.EVENT.register(HelpCommand::register);
	}
}