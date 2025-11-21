package dev.majanito.mixin;

import dev.majanito.SessionIDLoginMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "getSession", at = @At("HEAD"), cancellable = true)
    private void onGetSession(CallbackInfoReturnable<Session> cir) {
        if (!SessionIDLoginMod.overrideSession)
            return;
        cir.setReturnValue(SessionIDLoginMod.currentSession);
    }
}
