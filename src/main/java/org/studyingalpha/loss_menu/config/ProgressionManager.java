package org.studyingalpha.loss_menu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProgressionManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("LoSS Main/progression.json");

    private static ProgressionData data = null;

    public static ProgressionData getData() {
        if (data == null) {
            load();
        }
        return data;
    }

    public static void load() {
        // 确保目录存在
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果文件不存在，创建默认值并保存
        if (!Files.exists(CONFIG_PATH)) {
            data = new ProgressionData();
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            data = GSON.fromJson(reader, ProgressionData.class);
            // 如果某些字段缺失，确保有默认值
            if (data == null) {
                data = new ProgressionData();
            }
        } catch (IOException e) {
            e.printStackTrace();
            data = new ProgressionData();
        }
    }

    public static void save() {
        if (data == null) {
            data = new ProgressionData();
        }
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setUnlocked(String mode, boolean value) {
        ProgressionData d = getData();
        switch (mode) {
            case "times_change" -> d.times_changeUnlocked = value;
            case "vanilla" -> d.vanillaUnlocked = value;
            default -> throw new IllegalArgumentException("Unknown mode: " + mode);
        }
        save();
    }

    public static boolean isUnlocked(String mode) {
        ProgressionData d = getData();
        return switch (mode) {
            case "times_change" -> d.times_changeUnlocked;
            case "vanilla" -> d.vanillaUnlocked;
            default -> false;
        };
    }
    public static void reload() {
        load();
    }
}