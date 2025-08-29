package com.gly091020.BiliBiliMedia.util;

import com.gly091020.BiliBiliMedia.BiliBiliMedia;
import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class BilibiliMediaUtil {
    private static Map<String, String> DATA;
    public static Path getDownloadPath(){
        var p = FMLPaths.GAMEDIR.get().resolve("BiliBiliMediaFiles");
        if (!p.toFile().isDirectory() && !p.toFile().mkdir()) {
            throw new RuntimeException("文件夹创建失败");
        }
        return p;
    }

    public static URI getUri(File file){
        return URI.create("http://127.0.0.1:" + SimpleFileServer.PORT + "/download?file=" + file.getName());
    }

    public static void loadJson(){
        var p = FMLPaths.GAMEDIR.get().resolve("BiliBiliMediaFiles").resolve("video.json");
        if(!p.toFile().isFile()){
            try {
                if(!p.toFile().createNewFile()){
                    throw new IOException("文件创建失败");
                }
                DATA = new HashMap<>();
                saveJson();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            DATA = gson.fromJson(new String(Files.readAllBytes(p)), new TypeToken<Map<String, String>>() {}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveJson(){
        var p = FMLPaths.GAMEDIR.get().resolve("BiliBiliMediaFiles").resolve("video.json");
        if(!p.toFile().isFile()){
            try {
                if(!p.toFile().createNewFile()){
                    throw new IOException("文件创建失败");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.write(p, gson.toJson(DATA).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static String tryGetLocalFile(String url){
        return DATA.get(url);
    }

    public static void updateVideoFile(String url, File file){
        DATA.put(url, getUri(file).toString());
        saveJson();
    }

    public static void clearFile(){
        var p = FMLPaths.GAMEDIR.get().resolve("BiliBiliMediaFiles");
        try {
            Files.delete(p);
        } catch (IOException ignored) {
        }
    }

    public static boolean isGLY(){
        return Objects.equal(BiliBiliMedia._5112151111121, Minecraft.getInstance().player.getGameProfile().getId());
    }

    public static void tryBBDown(){
        try {
            new ProcessBuilder(
                    "BBDown.exe",
                    "--help"
            ).directory(BilibiliMediaUtil.getDownloadPath().toFile()).start();
        } catch (IOException e) {
            BiliBiliMedia.LOGGER.error("BBDown测试失败：", e);
            BilibiliMediaUtil.clearFile();
            releaseBBDown();
        }
        BiliBiliMedia.LOGGER.info("BBDown可运行");
    }

    public static void releaseBBDown(){
        var outputPath = FMLPaths.GAMEDIR.get().resolve("BiliBiliMediaFiles/BBDown.exe");
        if(outputPath.toFile().isFile()){
            BiliBiliMedia.LOGGER.info("检测到BBDown");
            return;
        }
        if(!System.getProperty("os.name").toLowerCase().contains("windows")){
            BiliBiliMedia.LOGGER.info("不是Windows电脑，不需要释放BBDown");
        }
        try (InputStream input = BilibiliMediaUtil.class.getResourceAsStream("../../../../assets/bilibili_media/bbdown/BBDown.exe")) {
            if (input == null) {
                BiliBiliMedia.LOGGER.error("未找到BBDown");
                return;
            }
            Files.copy(input, outputPath, StandardCopyOption.REPLACE_EXISTING);
            boolean setExec = outputPath.toFile().setExecutable(true);
            if (!setExec) {
                BiliBiliMedia.LOGGER.warn("无法设置可执行权限: {}", outputPath);
            }
            BiliBiliMedia.LOGGER.info("释放exe成功");
        } catch (IOException e) {
            BiliBiliMedia.LOGGER.error("无法解压BBDown：", e);
        }
    }
}
