package banduty.ticktweaks.mixin;


import banduty.ticktweaks.TickTweaks;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SpawnGroup.class)
public abstract class SpawnGroupMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 70))
    private static int ticktweaks$modifyMonsterCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.misc.getMonsterSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 10))
    private static int ticktweaks$modifyCreatureCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.misc.getCreatureSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 15))
    private static int ticktweaks$modifyAmbientCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.misc.getAmbientSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 5))
    private static int ticktweaks$modifyValue5Capacity(int originalCapacity) {
        return TickTweaks.CONFIG.misc.getWaterCreatureSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 20))
    private static int ticktweaks$modifyWaterAmbientCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.misc.getWaterAmbientSpawnGroupCapacity();
    }
}