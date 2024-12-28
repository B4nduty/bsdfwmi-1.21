package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import banduty.ticktweaks.util.TickRateCalculator;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
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
    @Unique private int portalTickCounter = 0;

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void modifyNetherPortalTickRate(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (TickRateCalculator.shouldSkipTicking(world)) return;
        if (TickTweaks.CONFIG.enableCustomTick.changeTickRateNetherPortalBlock) return;
        if (TickHandlerUtil.validateAndCancelTick(world, ci, TickTweaks.CONFIG.tickRateTime.getSpecificTickRateBlockEntities(), portalTickCounter)) {
            portalTickCounter = 0;
        } else portalTickCounter++;
    }
}