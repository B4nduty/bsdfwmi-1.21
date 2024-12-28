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

public class StopTickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("tickTweaks").requires((source) -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("stopTick")
                        .then(CommandManager.literal("stopBlacklist").executes(StopTickCommand::manageBlacklistedLivingEntities))
                        .then(CommandManager.literal("emergencyStopTps").executes(StopTickCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(StopTickCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                .executes(StopTickCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("stopTickingDistance").executes(StopTickCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(StopTickCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                .executes(StopTickCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("tickingTimeOnStop").executes(StopTickCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(StopTickCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                .executes(StopTickCommand::setValue)
                                        )
                                )
                        )
                )
        );
    }

    private static int getActualValue(CommandContext<ServerCommandSource> context) {
        String type = detectCommand(context);
        double value;
        switch (type) {
            case "Emergency Stop Tps" -> value = CONFIG.stopTick.emergencyStopTps;
            case "Stop Ticking Distance" -> value = CONFIG.stopTick.stopTickingDistance;
            case "Ticking Time On Stop" -> value = CONFIG.stopTick.tickingTimeOnStop;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        context.getSource().sendFeedback(() -> Text.literal("Current " + type + ": " + value), false);
        return 1;
    }

    private static int resetValue(CommandContext<ServerCommandSource> context) {
        String type = detectCommand(context);
        switch (type) {
            case "Emergency Stop Tps" -> CONFIG.stopTick.emergencyStopTps = 2;
            case "Stop Ticking Distance" -> CONFIG.stopTick.stopTickingDistance = 64;
            case "Ticking Time On Stop" -> CONFIG.stopTick.tickingTimeOnStop = 0;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }
        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal(type + " has been reset to default"), true);
        return 1;
    }

    private static int setValue(CommandContext<ServerCommandSource> context) {
        String type = detectCommand(context);
        int newValue = IntegerArgumentType.getInteger(context, "value");
        switch (type) {
            case "Emergency Stop Tps" -> CONFIG.stopTick.emergencyStopTps = newValue;
            case "Stop Ticking Distance" -> CONFIG.stopTick.stopTickingDistance = newValue;
            case "Ticking Time On Stop" -> CONFIG.stopTick.tickingTimeOnStop = newValue;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        AutoConfig.getConfigHolder(ModConfigs.class).save();

        context.getSource().sendFeedback(() -> Text.literal(type + " updated to: " + newValue), true);
        return 1;
    }

    private static String detectCommand(CommandContext<ServerCommandSource> context) {
        String input = context.getInput();
        String output;

        if (input.contains("emergencyStopTps")) output = "Emergency Stop Tps";
        else if (input.contains("stopTickingDistance")) output = "Stop Ticking Distance";
        else if (input.contains("tickingTimeOnStop")) output = "Ticking Time On Stop";
        else {
            context.getSource().sendError(Text.literal("Unknown type: " + input));
            return null;
        }

        return output;
    }

    private static int manageBlacklistedLivingEntities(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal("""
                Blacklisted Living Entities from Distance-Based Stop:\s""" + CONFIG.stopTick.stopBlacklist + """
                \s
                \s
                To change the list you need to change the config file directly.
                """), true);

        return 1;
    }
}
