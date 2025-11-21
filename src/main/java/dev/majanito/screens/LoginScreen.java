package dev.majanito.screens;

import dev.majanito.SessionIDLoginMod;
import dev.majanito.utils.APIUtils;
import dev.majanito.utils.SessionUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class LoginScreen extends Screen {
    private TextFieldWidget sessionField;
    private ButtonWidget loginButton;
    private ButtonWidget restoreButton;

    private Text currentTitle;

    public LoginScreen() {
        super(Text.literal(""));
        this.currentTitle = Text.literal("Input Session ID").formatted(Formatting.GOLD);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        sessionField = new TextFieldWidget(this.textRenderer, centerX - 100, centerY, 200, 20, Text.literal("Session Input"));
        sessionField.setMaxLength(32767);
        sessionField.setText("");
        sessionField.setFocused(true);
        this.addSelectableChild(sessionField);

        loginButton = ButtonWidget.builder(Text.literal("Login"), button -> {
            String sessionInput = sessionField.getText().trim();

            if (!sessionInput.isEmpty()) {
                try {
                    String[] sessionInfo = APIUtils.getProfileInfo(sessionInput);
                    SessionUtils.setSession(SessionUtils.createSession(sessionInfo[0], sessionInfo[1], sessionInput));
                    this.currentTitle = Text.literal("Logged in as: " + sessionInfo[0]).formatted(Formatting.GREEN);
                    restoreButton.active = true;
                } catch (IOException | RuntimeException e) {
                    this.currentTitle = Text.literal("Invalid Session ID").formatted(Formatting.RED);
                }
            } else
                this.currentTitle = Text.literal("Session ID cannot be empty").formatted(Formatting.RED);
        }).dimensions(centerX - 100, centerY + 25, 97, 20).build();

        this.addDrawableChild(loginButton);

        restoreButton = ButtonWidget.builder(Text.literal("Restore"), button -> {
            SessionUtils.restoreSession();

            this.currentTitle = Text.literal("Restored original session").formatted(Formatting.GREEN);

            loginButton.active = true;
            restoreButton.active = false;
        }).dimensions(centerX + 3, centerY + 25, 97, 20).build();

        this.addDrawableChild(restoreButton);

        ButtonWidget backButton = ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen(new TitleScreen()));

        }).dimensions(centerX - 100, centerY + 50, 200, 20).build();
        this.addDrawableChild(backButton);

        if (SessionIDLoginMod.currentSession.equals(SessionIDLoginMod.originalSession))
            restoreButton.active = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        sessionField.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.currentTitle, this.width / 2, this.height / 2 - 30, 0xFFFFFFFF);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (sessionField.keyPressed(keyInput) || sessionField.isActive())
            return true;

        return super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (sessionField.charTyped(charInput))
            return true;

        return super.charTyped(charInput);
    }
}
