package com.zalith.vibe.mixin;

import com.zalith.vibe.ZalithVibeMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Unique
    private boolean zalith$wasUsingBow = false;
    @Unique
    private int zalith$lastUseTicks = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void zalith_onPlayerTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        if (player.isUsingItem()) {
            ItemStack active = player.getActiveItem();
            if (active.getItem() instanceof BowItem || active.getItem() instanceof CrossbowItem) {
                zalith$wasUsingBow = true;
                int time = player.getItemUseTime();
                zalith$lastUseTicks = time;

                // Ступени натяжения тетивы (0.5 - лёгкий микро-клик)
                if (time == 6 || time == 12 || time == 18) {
                    ZalithVibeMod.getInstance().onBowPull();
                }
                return;
            }
        }

        // Момент спуска тетивы / выстрела
        if (zalith$wasUsingBow) {
            if (zalith$lastUseTicks >= 5) {
                ZalithVibeMod.getInstance().onBowShoot();
            }
            zalith$wasUsingBow = false;
            zalith$lastUseTicks = 0;
        }
    }
}
