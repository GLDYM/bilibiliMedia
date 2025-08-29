package com.gly091020.BiliBiliMedia.config;

import com.gly091020.BiliBiliMedia.BiliBiliMedia;
import com.gly091020.BiliBiliMedia.util.BilibiliMediaUtil;
import com.gly091020.BiliBiliMedia.util.SimpleFileServer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


@Config(name = BiliBiliMedia.ModID)
public class BiliBiliMediaConfig implements ConfigData {
    public boolean enable = true;
    public boolean enableRangeRequests = false;
    public boolean clearOnStart = false;

    public static Screen getConfigScreen(Screen parent){
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.bilibili_media.config"))
                .setDefaultBackgroundTexture(ResourceLocation.fromNamespaceAndPath(BiliBiliMedia.ModID,  BilibiliMediaUtil.isGLY() ? "textures/gui/gly091020.png" : "icon.png"));
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("title.bilibili_media.config"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.bilibili_media.enable"), BiliBiliMedia.config.enable)
                .setSaveConsumer(x->BiliBiliMedia.config.enable = x)
                .setDefaultValue(true)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.bilibili_media.enable_range_requests"), BiliBiliMedia.config.enableRangeRequests)
                .setSaveConsumer(x->BiliBiliMedia.config.enableRangeRequests = x)
                .setTooltip(Component.translatable("config.bilibili_media.enable_range_requests.tip"))
                .setDefaultValue(false)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.bilibili_media.clear_on_start"), BiliBiliMedia.config.clearOnStart)
                .setSaveConsumer(x->BiliBiliMedia.config.clearOnStart = x)
                .setDefaultValue(false)
                .requireRestart()
                .build());
        builder.setSavingRunnable(() -> {
            AutoConfig.getConfigHolder(BiliBiliMediaConfig.class).setConfig(BiliBiliMedia.config);
            AutoConfig.getConfigHolder(BiliBiliMediaConfig.class).save();
            BiliBiliMedia.config = AutoConfig.getConfigHolder(BiliBiliMediaConfig.class).getConfig();
            SimpleFileServer.enableRangeRequests = BiliBiliMedia.config.enableRangeRequests;
        });
        return builder.build();
    }
}
