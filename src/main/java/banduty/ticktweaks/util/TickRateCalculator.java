package banduty.ticktweaks.util;

import banduty.streq.StrEq;
import banduty.ticktweaks.TickTweaks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

public class TickRateCalculator {

    public static int getCustomTickRate(MinecraftServer server, int specificTickRate) {
        float tickTime = server
        //? if >= 1.20.3 {
        .getAverageTickTime();
        //?} else if >= 1.19.3 && <= 1.20.2 {
        /*.getTickTime();
        *///?}

        double tps = Math.min(1000.0 / tickTime, 20.0);

        String formula = TickTweaks.CONFIG.coreTickSettings.getTickRateFormula();
        Map<String, Double> variables = new HashMap<>();
        variables.put("tps", tps);

        int customTickRate = (int) Math.round(Math.min(20, StrEq.evaluate(formula, variables)));

        return specificTickRate > 0 ? specificTickRate : customTickRate;
    }

    public static boolean shouldSkipTicking(ServerWorld world) {
        return !TickTweaks.CONFIG.coreTickSettings.isDimensionEnabled(world.getRegistryKey());
    }
}