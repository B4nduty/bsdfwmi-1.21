package banduty.ticktweaks.command;

import banduty.ticktweaks.configs.ModConfigs;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static banduty.ticktweaks.TickTweaks.CONFIG;

public class MiscCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("tickTweaks").requires((source) -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("misc")
                        .then(CommandManager.literal("distanceItemEntities").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("maxSpawnerMobs").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("spawnerRange").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("monsterSpawnGroupCapacity").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("creatureSpawnGroupCapacity").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("ambientSpawnGroupCapacity").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("waterCreatureSpawnGroupCapacity").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("waterAmbientSpawnGroupCapacity").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("mobDespawnTime").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                        .then(CommandManager.literal("mobDespawnChance").executes(MiscCommand::getActualValue)
                                .then(CommandManager.literal("reset")
                                        .executes(MiscCommand::resetValue)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(MiscCommand::setValue)
                                        )
                                )
                        )
                )
        );
    }

    private static int getActualValue(CommandContext<ServerCommandSource> context) {
        String type = detectCommand(context);
        float value;
        switch (type) {
            case "Distance Item Entities" -> value = (float) CONFIG.misc.distanceItemEntities;
            case "Max Spawner Mobs" -> value = CONFIG.misc.maxSpawnerMobs;
            case "Spawner Range" -> value = CONFIG.misc.spawnerRange;
            case "Monster Spawn Group Capacity" -> value = CONFIG.misc.monsterSpawnGroupCapacity;
            case "Creature Spawn Group Capacity" -> value = CONFIG.misc.creatureSpawnGroupCapacity;
            case "Ambient Spawn Group Capacity" -> value = CONFIG.misc.ambientSpawnGroupCapacity;
            case "Water Creature Spawn Group Capacity" -> value = CONFIG.misc.waterCreatureSpawnGroupCapacity;
            case "Water Ambient Spawn Group Capacity" -> value = CONFIG.misc.waterAmbientSpawnGroupCapacity;
            case "Mob Despawn Time" -> value = CONFIG.misc.mobDespawnTime;
            case "Mob Despawn Chance" -> value = CONFIG.misc.mobDespawnChance;
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
            case "Distance Item Entities" -> CONFIG.misc.distanceItemEntities = 3.0;
            case "Max Spawner Mobs" -> CONFIG.misc.maxSpawnerMobs = 6;
            case "Spawner Range" -> CONFIG.misc.spawnerRange = 4;
            case "Monster Spawn Group Capacity" -> CONFIG.misc.monsterSpawnGroupCapacity = 70;
            case "Creature Spawn Group Capacity" -> CONFIG.misc.creatureSpawnGroupCapacity = 10;
            case "Ambient Spawn Group Capacity" -> CONFIG.misc.ambientSpawnGroupCapacity = 15;
            case "Water Creature Spawn Group Capacity" -> CONFIG.misc.waterCreatureSpawnGroupCapacity = 5;
            case "Water Ambient Spawn Group Capacity" -> CONFIG.misc.waterAmbientSpawnGroupCapacity = 20;
            case "Mob Despawn Time" -> CONFIG.misc.mobDespawnTime = 600;
            case "Mob Despawn Chance" -> CONFIG.misc.mobDespawnChance = 0.00125f;
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
        float newValue = FloatArgumentType.getFloat(context, "value");
        switch (type) {
            case "Distance Item Entities" -> CONFIG.misc.distanceItemEntities = Math.clamp(newValue, 0.5, 10);
            case "Max Spawner Mobs" -> CONFIG.misc.maxSpawnerMobs = (int) newValue;
            case "Spawner Range" -> CONFIG.misc.spawnerRange = (int) newValue;
            case "Monster Spawn Group Capacity" -> CONFIG.misc.monsterSpawnGroupCapacity = (int) newValue;
            case "Creature Spawn Group Capacity" -> CONFIG.misc.creatureSpawnGroupCapacity = (int) newValue;
            case "Ambient Spawn Group Capacity" -> CONFIG.misc.ambientSpawnGroupCapacity = (int) newValue;
            case "Water Creature Spawn Group Capacity" -> CONFIG.misc.waterCreatureSpawnGroupCapacity = (int) newValue;
            case "Water Ambient Spawn Group Capacity" -> CONFIG.misc.waterAmbientSpawnGroupCapacity = (int) newValue;
            case "Mob Despawn Time" -> CONFIG.misc.mobDespawnTime = (int) newValue;
            case "Mob Despawn Chance" -> CONFIG.misc.mobDespawnChance = newValue;
            case null, default -> {
                context.getSource().sendError(Text.literal("Unknown type: " + type));
                return 0;
            }
        }

        AutoConfig.getConfigHolder(ModConfigs.class).save();

        context.getSource().sendFeedback(() -> Text.literal(type + " updated to: " + (type.equals("Distance Item Entities") ? Math.clamp(newValue, 0.5, 10) : newValue)), true);
        return 1;
    }

    private static String detectCommand(CommandContext<ServerCommandSource> context) {
        String input = context.getInput();
        String output;

        if (input.contains("distanceItemEntities")) output = "Distance Item Entities";
        else if (input.contains("maxSpawnerMobs")) output = "Max Spawner Mobs";
        else if (input.contains("spawnerRange")) output = "Spawner Range";
        else if (input.contains("monsterSpawnGroupCapacity")) output = "Monster Spawn Group Capacity";
        else if (input.contains("creatureSpawnGroupCapacity")) output = "Creature Spawn Group Capacity";
        else if (input.contains("ambientSpawnGroupCapacity")) output = "Ambient Spawn Group Capacity";
        else if (input.contains("waterCreatureSpawnGroupCapacity")) output = "Water Creature Spawn Group Capacity";
        else if (input.contains("waterAmbientSpawnGroupCapacity")) output = "Water Ambient Spawn Group Capacity";
        else if (input.contains("mobDespawnTime")) output = "Mob Despawn Time";
        else if (input.contains("mobDespawnChance")) output = "Mob Despawn Chance";
        else {
            context.getSource().sendError(Text.literal("Unknown type: " + input));
            return null;
        }

        return output;
    }
}
