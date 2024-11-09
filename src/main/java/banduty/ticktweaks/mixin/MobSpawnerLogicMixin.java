package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import net.minecraft.block.spawner.MobSpawnerLogic;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {
    @Shadow private int maxNearbyEntities;
    @Shadow private int spawnRange;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ticktweaks$onConstruct(CallbackInfo ci) {
        maxNearbyEntities = TickTweaks.CONFIG.configs.getMaxSpawnerMobs();
        spawnRange = TickTweaks.CONFIG.configs.getSpawnerRange();
    }
}
