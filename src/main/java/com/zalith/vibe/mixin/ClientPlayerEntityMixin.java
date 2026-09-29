package com.zalith.vibe.mixin;

import com.zalith.vibe.ZalithVibeMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Unique
    private boolean zalith$wasUsingBow = false;
    @Unique
    private int zalith$lastUseTicks = 0;
    @Unique
    private ItemStack zalith$lastHandItem = ItemStack.EMPTY;
    @Unique
    private int zalith$heartbeatTick = 0;
    @Unique
    private boolean zalith$wasFishingBite = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void zalith_onPlayerTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        // 1. Хотбар: безопасное отслеживание смены предмета в руке без приватных полей
        ItemStack currentItem = player.getMainHandStack();
        if (zalith$lastHandItem != null && currentItem != zalith$lastHandItem) {
            ZalithVibeMod.getInstance().onHotbarChanged();
        }
        zalith$lastHandItem = currentItem;

        // 2. Пульс сердца (<3 сердечек / <=6 HP)
        if (player.isAlive() && player.getHealth() <= 6.0f) {
            zalith$heartbeatTick++;
            if (zalith$heartbeatTick % 25 == 0) {
                ZalithVibeMod.getInstance().onHeartbeat();
            }
        } else {
            zalith$heartbeatTick = 0;
        }

        // 3. Рыбалка: поклёвка рыбы (ныряние поплавка вниз)
        if (player.fishHook != null) {
            if (player.fishHook.getVelocity().y < -0.07) {
                if (!zalith$wasFishingBite) {
                    ZalithVibeMod.getInstance().onFishingBite();
                    zalith$wasFishingBite = true;
                }
            } else if (player.fishHook.getVelocity().y >= 0) {
                zalith$wasFishingBite = false;
            }
        } else {
            zalith$wasFishingBite = false;
        }

        // 4. Поедание еды и натяжение тетивы лука
        if (player.isUsingItem()) {
            ItemStack active = player.getActiveItem();
            int time = player.getItemUseTime();

            if (active.getItem() instanceof BowItem || active.getItem() instanceof CrossbowItem) {
                zalith$wasUsingBow = true;
                zalith$lastUseTicks = time;
                if (time == 6 || time == 12 || time == 18) {
                    ZalithVibeMod.getInstance().onBowPull();
                }
                return;
            }

            if (time > 0 && time % 4 == 0) {
                ZalithVibeMod.getInstance().onEatTick();
            }
        }

        // 5. Выстрел из лука (спуск тетивы)
        if (zalith$wasUsingBow) {
            if (zalith$lastUseTicks >= 5) {
                ZalithVibeMod.getInstance().onBowShoot();
            }
            zalith$wasUsingBow = false;
            zalith$lastUseTicks = 0;
        }

        // 6. Шипение крипера поблизости
        if (player.age % 4 == 0 && player.getWorld() != null) {
            try {
                Box box = player.getBoundingBox().expand(6.0);
                List<CreeperEntity> creepers = player.getWorld().getEntitiesByClass(
                        CreeperEntity.class, box, c -> c.getFuseSpeed() > 0
                );
                if (!creepers.isEmpty()) {
                    ZalithVibeMod.getInstance().onCreeperWarning();
                }
            } catch (Throwable ignored) {}
        }
    }
}
