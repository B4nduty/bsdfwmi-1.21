package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private static final int maxCount = BsDFWMI.CONFIG.common.getMaxGroundStack();

    /**
     * @author
     * Banduty
     * @reason
     * Change Max Stack Item Entity
     */
    @Overwrite
    private static void merge(ItemEntity targetEntity, ItemStack stack1, ItemStack stack2) {
        ItemStack itemStack = merge(stack1, stack2, maxCount);
        targetEntity.setStack(itemStack);
    }

    /**
     * @author
     * Banduty
     * @reason
     * Change the canMerge method to work with maxCount
     */
    @Overwrite
    public static boolean canMerge(ItemStack stack1, ItemStack stack2) {
        return stack2.getCount() + stack1.getCount() <= maxCount && ItemStack.areItemsAndComponentsEqual(stack1, stack2);
    }

    @Shadow private int pickupDelay;
    @Shadow private int itemAge;
    @Shadow @Final private static TrackedData<ItemStack> STACK;
    @Shadow public ItemStack getStack() { return this.getDataTracker().get(STACK); }

    /**
     * @author
     * Banduty
     * @reason
     * Change the canMerge method to work with maxCount
     */
    @Overwrite
    private boolean canMerge() {
        ItemStack itemStack = this.getStack();
        return this.isAlive() && this.pickupDelay != 32767 && this.itemAge != -32768 && this.itemAge < 6000 && itemStack.getCount() < maxCount;
    }

    /**
     * @author
     * Banduty
     * @reason
     * Change for Max Value
     */
    @Overwrite
    public static ItemStack merge(ItemStack stack1, ItemStack stack2, int maxCount) {
        int i = Math.min(maxCount - stack1.getCount(), stack2.getCount());
        ItemStack itemStack = stack1.copyWithCount(stack1.getCount() + i);
        stack2.decrement(i);
        return itemStack;
    }

    @Unique
    double distanceItemEntities = BsDFWMI.CONFIG.common.getDistanceItemEntities();

    @ModifyArg(method = "tryMerge()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"), index = 0)
    private double bsDFWMI$tryMergeX(double x) {
        if (FabricLoader.getInstance().isModLoaded("servercore")) return x;
        return distanceItemEntities;
    }

    @ModifyArg(method = "tryMerge()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"), index = 2)
    private double bsDFWMI$tryMergeZ(double z) {
        if (FabricLoader.getInstance().isModLoaded("servercore")) return z;
        return distanceItemEntities;
    }

    @Shadow @Nullable private UUID owner;

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    public void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            ItemStack itemStack = this.getStack();
            Item item = itemStack.getItem();
            int stackCount = itemStack.getCount();
            int maxCount = itemStack.getMaxCount();
            int giveCount = Math.min(stackCount, maxCount);

            if (this.pickupDelay == 0 && (owner == null || owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack.copyWithCount(giveCount))) {
                player.sendPickup(this, giveCount);
                itemStack.setCount(stackCount - giveCount);

                if (itemStack.isEmpty()) {
                    this.discard();
                }

                player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), giveCount);
                player.triggerItemPickedUpByEntityCriteria((ItemEntity)(Object)this);
            }

            ci.cancel();
        }
    }

    @Unique
    private int customTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void bsDFWMI$onTick(CallbackInfo ci) {
        if (BsDFWMI.CONFIG.common.getTickRateItemEntities) {
            customTickCounter++;

            MinecraftServer server = this.getWorld().getServer();

            if (server != null) {
                double tps = Math.min(1000.0 / server.getAverageTickTime(), 20.0);
                if (tps != 0) {
                    final int CUSTOM_TICK_RATE = (int) (21 - tps);

                    customTickCounter++;
                    if (customTickCounter < CUSTOM_TICK_RATE) {
                        ci.cancel();
                    } else {
                        customTickCounter = 0;
                    }
                }
            }

        }
    }
}