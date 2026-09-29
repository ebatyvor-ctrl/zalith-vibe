package com.zalith.vibe.mixin;

import com.zalith.vibe.ZalithVibeMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void zalith_onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != null && cir.getReturnValue().isAccepted()) {
            ZalithVibeMod.getInstance().onBlockPlaced();
        }
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void zalith_onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ZalithVibeMod.getInstance().onBlockBroken();
    }

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void zalith_onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        ZalithVibeMod.getInstance().onEntityAttacked();
    }
}
