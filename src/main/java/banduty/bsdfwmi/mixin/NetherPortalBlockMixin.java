package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
    @Unique
    private int portalTickCounter = 0;
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void modifyTickRate(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (BsDFWMI.CONFIG.common.getTickRateNetherPortalBlock) {
            portalTickCounter++;

            MinecraftServer server = world.getServer();

            final int CUSTOM_TICK_RATE = getCustomTickRate(server);

            portalTickCounter++;
            if (portalTickCounter < CUSTOM_TICK_RATE) {
                ci.cancel();
            } else {
                portalTickCounter = 0;
            }

        }
    }

    @Unique
    private static int getCustomTickRate(MinecraftServer server) {
        double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
        double custom_tps = 3.0d - (tps / 10);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 1) custom_tps = 5 - (tps / 5);
        if (BsDFWMI.CONFIG.common.getStrongerPerformance() == 2) custom_tps = 21 - tps;
        int specificNetherPortalBlockTickRate = BsDFWMI.CONFIG.common.getSpecificTickRateNetherPortalBlocks();
        return (int) (specificNetherPortalBlockTickRate > 0 ? (21 - tps) * specificNetherPortalBlockTickRate : custom_tps);
    }
}
