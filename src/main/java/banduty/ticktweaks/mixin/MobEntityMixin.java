package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @ModifyConstant(method = "checkDespawn", constant = @Constant(intValue = 600))
    private int ticktweaks$checkDespawnTime(int constant) {
        return TickTweaks.CONFIG.performanceSettings.mobDespawn.getDespawnTime();
    }

    @ModifyConstant(method = "checkDespawn", constant = @Constant(intValue = 800))
    private int ticktweaks$checkDespawnChance(int constant) {
        return (int) (1 / TickTweaks.CONFIG.performanceSettings.mobDespawn.getDespawnChance());
    }
}
