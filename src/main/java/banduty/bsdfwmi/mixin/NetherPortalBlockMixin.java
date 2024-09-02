package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.block.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
    @Unique
    private int portalTickCounter = 0;
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void modifyTickRate(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (BsDFWMI.CONFIG.configs.getChangeTickRateNetherPortalBlock) {
            portalTickCounter++;

            MinecraftServer server = world.getServer();

            final int CUSTOM_TICK_RATE = BsDFWMI.getCustomTickRate(server, BsDFWMI.CONFIG.configs.getSpecificTickRateNetherPortalBlocks());

            portalTickCounter++;
            if (portalTickCounter < CUSTOM_TICK_RATE) {
                ci.cancel();
            } else {
                portalTickCounter = 0;
            }
        }
    }
}
