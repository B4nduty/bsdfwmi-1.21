package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Unique
    private static final TrackedData<Integer> TICK_TIME;

    static {
        TICK_TIME = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    @Unique
    double itemDetectionRange = TickTweaks.CONFIG.entityTickSettings.itemEntities.getDetectionRange();

    @ModifyArgs(
            method = "tryMerge()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;")
    )
    private void ticktweaks$modifyMergeArgs(Args args) {
        if (!FabricLoader.getInstance().isModLoaded("servercore")) {
            args.set(0, itemDetectionRange);
            args.set(2, itemDetectionRange);
        }
    }

    //? if >= 1.19.3 && <= 1.20.4 {
    /*@Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomPropertiesToDataTracker(CallbackInfo ci) {
        ((ItemEntity) (Object) this).getDataTracker().startTracking(TICK_TIME, 0);
    }
    *///?}

    //? if >= 1.20.5 {
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomPropertiesToDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(TICK_TIME, 0);
    }
    //?}

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickEntity(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        World world = itemEntity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        MinecraftServer server = serverWorld.getServer();

        if (!TickTweaks.CONFIG.entityTickSettings.itemEntities.enabled || TickHandlerUtil.tickCancellation(server, ci, true,
                TickTweaks.CONFIG.entityTickSettings.itemEntities.getFixedTickRate(), getTickTime(), 0))
            setTickTime(0);

        setTickTime(getTickTime() + 1);
    }

    @Unique
    private int getTickTime() {
        return ((ItemEntity) (Object) this).getDataTracker().get(TICK_TIME);
    }

    @Unique
    private void setTickTime(int tickTime) {
        ((ItemEntity) (Object) this).getDataTracker().set(TICK_TIME, tickTime);
    }
}