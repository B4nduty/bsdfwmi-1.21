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
            double tps = 3.0d - (Math.min(1000.0 / server.getAverageTickTime(), 20.0) / 10);
            if (BsDFWMI.CONFIG.common.getStrongerPerformance) tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
            final int CUSTOM_TICK_RATE = (int) (21 - tps);

            if (portalTickCounter < CUSTOM_TICK_RATE) {
                ci.cancel();
            } else {
                portalTickCounter = 0;
            }
        }
    }
}
