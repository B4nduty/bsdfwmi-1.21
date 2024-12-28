package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
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

    @Unique private boolean tickTimeBoolean;
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

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomPropertiesToDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(TICK_TIME, 0);
    }

    @Inject(method = "onTrackedDataSet", at = @At("TAIL"))
    private void markCustomPropertiesAsSet(TrackedData<?> data, CallbackInfo ci) {
        if (TICK_TIME.equals(data)) {
            this.tickTimeBoolean = true;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickEntity(CallbackInfo ci) {
        if (this.tickTimeBoolean) {
            this.tickTimeBoolean = false;
        }
        Entity entity = (ItemEntity) (Object) this;
        World world = entity.getWorld();
        if (!(world instanceof ServerWorld serverWorld) || entity instanceof PlayerEntity) return;

        MinecraftServer server = serverWorld.getServer();

        if (!TickTweaks.CONFIG.enableCustomTick.changeTickRateItemEntities || TickHandlerUtil.handleTickCancellation(server, ci, false,
                TickTweaks.CONFIG.tickRateTime.getSpecificTickRateItemEntities(), getTickTime()))
            setTickTime(0);
        else setTickTime(getTickTime() + 1);
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