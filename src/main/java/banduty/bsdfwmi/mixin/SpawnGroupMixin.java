package banduty.bsdfwmi.mixin;


import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SpawnGroup.class)
public abstract class SpawnGroupMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 70))
    private static int modifyMonsterCapacity(int originalCapacity) {
        return BsDFWMI.CONFIG.common.getMonsterSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 10))
    private static int modifyCreatureCapacity(int originalCapacity) {
        return BsDFWMI.CONFIG.common.getCreatureSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 15))
    private static int modifyAmbientCapacity(int originalCapacity) {
        return BsDFWMI.CONFIG.common.getAmbientSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 5))
    private static int modifyValue5Capacity(int originalCapacity) {
        return BsDFWMI.CONFIG.common.getValue5SpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 20))
    private static int modifyWaterAmbientCapacity(int originalCapacity) {
        return BsDFWMI.CONFIG.common.getWaterAmbientSpawnGroupCapacity();
    }
}