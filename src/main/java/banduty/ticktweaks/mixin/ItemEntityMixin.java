package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    double distanceItemEntities = TickTweaks.CONFIG.misc.getDistanceItemEntities();

    @ModifyArgs(
            method = "tryMerge()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;")
    )
    private void ticktweaks$modifyMergeArgs(Args args) {
        if (!FabricLoader.getInstance().isModLoaded("servercore")) {
            args.set(0, distanceItemEntities);
            args.set(2, distanceItemEntities);
        }
    }
}