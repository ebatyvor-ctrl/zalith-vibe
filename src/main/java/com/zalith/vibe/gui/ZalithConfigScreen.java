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
        super(Text.literal("Zalith Vibe — Расширенные тактильные настройки"));
        this.parent = parent;
        this.config = ZalithVibeMod.getInstance().getConfig();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int leftX = centerX - 155;
        int rightX = centerX + 5;
        int colW = 150;
        int btnH = 20;

        // Главный переключатель вверху
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Вибрация: " + (config.enabled ? "ВКЛ" : "ВЫКЛ")),
                btn -> {
                    config.enabled = !config.enabled;
                    btn.setMessage(Text.literal("Вибрация: " + (config.enabled ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(centerX - 100, 24, 200, btnH).build());

        // Левая колонка
        int yLeft = 48;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Ломание: " + VibrationConfig.getStrengthLabel(config.breakStrength)),
                b -> { config.breakStrength = (config.breakStrength + 1) % 4; b.setMessage(Text.literal("Ломание: " + VibrationConfig.getStrengthLabel(config.breakStrength))); }
        ).dimensions(leftX, yLeft, colW, btnH).build());

        yLeft += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Установка: " + VibrationConfig.getStrengthLabel(config.placeStrength)),
                b -> { config.placeStrength = (config.placeStrength + 1) % 4; b.setMessage(Text.literal("Установка: " + VibrationConfig.getStrengthLabel(config.placeStrength))); }
        ).dimensions(leftX, yLeft, colW, btnH).build());

        yLeft += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Удар моба: " + VibrationConfig.getStrengthLabel(config.attackStrength)),
                b -> { config.attackStrength = (config.attackStrength + 1) % 4; b.setMessage(Text.literal("Удар моба: " + VibrationConfig.getStrengthLabel(config.attackStrength))); }
        ).dimensions(leftX, yLeft, colW, btnH).build());

        yLeft += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Лук / Арбалет: " + (config.bowEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.bowEnabled = !config.bowEnabled; b.setMessage(Text.literal("Лук / Арбалет: " + (config.bowEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(leftX, yLeft, colW, btnH).build());

        yLeft += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Рыбалка (поклёвка): " + (config.fishingEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.fishingEnabled = !config.fishingEnabled; b.setMessage(Text.literal("Рыбалка (поклёвка): " + (config.fishingEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(leftX, yLeft, colW, btnH).build());

        yLeft += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Пульс сердца (<3 HP): " + (config.heartbeatEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.heartbeatEnabled = !config.heartbeatEnabled; b.setMessage(Text.literal("Пульс сердца (<3 HP): " + (config.heartbeatEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(leftX, yLeft, colW, btnH).build());

        // Правая колонка
        int yRight = 48;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Крипер / TNT: " + (config.creeperEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.creeperEnabled = !config.creeperEnabled; b.setMessage(Text.literal("Крипер / TNT: " + (config.creeperEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(rightX, yRight, colW, btnH).build());

        yRight += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Щит и Криты: " + (config.shieldCritEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.shieldCritEnabled = !config.shieldCritEnabled; b.setMessage(Text.literal("Щит и Криты: " + (config.shieldCritEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(rightX, yRight, colW, btnH).build());

        yRight += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Смена хотбара: " + (config.hotbarEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.hotbarEnabled = !config.hotbarEnabled; b.setMessage(Text.literal("Смена хотбара: " + (config.hotbarEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(rightX, yRight, colW, btnH).build());

        yRight += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Поломка вещей: " + (config.itemBreakEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.itemBreakEnabled = !config.itemBreakEnabled; b.setMessage(Text.literal("Поломка вещей: " + (config.itemBreakEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(rightX, yRight, colW, btnH).build());

        yRight += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Поедание еды: " + (config.eatEnabled ? "ВКЛ" : "ВЫКЛ")),
                b -> { config.eatEnabled = !config.eatEnabled; b.setMessage(Text.literal("Поедание еды: " + (config.eatEnabled ? "ВКЛ" : "ВЫКЛ"))); }
        ).dimensions(rightX, yRight, colW, btnH).build());

        yRight += 23;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Тест вибрации"),
                b -> ZalithVibeMod.getInstance().getBridge().vibrate(2)
        ).dimensions(rightX, yRight, colW, btnH).build());

        // Кнопка сохранения внизу
        int yBottom = Math.max(yLeft, yRight) + 5;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Сохранить и закрыть"),
                b -> {
                    config.save();
                    if (this.client != null) this.client.setScreen(this.parent);
                }
        ).dimensions(centerX - 100, yBottom, 200, btnH).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public void close() {
        config.save();
        if (this.client != null) this.client.setScreen(this.parent);
    }
}
