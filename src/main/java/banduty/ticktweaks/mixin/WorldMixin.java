package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import banduty.ticktweaks.util.TickHandlerUtil;
import banduty.ticktweaks.util.TickRateCalculator;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
//? if >= 1.20.5 {
    import net.minecraft.network.packet.CustomPayload;
    import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
//?}
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    @Unique private int blockEntitiesTickCounter = 0;
    //? if >= 1.20.5 {
    @Unique
    private static final CustomPayload.Id<? extends CustomPayload> DUMMY_SYNC_PACKET = new CustomPayload.Id<>(Identifier.of(TickTweaks.MOD_ID, "dummy_sync"));
     //?} else if >= 1.19.3 && <= 1.20.4 {
    /*@Unique
    private static final Identifier DUMMY_SYNC_PACKET = new Identifier(TickTweaks.MOD_ID, "dummy_sync");
    *///?}

    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void onTickBlockEntities(CallbackInfo ci) {
        World world = (World) (Object) this;
        if (!(world instanceof ServerWorld serverWorld)) return;
        double tps = Math.min(1000.0 / serverWorld.getServer()
                        //? if >= 1.20.3 {
                        .getAverageTickTime()
                //?} else if >= 1.19.3 && <= 1.20.2 {
                /*.getTickTime()
                 *///?}
                , 20.0);
        if (TickRateCalculator.shouldSkipTicking(serverWorld)) return;
        if (!TickTweaks.CONFIG.entityTickSettings.blockEntities.enabled) return;
        if (tps < TickTweaks.CONFIG.emergencySettings.getTpsThreshold()) ci.cancel();
        if (TickHandlerUtil.tickCancellation(serverWorld.getServer(), ci, true,
                TickTweaks.CONFIG.entityTickSettings.blockEntities.getFixedTickRate(),
                blockEntitiesTickCounter, 0)) {
            blockEntitiesTickCounter = 0;
        }
        blockEntitiesTickCounter++;
        sendDummyPacket(serverWorld);
    }

    @Unique
    private void sendDummyPacket(ServerWorld serverWorld) {
        //? if >= 1.20.5 {
        serverWorld.getPlayers()
                .forEach(player -> {
                    player.networkHandler.sendPacket(
                            new CustomPayloadS2CPacket(
                                    () -> DUMMY_SYNC_PACKET
                            )
                    );
                });
        //?} else if >= 1.19.3 && <= 1.20.4 {
        /*PacketByteBuf buf = PacketByteBufs.create();
        serverWorld.getPlayers().forEach(serverPlayerEntity -> {
                    ServerPlayNetworking.send(serverPlayerEntity, DUMMY_SYNC_PACKET, buf);
                });
        *///?}
    }
}