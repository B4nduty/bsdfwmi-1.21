package banduty.ticktweaks.mixin;

import banduty.ticktweaks.TickTweaks;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static banduty.ticktweaks.TickTweaks.lastCacheClear;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void ticktweaks$onServerTick(CallbackInfo ci) {
        long currentTime = System.currentTimeMillis();
        long cacheDuration = TickTweaks.CONFIG.performanceSettings.getSettingsCacheTime() * 1000L;
        if (currentTime - lastCacheClear > cacheDuration) {
            TickTweaks.ACTIVATION_CACHE.clear();
            lastCacheClear = currentTime;
        }
    }
}