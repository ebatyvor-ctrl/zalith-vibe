package com.zalith.vibe.gui;

import com.zalith.vibe.ZalithVibeMod;
import com.zalith.vibe.config.VibrationConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ZalithConfigScreen extends Screen {
    private final Screen parent;
    private final VibrationConfig config;

    public ZalithConfigScreen(Screen parent) {
        super(Text.translatable("zalith_vibe.title"));
        this.parent = parent;
        this.config = ZalithVibeMod.getInstance().getConfig();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 40;

        // Master toggle
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Вибрация: " + (config.enabled ? "ВКЛ" : "ВЫКЛ")),
                button -> {
                    config.enabled = !config.enabled;
                    button.setMessage(Text.literal("Вибрация: " + (config.enabled ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(centerX - 100, y, 200, 20).build());

        // Break block toggle
        y += 24;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Ломание блока: " + (config.breakEnabled ? "ВКЛ" : "ВЫКЛ")),
                button -> {
                    config.breakEnabled = !config.breakEnabled;
                    button.setMessage(Text.literal("Ломание блока: " + (config.breakEnabled ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(centerX - 100, y, 200, 20).build());

        // Break duration slider (10ms - 200ms)
        y += 24;
        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20,
                Text.literal("Длительность ломания: " + config.breakDurationMs + " мс"),
                (config.breakDurationMs - 10.0) / 190.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Длительность ломания: " + config.breakDurationMs + " мс"));
            }
            @Override
            protected void applyValue() {
                config.breakDurationMs = (int) (10 + value * 190);
            }
        });

        // Place block toggle
        y += 28;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Установка блока: " + (config.placeEnabled ? "ВКЛ" : "ВЫКЛ")),
                button -> {
                    config.placeEnabled = !config.placeEnabled;
                    button.setMessage(Text.literal("Установка блока: " + (config.placeEnabled ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(centerX - 100, y, 200, 20).build());

        // Place duration slider (10ms - 150ms)
        y += 24;
        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20,
                Text.literal("Длительность установки: " + config.placeDurationMs + " мс"),
                (config.placeDurationMs - 10.0) / 140.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Длительность установки: " + config.placeDurationMs + " мс"));
            }
            @Override
            protected void applyValue() {
                config.placeDurationMs = (int) (10 + value * 140);
            }
        });

        // Test vibration button
        y += 28;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Тест вибрации"),
                button -> ZalithVibeMod.getInstance().getBridge().vibrate(60, 200)
        ).dimensions(centerX - 100, y, 200, 20).build());

        // Done button
        y += 28;
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.done"),
                button -> {
                    config.save();
                    this.client.setScreen(this.parent);
                }
        ).dimensions(centerX - 100, y, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        config.save();
        this.client.setScreen(this.parent);
    }
}
