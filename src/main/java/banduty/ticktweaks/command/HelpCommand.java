package banduty.ticktweaks.command;

import banduty.ticktweaks.configs.ModConfigs;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class HelpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("tickTweaks").requires((source) -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("help").executes(HelpCommand::helpMessage)
                        .then(CommandManager.literal("stopTick").executes(HelpCommand::helpStopTickMessage))
                        .then(CommandManager.literal("misc").executes(HelpCommand::helpMiscMessage))
                )
        );
    }

    private static int helpMessage(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal(
                """
                §4§lTick Rate Formula§r is the formula set to change the tick rate based on current TPS. \s
                \s
                §4§lSpecific Tick Rate§r is the specific Tick Rate you set for entities. \s
                If you put specific Tick Rate, the Tick Rate Formula won't be used. \s
                \s
                §4§lEnable Custom Tick§r is where is applied the custom tick (TPS-Based and Specific) \s
                \s
                For more info about §4§lstopTick§r and §4§lmisc§r put it after help command.
                """), true);

        return 1;
    }

    private static int helpStopTickMessage(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal(
                """
                §4§lEmergency stop TPS§r stops all the block entities work when the tps is lower from the specified value. \s
                \s
                §4§lStop Ticking Distance§r stops all the item and living entities work when they are far away from a player. \s
                You can set the distance they stop working. \s
                \s
                §4§lTicking Time On Stop§r is the Tick Rate set for all that entities are far away from a player. \s
                """), true);

        return 1;
    }

    private static int helpMiscMessage(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfigs.class).save();
        context.getSource().sendFeedback(() -> Text.literal(
                """
                §4§lDistance Item Entities§r specifies the distance of where the item entities can join together. \s
                If ServerCore is installed, it will be disabled. Change it in ServerCore options. \s
                \s
                §4§lMax Spawner Mobs§r specifies the number of Mobs can spawn a Spawner. \s
                \s
                §4§lSpawner Range§r specifies the range where a Spawner can spawn mobs. \s
                \s
                §4§lAll Spawn Group Capacity§r specifies the group capacity of that type of mob to spawn in a chunk. \s
                \s
                §4§lMob Despawn Time§r specifies the time it needs a mob to despawn. \s
                \s
                §4§lMob Despawn Chance§r specifies the chance it has a mob to despawn if the time of despawn has reached. \s
                """), true);

        return 1;
    }
}