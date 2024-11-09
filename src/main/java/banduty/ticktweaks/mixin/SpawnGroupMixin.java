package banduty.ticktweaks.mixin;


import banduty.ticktweaks.TickTweaks;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SpawnGroup.class)
public abstract class SpawnGroupMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 70))
    private static int ticktweaks$modifyMonsterCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.configs.getMonsterSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 10))
    private static int ticktweaks$modifyCreatureCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.configs.getCreatureSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 15))
    private static int ticktweaks$modifyAmbientCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.configs.getAmbientSpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 5))
    private static int ticktweaks$modifyValue5Capacity(int originalCapacity) {
        return TickTweaks.CONFIG.configs.getValue5SpawnGroupCapacity();
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 20))
    private static int ticktweaks$modifyWaterAmbientCapacity(int originalCapacity) {
        return TickTweaks.CONFIG.configs.getWaterAmbientSpawnGroupCapacity();
    }
}