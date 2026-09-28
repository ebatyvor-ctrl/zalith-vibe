package com.zalith.vibe.mixin;

import com.zalith.vibe.ZalithVibeMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    /**
     * Intercept block placement (interactBlock).
     */
    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void zalith_onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != null && cir.getReturnValue().isAccepted()) {
            ZalithVibeMod.getInstance().onBlockPlaced();
        }
    }

    /**
     * Intercept block breaking completion (breakBlock).
     */
    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void zalith_onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ZalithVibeMod.getInstance().onBlockBroken();
    }

    /**
     * Intercept periodic mining progress (optional subtle pulse while mining tough blocks).
     */
    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void zalith_onUpdateMiningProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        // Subtle tick pulse while mining
        if (System.currentTimeMillis() % 150 < 25) {
            ZalithVibeMod.getInstance().onMiningTick();
        }
    }
}
