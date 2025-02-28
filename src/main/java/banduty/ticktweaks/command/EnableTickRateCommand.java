package banduty.ticktweaks.command;

import banduty.ticktweaks.configs.ModConfigs;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static banduty.ticktweaks.TickTweaks.CONFIG;

public class EnableTickRateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("tickTweaks").requires((source) -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("enableTickRate")
                        .then(CommandManager.literal("blacklistedLivingEntities").executes(EnableTickRateCommand::manageBlacklistedLivingEntities))
                        .then(CommandManager.literal("overworld")
                                .executes(EnableTickRateCommand::enableTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(EnableTickRateCommand::resetEnableTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("enable", BoolArgumentType.bool())
                                                .executes(EnableTickRateCommand::setEnableTickRate)
                                        )
                                )
                        )
                        .then(CommandManager.literal("nether")
                                .executes(EnableTickRateCommand::enableTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(EnableTickRateCommand::resetEnableTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("enable", BoolArgumentType.bool())
                                                .executes(EnableTickRateCommand::setEnableTickRate)
                                        )
                                )
                        )
                        .then(CommandManager.literal("end")
                                .executes(EnableTickRateCommand::enableTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(EnableTickRateCommand::resetEnableTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("enable", BoolArgumentType.bool())
                                                .executes(EnableTickRateCommand::setEnableTickRate)
                                        )
                                )
                        )
                        .then(CommandManager.literal("itemEntities")
                                .executes(EnableTickRateCommand::enableTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(EnableTickRateCommand::resetEnableTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("enable", BoolArgumentType.bool())
                                                .executes(EnableTickRateCommand::setEnableTickRate)
                                        )
                                )
                        )
                        .then(CommandManager.literal("blockEntities")
                                .executes(EnableTickRateCommand::enableTickRate)
                                .then(CommandManager.literal("reset")
                                        .executes(EnableTickRateCommand::resetEnableTickRate)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("enable", BoolArgumentType.bool())
                                                .executes(EnableTickRateCommand::setEnableTickRate)
                                        )
                                )
                        )
                )
        );
    }

    private static int enableTickRate(CommandContext<ServerCommandSource> context) {
        String type = detectEntityType(context);
        boolean enable;

        switch (type) {
            case "Overworld" -> enable = CONFIG.enableCustomTick.tickOverworld;
            case "End" -> enable = CONFIG.enableCustomTick.tickEnd;
            case "Item Entities" -> enable = CONFIG.enableCustomTick.changeTickRateItemEntities;
            case "Block Entities" -> enable = CONFIG.enableCustomTick.changeTickRateBlockEntities;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        context.getSource().sendFeedback(() -> Text.literal("Current Enable Tick Rate for " + type + ": " + enable), false);
        return 1;
    }

    private static int resetEnableTickRate(CommandContext<ServerCommandSource> context) {
        String type = detectEntityType(context);
        boolean reset;

        switch (type) {
            case "Overworld" -> {
                CONFIG.enableCustomTick.tickOverworld = true;
                reset = true;
            }
            case "End" -> {
                CONFIG.enableCustomTick.tickEnd = true;
                reset = true;
            }
            case "Item Entities" -> {
                CONFIG.enableCustomTick.changeTickRateItemEntities = true;
                reset = true;
            }
            case "Block Entities" -> {
                CONFIG.enableCustomTick.changeTickRateBlockEntities = false;
                reset = false;
            }
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        AutoConfig.getConfigHolder(ModConfigs.class).save();
        boolean finalReset = reset;
        context.getSource().sendFeedback(() -> Text.literal(type + " Enable Tick Rate has been reset to default: " + finalReset), true);
        return 1;
    }

    private static int setEnableTickRate(CommandContext<ServerCommandSource> context) {
        String type = detectEntityType(context);
        boolean newBoolean = BoolArgumentType.getBool(context, "enable");

        switch (type) {
            case "Overworld" -> CONFIG.enableCustomTick.tickOverworld = newBoolean;
            case "End" -> CONFIG.enableCustomTick.tickEnd = newBoolean;
            case "Item Entities" -> CONFIG.enableCustomTick.changeTickRateItemEntities = newBoolean;
            case "Block Entities" -> CONFIG.enableCustomTick.changeTickRateBlockEntities = newBoolean;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal(type + " Enable Tick Rate updated to: " + newBoolean), true);
        return 1;
    }

    private static String detectEntityType(CommandContext<ServerCommandSource> context) {
        String input = context.getInput();
        String output;

        if (input.contains("overworld")) output = "Overworld";
        else if (input.contains("end")) output = "End";
        else if (input.contains("itemEntities")) output = "Item Entities";
        else if (input.contains("blockEntities")) output = "Block Entities";
        else {
            context.getSource().sendError(Text.literal("Unknown type: " + input));
            return null;
        }

        return output;
    }

    private static int manageBlacklistedLivingEntities(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal("""
                Blacklisted Living Entities:\s""" + CONFIG.enableCustomTick.blacklistedLivingEntities + """
                \s
                \s
                To change the list you need to change the config file directly.
                """), true);

        return 1;
    }
}