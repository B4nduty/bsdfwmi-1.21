package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ItemEntity;
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

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Unique
    private static final Map<ItemEntity, Integer> TICK_TIME_MAP = Collections.synchronizedMap(new WeakHashMap<>());

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

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTickEntity(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        World world = itemEntity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        MinecraftServer server = serverWorld.getServer();
        int currentTickTime = TICK_TIME_MAP.getOrDefault(itemEntity, 0);

        if (!TickTweaks.CONFIG.entityTickSettings.itemEntities.enabled || TickHandlerUtil.tickCancellation(server, ci, true,
                TickTweaks.CONFIG.entityTickSettings.itemEntities.getFixedTickRate(), currentTickTime, 0))
            TICK_TIME_MAP.put(itemEntity, 0);

        TICK_TIME_MAP.put(itemEntity, currentTickTime + 1);
    }
}