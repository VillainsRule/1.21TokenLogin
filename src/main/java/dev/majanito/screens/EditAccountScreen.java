package dev.majanito.screens;

import dev.majanito.SessionIDLoginMod;
import dev.majanito.utils.APIUtils;
import dev.majanito.utils.SessionUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EditAccountScreen extends Screen {
    private TextFieldWidget nameField;
    private TextFieldWidget skinUrlField;
    private ButtonWidget nameButton;
    private ButtonWidget skinButton;
    private Text currentTitle;

    public EditAccountScreen() {
        super(Text.literal(""));
        this.currentTitle = Text.literal("Edit Account").formatted(Formatting.AQUA);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        nameField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY - 40,
                200,
                20,
                Text.literal("New Username")
        );
        nameField.setMaxLength(16);
        nameField.setFocused(true);
        this.addSelectableChild(nameField);

        skinUrlField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY,
                200,
                20,
                Text.literal("Skin URL")
        );
        skinUrlField.setMaxLength(2048);
        this.addSelectableChild(skinUrlField);

        nameButton = ButtonWidget.builder(Text.literal("Change Name"), button -> {
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                if(newName.matches("^[a-zA-Z0-9_]{3,16}$")) {
                    int statusCode = APIUtils.changeName(newName, SessionIDLoginMod.currentSession.getAccessToken());
                    currentTitle = switch (statusCode) {
                        case 200 -> {
                            SessionIDLoginMod.currentSession = SessionUtils.createSession(newName, SessionIDLoginMod.currentSession.getUuidOrNull(), SessionIDLoginMod.currentSession.getAccessToken());
                            yield Text.literal("Successfully changed name").formatted(Formatting.GREEN);
                        }
                        case 429 -> Text.literal("Too many requests").formatted(Formatting.RED);
                        case 400 -> Text.literal("Invalid name").formatted(Formatting.RED);
                        case 401 -> Text.literal("Invalid token").formatted(Formatting.RED);
                        case 403 -> Text.literal("Name is unavailable or Player already changed name in the last 35 days").formatted(Formatting.RED);
                        default -> Text.literal("Unknown error").formatted(Formatting.RED);
                    };

                }else{
                    currentTitle = Text.literal("Invalid name").formatted(Formatting.RED);
                }
            }else{
                currentTitle = Text.literal("Please input a name").formatted(Formatting.RED);
            }
        }).dimensions(centerX - 100, centerY + 25, 97, 20).build();
        this.addDrawableChild(nameButton);

        skinButton = ButtonWidget.builder(Text.literal("Change Skin"), button -> {
            String skinUrl = skinUrlField.getText().trim();
            if (!skinUrl.isEmpty()) {
                int statusCode = APIUtils.changeSkin(skinUrl, SessionIDLoginMod.currentSession.getAccessToken());
                currentTitle = switch (statusCode){
                    case 200 -> Text.literal("Successfully changed skin").formatted(Formatting.GREEN);
                    case 429 -> Text.literal("Too many requests").formatted(Formatting.RED);
                    case 401 -> Text.literal("Invalid token").formatted(Formatting.RED);
                    case -1 -> Text.literal("Unknown error").formatted(Formatting.RED);
                    default  -> Text.literal("Invalid Skin").formatted(Formatting.RED);
                };
            }else{
                currentTitle = Text.literal("Please input an URL").formatted(Formatting.RED);
            }
        }).dimensions(centerX + 3, centerY + 25, 97, 20).build();
        this.addDrawableChild(skinButton);

        ButtonWidget backButton = ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen(new TitleScreen()));
        }).dimensions(centerX - 100, centerY + 50, 200, 20).build();
        this.addDrawableChild(backButton);

        if (SessionIDLoginMod.originalSession.equals(SessionIDLoginMod.currentSession)){
            nameButton.active = false;
            skinButton.active = false;

            currentTitle = Text.literal("To enable this, you MUST first sign in with a token!").formatted(Formatting.YELLOW);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Username:"), this.width / 2 - 100, this.height / 2 - 52, 0xA0A0A0);
        nameField.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Skin URL:"), this.width / 2 - 100, this.height / 2 - 10, 0xA0A0A0);
        skinUrlField.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.currentTitle,
                this.width / 2,
                this.height / 2 - 75,
                0xFFFFFF
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return nameField.keyPressed(keyCode, scanCode, modifiers) ||
                skinUrlField.keyPressed(keyCode, scanCode, modifiers) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return nameField.charTyped(chr, modifiers) ||
                skinUrlField.charTyped(chr, modifiers) ||
                super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean nameFocused = nameField.mouseClicked(mouseX, mouseY, button);
        boolean skinFocused = skinUrlField.mouseClicked(mouseX, mouseY, button);

        nameField.setFocused(nameFocused);
        skinUrlField.setFocused(skinFocused);

        return nameFocused || skinFocused || super.mouseClicked(mouseX, mouseY, button);
    }
}
