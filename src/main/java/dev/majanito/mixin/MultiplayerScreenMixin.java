package dev.majanito.mixin;

import dev.majanito.screens.EditAccountScreen;
import dev.majanito.screens.LoginScreen;
import dev.majanito.utils.APIUtils;
import dev.majanito.utils.SessionUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {
    @Unique
    private static Boolean isSessionValid = null;
    @Unique
    private static boolean hasValidationStarted = false;

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

        isSessionValid = null;
        hasValidationStarted = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String username = SessionUtils.getUsername();

        if (isSessionValid == null && !hasValidationStarted) {
            hasValidationStarted = true;
            new Thread(() -> {
                isSessionValid = APIUtils.validateSession(MinecraftClient.getInstance().getSession().getAccessToken());
            }, "SessionValidationThread").start();
        }

        Text statusText;

        if (isSessionValid == null)
            statusText = Text.literal("[... Validating]").formatted(Formatting.GRAY);
        else if (isSessionValid)
            statusText = Text.literal("[✔] Valid").formatted(Formatting.GREEN);
        else
            statusText = Text.literal("[✘] Invalid").formatted(Formatting.RED);

        Text display = Text.literal("User: ").append(Text.literal(username).formatted(Formatting.WHITE)).append(Text.literal(" | ").formatted(Formatting.DARK_GRAY)).append(statusText);

        context.drawText(this.textRenderer, display, 5, 10, 0xFFFFFFFF, false);
    }
}
