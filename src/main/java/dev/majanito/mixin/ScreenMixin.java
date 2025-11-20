package dev.majanito.mixin;

import dev.majanito.utils.APIUtils;
import dev.majanito.utils.SessionUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {

	@Unique
	private static Boolean isSessionValid = null;
	@Unique
	private static boolean hasValidationStarted = false;

	@Inject(method = "init", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {

		isSessionValid = null;
		hasValidationStarted = false;
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!((Object) this instanceof MultiplayerScreen)) return;

		String username = SessionUtils.getUsername();

		if (isSessionValid == null && !hasValidationStarted) {
			hasValidationStarted = true;
			new Thread(() -> {
                isSessionValid = APIUtils.validateSession(MinecraftClient.getInstance().getSession().getAccessToken());
			}, "SessionValidationThread").start();
		}


		Text statusText;
		if (isSessionValid == null) {
			statusText = Text.literal("[... Validating]").formatted(Formatting.GRAY);
		} else if (isSessionValid) {
			statusText = Text.literal("[✔] Valid").formatted(Formatting.GREEN);
		} else {
			statusText = Text.literal("[✘] Invalid").formatted(Formatting.RED);
		}

		Text display = Text.literal("User: ")
				.append(Text.literal(username).formatted(Formatting.WHITE))
				.append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
				.append(statusText);

		context.drawText(MinecraftClient.getInstance().textRenderer, display, 5, 10, 0xFFFFFFFF, false);
	}
}