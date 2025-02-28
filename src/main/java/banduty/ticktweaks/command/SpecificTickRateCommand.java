package banduty.ticktweaks.command;

import banduty.ticktweaks.configs.ModConfigs;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static banduty.ticktweaks.TickTweaks.CONFIG;

public class SpecificTickRateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("tickTweaks").requires((source) -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("specificTickRate")
                        .then(CommandManager.literal("livingEntities")
                                .executes(SpecificTickRateCommand::specificTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(SpecificTickRateCommand::resetSpecificTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("formula", IntegerArgumentType.integer())
                                                .executes(SpecificTickRateCommand::setSpecificTickRate)
                                        )
                                )
                        )
                        .then(CommandManager.literal("itemEntities")
                                .executes(SpecificTickRateCommand::specificTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(SpecificTickRateCommand::resetSpecificTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("formula", IntegerArgumentType.integer())
                                                .executes(SpecificTickRateCommand::setSpecificTickRate)
                                        )
                                )
                        )
                        .then(CommandManager.literal("blockEntities")
                                .executes(SpecificTickRateCommand::specificTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(SpecificTickRateCommand::resetSpecificTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("formula", IntegerArgumentType.integer())
                                                .executes(SpecificTickRateCommand::setSpecificTickRate)
                                        )
                                )
                        )
                )
        );
    }

    private static int specificTickRate(CommandContext<ServerCommandSource> context) {
        String type = detectEntityType(context);
        int tickRate;

        switch (type) {
            case "Living Entities" -> tickRate = CONFIG.tickRateTime.getSpecificTickRateLivingEntities();
            case "Item Entities" -> tickRate = CONFIG.tickRateTime.getSpecificTickRateItemEntities();
            case "Block Entities" -> tickRate = CONFIG.tickRateTime.getSpecificTickRateBlockEntities();
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        context.getSource().sendFeedback(() -> Text.literal("Current Specific Tick Rate for " + type + ": " + tickRate), false);
        return 1;
    }

    private static int resetSpecificTickRate(CommandContext<ServerCommandSource> context) {
        String type = detectEntityType(context);

        switch (type) {
            case "Living Entities" -> CONFIG.tickRateTime.specificTickRateLivingEntities = 0;
            case "Item Entities" -> CONFIG.tickRateTime.specificTickRateItemEntities = 0;
            case "Block Entities" -> CONFIG.tickRateTime.specificTickRateBlockEntities = 0;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal(type + " Specific Tick Rate has been reset to default: 0"), true);
        return 1;
    }

    private static int setSpecificTickRate(CommandContext<ServerCommandSource> context) {
        String type = detectEntityType(context);
        int newFormula = IntegerArgumentType.getInteger(context, "formula");

        switch (type) {
            case "Living Entities" -> CONFIG.tickRateTime.specificTickRateLivingEntities = newFormula;
            case "Item Entities" -> CONFIG.tickRateTime.specificTickRateItemEntities = newFormula;
            case "Block Entities" -> CONFIG.tickRateTime.specificTickRateBlockEntities = newFormula;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal(type + " Specific Tick Rate updated to: " + newFormula), true);
        return 1;
    }

    private static String detectEntityType(CommandContext<ServerCommandSource> context) {
        String input = context.getInput();
        String output;

        if (input.contains("livingEntities")) output = "Living Entities";
        else if (input.contains("itemEntities")) output = "Item Entities";
        else if (input.contains("blockEntities")) output = "Block Entities";
        else {
            context.getSource().sendError(Text.literal("Unknown type: " + input));
            return null;
        }

        return output;
    }
}