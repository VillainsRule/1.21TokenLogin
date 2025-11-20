package dev.majanito.mixin;

import dev.majanito.screens.EditAccountScreen;
import dev.majanito.screens.LoginScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {
	protected MultiplayerScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {
		int loginButtonX = this.width - 90;
		int editAccountButtonX = this.width - 180;
		int buttonY = 5;
		int buttonWidth = 80;
		int buttonHeight = 20;

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Login"), button -> {
			MinecraftClient.getInstance().setScreen(new LoginScreen());
		}).dimensions(loginButtonX, buttonY, buttonWidth, buttonHeight).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Edit Account"), button -> {
			MinecraftClient.getInstance().setScreen(new EditAccountScreen());
		}).dimensions(editAccountButtonX, buttonY, buttonWidth, buttonHeight).build());
	}
}