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
import net.minecraft.world.level.storage.LevelStorageSource;

public class CreateCanghaiScreen extends Screen {

    private final Screen lastScreen;
    private EditBox worldNameEdit;
    private boolean allowCheats = false;
    private boolean keepInventory = false;
    private String errorMessage = null;   // 用于显示错误信息

    public CreateCanghaiScreen(Screen lastScreen) {
        super(Component.literal("创建沧海桑田"));
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
                Component.literal("世界名称")
        );
        this.worldNameEdit.setMaxLength(64);
        this.worldNameEdit.setValue("沧海桑田");
        this.addRenderableWidget(this.worldNameEdit);

        startY += 35;

        Button cheatButton = Button.builder(
                Component.literal("允许作弊：关"),
                button -> {
                    allowCheats = !allowCheats;
                    button.setMessage(Component.literal("允许作弊：" + (allowCheats ? "开" : "关")));
                }
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(cheatButton);

        startY += 30;

        Button keepInventoryButton = Button.builder(
                Component.literal("死亡不掉落：关"),
                button -> {
                    keepInventory = !keepInventory;
                    button.setMessage(Component.literal("死亡不掉落：" + (keepInventory ? "开" : "关")));
                }
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(keepInventoryButton);

        startY += 30;

        Button createButton = Button.builder(
                Component.literal("创建"),
                button -> this.createWorld()
        ).bounds(centerX - 100, startY, 200, 20).build();
        this.addRenderableWidget(createButton);

        startY += 30;

        Button backButton = Button.builder(
                Component.literal("返回"),
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

        // 清除之前的错误信息
        errorMessage = null;

        String worldName = this.worldNameEdit.getValue().trim();
        if (worldName.isEmpty()) {
            errorMessage = "世界名称不能为空";
            return;
        }

        if (this.minecraft.getLevelSource().levelExists(worldName)) {
            errorMessage = "世界已存在，请更换名称";
            return;
        }

        Path templatePath = FMLPaths.CONFIGDIR.get().resolve("LoSS Main/templates/canghai");
        if (!Files.exists(templatePath)) {
            errorMessage = "模板文件缺失，无法创建世界";
            return;
        }

        Path savesDir = this.minecraft.getLevelSource().getBaseDir();
        Path targetPath = savesDir.resolve(worldName);

        try {
            copyDirectory(templatePath, targetPath);
            cleanPlayerData(targetPath);
            modifyLevelDat(targetPath, worldName, allowCheats, keepInventory);

            // loadLevel 只需要 Screen 和世界名称，它会内部处理 LevelStorageAccess
            // 注意：你之前用 createAccess 获取的 access 不需要在这里传入了
            this.minecraft.createWorldOpenFlows().loadLevel(this, worldName);

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "创建世界失败";
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

        // 清除玩家实体数据，避免模板制作者信息残留
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

        // 绘制错误信息
        if (errorMessage != null) {
            guiGraphics.drawCenteredString(this.font, errorMessage, this.width / 2, this.height / 2 + 80, 0xFF5555);
        }
    }
}