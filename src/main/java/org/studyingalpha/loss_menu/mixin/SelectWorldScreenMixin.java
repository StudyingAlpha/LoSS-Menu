package org.studyingalpha.loss_menu.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.studyingalpha.loss_menu.client.screen.ModWorldSelectScreen;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin extends Screen {

    protected SelectWorldScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void loss_menu$replaceCreateButtonAndFixAutoOpen(CallbackInfo ci) {
        // 1. 替换“创建新世界”按钮行为
        for (GuiEventListener child : this.children()) {
            if (child instanceof Button button &&
                    button.getMessage().equals(Component.translatable("selectWorld.create"))) {

                int x = button.getX();
                int y = button.getY();
                int w = button.getWidth();
                int h = button.getHeight();
                Component message = button.getMessage();

                this.removeWidget(button);

                Button newButton = Button.builder(message, b -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new ModWorldSelectScreen(this));
                    }
                }).bounds(x, y, w, h).build();

                this.addRenderableWidget(newButton);
                break;
            }
        }

        // 2. 修正自动跳转：如果 init 结束后当前屏幕已经变成了原版创建界面，
        //    说明存档列表为空触发了自动跳转，我们将其替换为自定义选择界面
        if (this.minecraft != null && this.minecraft.screen instanceof CreateWorldScreen) {
            this.minecraft.setScreen(new ModWorldSelectScreen(this));
        }
    }
}