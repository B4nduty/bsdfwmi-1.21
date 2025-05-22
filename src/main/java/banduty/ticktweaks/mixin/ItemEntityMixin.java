package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.PacketByteBuf;
//? if >= 1.20.5 {
    import net.minecraft.network.packet.CustomPayload;
    import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
//?}
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
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


    //? if >= 1.20.5 {
    @Unique
    private static final CustomPayload.Id<? extends CustomPayload> DUMMY_SYNC_PACKET = new CustomPayload.Id<>(Identifier.of(TickTweaks.MOD_ID, "dummy_sync"));
     //?} else if >= 1.19.3 && <= 1.20.4 {
    /*@Unique
    private static final Identifier DUMMY_SYNC_PACKET = new Identifier(TickTweaks.MOD_ID, "dummy_sync");
     *///?}

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
        sendDummyPacket(itemEntity);
    }

    @Unique
    private void sendDummyPacket(ItemEntity entity) {
        ServerWorld world = (ServerWorld) entity.getWorld();
        //? if >= 1.20.5 {
        world.getPlayers(player -> player.canSee(entity))
                .forEach(player -> {
                    player.networkHandler.sendPacket(
                            new CustomPayloadS2CPacket(
                                    () -> DUMMY_SYNC_PACKET
                            )
                    );
                });
        //?} else if >= 1.19.3 && <= 1.20.4 {
        /*PacketByteBuf buf = PacketByteBufs.create();
        world.getPlayers(serverPlayerEntity -> serverPlayerEntity.canSee(entity))
                .forEach(serverPlayerEntity -> {
                    ServerPlayNetworking.send(serverPlayerEntity, DUMMY_SYNC_PACKET, buf);
                });
        *///?}
    }
}