package banduty.ticktweaks.command;

import banduty.ticktweaks.configs.ModConfigs;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static banduty.ticktweaks.TickTweaks.CONFIG;

public class TickRateFormulaCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("tickTweaks").requires((source) -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("tickRateFormula").executes(TickRateFormulaCommand::getTickRateFormula)
                        .then(CommandManager.literal("reset")
                                .executes(TickRateFormulaCommand::resetTickRateFormula)
                        )
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("formula", StringArgumentType.greedyString())
                                        .executes(TickRateFormulaCommand::setTickRateFormula)
                                )
                        )
                )
        );
    }

    private static int getTickRateFormula(CommandContext<ServerCommandSource> context) {
        String formula = CONFIG.tickRateTime.getTickRateFormula();

        context.getSource().sendFeedback(() -> Text.literal("Current Tick Rate Formula: " + formula), false);
        return 1;
    }

    private static int resetTickRateFormula(CommandContext<ServerCommandSource> context) {
        CONFIG.tickRateTime.tickRateFormula = "3 - (tps / 10)";
        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal("Tick Rate Formula has been reset to default: 3 - (tps / 10)"), true);
        return 1;
    }

    private static int setTickRateFormula(CommandContext<ServerCommandSource> context) {
        String newFormula = StringArgumentType.getString(context, "formula");

        CONFIG.tickRateTime.tickRateFormula = newFormula;

        AutoConfig.getConfigHolder(ModConfigs.class).save();

        context.getSource().sendFeedback(() -> Text.literal("Tick Rate Formula updated to: " + newFormula), true);
        return 1;
    }
}
