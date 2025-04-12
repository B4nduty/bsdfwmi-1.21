package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import banduty.ticktweaks.util.TickRateCalculator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    @Unique private int blockEntitiesTickCounter = 0;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void onTickBlockEntities(CallbackInfo ci) {
        World world = (World) (Object) this;
        if (!(world instanceof ServerWorld serverWorld)) return;
        double tps = Math.min(1000.0 / serverWorld.getServer()
                        //? if >= 1.20.3 {
                        .getAverageTickTime()
                //?} else if >= 1.19.3 && <= 1.20.2 {
                /*.getTickTime()
                 *///?}
                , 20.0);
        if (TickRateCalculator.shouldSkipTicking(serverWorld)) return;
        if (!TickTweaks.CONFIG.entityTickSettings.blockEntities.enabled) return;
        if (tps < TickTweaks.CONFIG.emergencySettings.getTpsThreshold()) ci.cancel();
        if (TickHandlerUtil.tickCancellation(serverWorld.getServer(), ci, true,
                TickTweaks.CONFIG.entityTickSettings.blockEntities.getFixedTickRate(),
                blockEntitiesTickCounter, 0)) {
            blockEntitiesTickCounter = 0;
        }
        blockEntitiesTickCounter++;
    }
}