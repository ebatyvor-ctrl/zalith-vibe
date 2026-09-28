package com.zalith.vibe.mixin;

import com.zalith.vibe.ZalithVibeMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    /**
     * Trigger vibration when striking an entity/mob.
     */
    @Inject(method = "attack", at = @At("HEAD"))
    private void zalith_onAttackEntity(Entity target, CallbackInfo ci) {
        ZalithVibeMod.getInstance().onEntityAttacked();
    }
}
