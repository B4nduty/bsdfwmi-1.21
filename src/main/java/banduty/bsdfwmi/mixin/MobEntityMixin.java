package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @ModifyConstant(method = "checkDespawn", constant = @Constant(intValue = 600))
    private int bsdfwmi$checkDespawnTime(int constant) {
        return BsDFWMI.CONFIG.configs.getMobDespawnTime();
    }

    @ModifyConstant(method = "checkDespawn", constant = @Constant(intValue = 800))
    private int bsdfwmi$checkDespawnChance(int constant) {
        return BsDFWMI.CONFIG.configs.getMobDespawnChance();
    }
}
