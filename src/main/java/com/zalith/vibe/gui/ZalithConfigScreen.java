package com.zalith.vibe.gui;

import com.zalith.vibe.ZalithVibeMod;
import com.zalith.vibe.config.VibrationConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ZalithConfigScreen extends Screen {
    private final Screen parent;
    private final VibrationConfig config;

    public ZalithConfigScreen(Screen parent) {
        super(Text.literal("Настройки вибрации Zalith"));
        this.parent = parent;
        this.config = ZalithVibeMod.getInstance().getConfig();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 35;

        // Главный тумблер
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Вибрация: " + (config.enabled ? "ВКЛ" : "ВЫКЛ")),
                button -> {
                    config.enabled = !config.enabled;
                    button.setMessage(Text.literal("Вибрация: " + (config.enabled ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(centerX - 110, y, 220, 20).build());

        // Ломание блока
        y += 24;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Ломание блока: " + VibrationConfig.getStrengthLabel(config.breakStrength)),
                button -> {
                    config.breakStrength = (config.breakStrength + 1) % 4;
                    button.setMessage(Text.literal("Ломание блока: " + VibrationConfig.getStrengthLabel(config.breakStrength)));
                }
        ).dimensions(centerX - 110, y, 220, 20).build());

        // Установка блока
        y += 24;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Установка блока: " + VibrationConfig.getStrengthLabel(config.placeStrength)),
                button -> {
                    config.placeStrength = (config.placeStrength + 1) % 4;
                    button.setMessage(Text.literal("Установка блока: " + VibrationConfig.getStrengthLabel(config.placeStrength)));
                }
        ).dimensions(centerX - 110, y, 220, 20).build());

        // Удар по мобу
        y += 24;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Удар по мобу: " + VibrationConfig.getStrengthLabel(config.attackStrength)),
                button -> {
                    config.attackStrength = (config.attackStrength + 1) % 4;
                    button.setMessage(Text.literal("Удар по мобу: " + VibrationConfig.getStrengthLabel(config.attackStrength)));
                }
        ).dimensions(centerX - 110, y, 220, 20).build());

        // Лук и арбалет
        y += 24;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Лук и арбалет: " + (config.bowEnabled ? "ВКЛ" : "ВЫКЛ")),
                button -> {
                    config.bowEnabled = !config.bowEnabled;
                    button.setMessage(Text.literal("Лук и арбалет: " + (config.bowEnabled ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(centerX - 110, y, 220, 20).build());

        // Кнопка теста
        y += 26;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Тест вибрации"),
                button -> ZalithVibeMod.getInstance().getBridge().vibrate(2)
        ).dimensions(centerX - 110, y, 220, 20).build());

        // Готово
        y += 24;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Готово"),
                button -> {
                    config.save();
                    if (this.client != null) this.client.setScreen(this.parent);
                }
        ).dimensions(centerX - 110, y, 220, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
    }

    @Override
    public void close() {
        config.save();
        if (this.client != null) this.client.setScreen(this.parent);
    }
}
