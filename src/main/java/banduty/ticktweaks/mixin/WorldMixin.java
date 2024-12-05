package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
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
        if (!TickHandlerUtil.validateAndCancelTick(serverWorld, ci, TickTweaks.CONFIG.tickRateTime.getSpecificTickRateBlockEntities(), blockEntitiesTickCounter++)) {
            blockEntitiesTickCounter = 0;
        }
    }
}