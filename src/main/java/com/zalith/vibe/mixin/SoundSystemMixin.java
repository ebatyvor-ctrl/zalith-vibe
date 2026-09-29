package com.zalith.vibe.mixin;

import com.zalith.vibe.ZalithVibeMod;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    @Inject(method = "play", at = @At("HEAD"))
    private void zalith_onPlaySound(SoundInstance sound, CallbackInfo ci) {
        if (sound != null && sound.getId() != null) {
            ZalithVibeMod.getInstance().onSoundEvent(sound.getId().getPath());
        }
    }
}
