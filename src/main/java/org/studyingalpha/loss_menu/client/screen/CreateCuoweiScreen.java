package org.studyingalpha.loss_menu.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

public class CreateCuoweiScreen extends Screen {

    private final Screen lastScreen;
    private EditBox worldNameEdit;
    private boolean allowCheats = false;
    private boolean keepInventory = false;
    private Component errorMessage = null;

    public CreateCuoweiScreen(Screen lastScreen) {
        super(Component.translatable("loss_menu.create.cuowei.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = this.height / 2 - 70;

        this.worldNameEdit = new EditBox(
                this.font,
                centerX - 100,
                startY,
                200,
                20,
                Component.translatable("loss_menu.create.world_name")
        );
        this.worldNameEdit.setMaxLength(64);
        this.worldNameEdit.setValue(Component.translatable("loss_menu.create.cuowei.default_name").getString());
        this.addRenderableWidget(this.worldNameEdit);

        startY += 35;

        Button cheatButton = Button.builder(
                Component.translatable("loss_menu.create.allow_cheats.off"),
                button -> {
                    allowCheats = !allowCheats;
                    button.setMessage(Component.translatable(allowCheats ?
                            "loss_menu.create.allow_cheats.on" : "loss_menu.create.allow_cheats.off"));
                }
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(cheatButton);

        startY += 30;

        Button keepInventoryButton = Button.builder(
                Component.translatable("loss_menu.create.keep_inventory.off"),
                button -> {
                    keepInventory = !keepInventory;
                    button.setMessage(Component.translatable(keepInventory ?
                            "loss_menu.create.keep_inventory.on" : "loss_menu.create.keep_inventory.off"));
                }
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(keepInventoryButton);

        startY += 30;

        Button createButton = Button.builder(
                Component.translatable("loss_menu.create.create"),
                button -> this.createWorld()
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(createButton);

        startY += 30;

        Button backButton = Button.builder(
                Component.translatable("loss_menu.create.back"),
                button -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(this.lastScreen);
                    }
                }
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(backButton);
    }

    private void createWorld() {
        if (this.minecraft == null) return;

        errorMessage = null;

        String worldName = this.worldNameEdit.getValue().trim();
        if (worldName.isEmpty()) {
            errorMessage = Component.translatable("loss_menu.create.error.empty_name");
            return;
        }

        if (this.minecraft.getLevelSource().levelExists(worldName)) {
            errorMessage = Component.translatable("loss_menu.create.error.world_exists");
            return;
        }

        Path templatePath = FMLPaths.CONFIGDIR.get().resolve("LoSS Main/templates/cuowei");
        if (!Files.exists(templatePath)) {
            errorMessage = Component.translatable("loss_menu.create.error.template_missing");
            return;
        }

        Path savesDir = this.minecraft.getLevelSource().getBaseDir();
        Path targetPath = savesDir.resolve(worldName);

        try {
            copyDirectory(templatePath, targetPath);
            cleanPlayerData(targetPath);
            modifyLevelDat(targetPath, worldName, allowCheats, keepInventory);

            // 直接进入新世界
            this.minecraft.createWorldOpenFlows().loadLevel(this, worldName);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = Component.translatable("loss_menu.create.error.failed");
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        if (!Files.exists(source)) {
            throw new IOException("模板目录不存在: " + source);
        }

        try (Stream<Path> paths = Files.walk(source)) {
            paths.forEach(path -> {
                Path relativePath = source.relativize(path);
                Path dest = target.resolve(relativePath);

                String fileName = path.getFileName().toString();
                if (fileName.equals("session.lock") ||
                        fileName.equals("stats") ||
                        fileName.equals("playerdata") ||
                        fileName.equals("advancements")) {
                    return;
                }

                try {
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.createDirectories(dest.getParent());
                        Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void cleanPlayerData(Path worldDir) throws IOException {
        Path[] dirsToDelete = {
                worldDir.resolve("playerdata"),
                worldDir.resolve("stats"),
                worldDir.resolve("advancements")
        };
        for (Path dir : dirsToDelete) {
            if (Files.exists(dir)) {
                try (Stream<Path> paths = Files.walk(dir)) {
                    paths.sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            }
        }
    }

    private void modifyLevelDat(Path worldDir, String worldName, boolean allowCheats, boolean keepInventory) throws IOException {
        Path levelDatPath = worldDir.resolve("level.dat");
        if (!Files.exists(levelDatPath)) {
            throw new IOException("level.dat 不存在: " + levelDatPath);
        }

        CompoundTag root = NbtIo.readCompressed(levelDatPath.toFile());
        CompoundTag data = root.getCompound("Data");

        data.putString("LevelName", worldName);
        data.putBoolean("allowCommands", allowCheats);

        CompoundTag gameRules = data.getCompound("GameRules");
        gameRules.putString("keepInventory", keepInventory ? "true" : "false");
        data.put("GameRules", gameRules);

        // 清除模板制作时的玩家数据，确保新玩家从零开始
        data.remove("Player");

        // 重置时间
        data.putLong("Time", 0L);
        data.putLong("DayTime", 0L);
        data.putLong("GameTime", 0L);
        data.putLong("LastPlayed", System.currentTimeMillis());

        NbtIo.writeCompressed(root, levelDatPath.toFile());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        if (errorMessage != null) {
            guiGraphics.drawCenteredString(this.font, errorMessage, this.width / 2, this.height / 2 + 80, 0xFF5555);
        }
    }
}