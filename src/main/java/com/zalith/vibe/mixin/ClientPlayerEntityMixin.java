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
    @Unique
    private int zalith$lastSlot = -1;
    @Unique
    private int zalith$heartbeatTick = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void zalith_onPlayerTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        // 1. Хотбар: щелчок при смене слота
        int currentSlot = player.getInventory().selectedSlot;
        if (zalith$lastSlot != -1 && zalith$lastSlot != currentSlot) {
            ZalithVibeMod.getInstance().onHotbarChanged();
        }
        zalith$lastSlot = currentSlot;

        // 2. Пульс сердца при малом здоровье (<= 6 HP / 3 сердца)
        if (player.isAlive() && player.getHealth() <= 6.0f) {
            zalith$heartbeatTick++;
            if (zalith$heartbeatTick % 25 == 0) {
                ZalithVibeMod.getInstance().onHeartbeat();
            }
        } else {
            zalith$heartbeatTick = 0;
        }

        // 3. Лук и арбалет: натяжение тетивы
        if (player.isUsingItem()) {
            ItemStack active = player.getActiveItem();
            if (active.getItem() instanceof BowItem || active.getItem() instanceof CrossbowItem) {
                zalith$wasUsingBow = true;
                int time = player.getItemUseTime();
                zalith$lastUseTicks = time;

                if (time == 6 || time == 12 || time == 18) {
                    ZalithVibeMod.getInstance().onBowPull();
                }
                return;
            }
        }

        // 4. Спуск стрелы
        if (zalith$wasUsingBow) {
            if (zalith$lastUseTicks >= 5) {
                ZalithVibeMod.getInstance().onBowShoot();
            }
            zalith$wasUsingBow = false;
            zalith$lastUseTicks = 0;
        }
    }
}
