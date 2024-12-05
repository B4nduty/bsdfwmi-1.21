package banduty.ticktweaks.util;

import banduty.streq.StrEq;
import banduty.ticktweaks.TickTweaks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class TickRateCalculator {
    public static int getCustomTickRate(MinecraftServer server, int specificTickRate) {
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);

        String formula = TickTweaks.CONFIG.tickRateTime.getTickRateFormula();
        Map<String, Double> variables = new HashMap<>();
        variables.put("tps", tps);

        int customTickRate = (int) Math.round(Math.min(20, StrEq.evaluate(formula, variables)));

        return specificTickRate > 0 ? specificTickRate : customTickRate;
    }

    public static boolean shouldSkipTicking(ServerWorld serverWorld) {
        RegistryKey<World> dimension = serverWorld.getRegistryKey();

        if (dimension == World.OVERWORLD && !TickTweaks.CONFIG.enableCustomTick.tickOverworld) {
            return true;
        }
        if (dimension == World.NETHER && !TickTweaks.CONFIG.enableCustomTick.tickNether) {
            return true;
        }
        return dimension == World.END && !TickTweaks.CONFIG.enableCustomTick.tickEnd;
    }
}