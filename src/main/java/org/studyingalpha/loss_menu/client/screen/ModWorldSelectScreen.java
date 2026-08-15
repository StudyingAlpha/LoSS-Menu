package org.studyingalpha.loss_menu.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.studyingalpha.loss_menu.config.ProgressionManager;
import org.studyingalpha.loss_menu.client.screen.CreateCanghaiScreen;
import org.studyingalpha.loss_menu.client.screen.CreateCuoweiScreen;


public class ModWorldSelectScreen extends Screen {

    private final Screen lastScreen;

    public ModWorldSelectScreen(Screen lastScreen) {
        super(Component.literal("创建世界"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();

        // 重新加载解锁状态
        ProgressionManager.reload();
        boolean timesChangeUnlocked = ProgressionManager.isUnlocked("times_change");
        boolean vanillaUnlocked = ProgressionManager.isUnlocked("vanilla");

        int buttonWidth = 200;
        int buttonHeight = 20;
        int startX = (this.width - buttonWidth) / 2;
        int startY = this.height / 2 - 40;

        // 错位的纪元（始终可用）
        Button epochButton = Button.builder(
                        Component.literal("错位的纪元"),
                        button -> {
                            if (this.minecraft != null) {
                                this.minecraft.setScreen(new CreateCuoweiScreen(this));
                            }
                        }
                ).bounds(startX, startY, buttonWidth, buttonHeight)
                .tooltip(Tooltip.create(Component.literal("从头开始游戏")))
                .build();
        this.addRenderableWidget(epochButton);

        startY += buttonHeight + 4;

        // 沧海桑田（未解锁时显示???）
        Component timesChangeText = timesChangeUnlocked
                ? Component.literal("沧海桑田")
                : Component.literal("???");
        Component timesChangeTooltip = timesChangeUnlocked
                ? Component.literal("数万年后......")
                : Component.literal("???后开放");

        Button timesChangeButton = Button.builder(
                        timesChangeText,
                        button -> {
                            if (timesChangeUnlocked) {
                                if (this.minecraft != null) {
                                    // 打开沧海桑田创建界面
                                    this.minecraft.setScreen(new CreateCanghaiScreen(this));
                                }
                            }
                        }
                ).bounds(startX, startY, buttonWidth, buttonHeight)
                .tooltip(Tooltip.create(timesChangeTooltip))
                .build();
        this.addRenderableWidget(timesChangeButton);

        startY += buttonHeight + 4;

        // 原版世界
        Component vanillaText = vanillaUnlocked
                ? Component.literal("原版世界")
                : Component.literal("原版世界（已锁定）");
        Component vanillaTooltip = vanillaUnlocked
                ? Component.literal("感受万年前的创造吧!")
                : Component.literal("完成全部剧情后开放");

        Button vanillaButton = Button.builder(
                        vanillaText,
                        button -> {
                            if (vanillaUnlocked) {
                                if (this.minecraft != null) {
                                    CreateWorldScreen.openFresh(this.minecraft, this);
                                }
                            }
                        }
                ).bounds(startX, startY, buttonWidth, buttonHeight)
                .tooltip(Tooltip.create(vanillaTooltip))
                .build();
        this.addRenderableWidget(vanillaButton);

        startY += buttonHeight + 8;

        // 返回
        Button backButton = Button.builder(
                Component.literal("返回"),
                button -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(this.lastScreen);
                    }
                }
        ).bounds(startX, startY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(backButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}