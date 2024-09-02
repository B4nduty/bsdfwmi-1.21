package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.block.spawner.MobSpawnerLogic;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {
    @Shadow private int maxNearbyEntities;
    @Shadow private int spawnRange;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bsDFWMI$onConstruct(CallbackInfo ci) {
        maxNearbyEntities = BsDFWMI.CONFIG.configs.getMaxSpawnerMobs();
        spawnRange = BsDFWMI.CONFIG.configs.getSpawnerRange();
    }
}
