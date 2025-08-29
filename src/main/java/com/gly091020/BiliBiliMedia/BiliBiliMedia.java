package com.gly091020.BiliBiliMedia;

import com.gly091020.BiliBiliMedia.config.BiliBiliMediaConfig;
import com.gly091020.BiliBiliMedia.util.BilibiliMediaUtil;
import com.gly091020.BiliBiliMedia.util.SimpleFileServer;
import com.sun.net.httpserver.HttpServer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@Mod(BiliBiliMedia.ModID)
public class BiliBiliMedia {
    public static final String ModID = "bilibili_media";
    public static final Logger LOGGER = LoggerFactory.getLogger(ModID);
    public static BiliBiliMediaConfig config;
    public static HttpServer server;
    public static final UUID _5112151111121 = UUID.fromString("91bd580f-5f17-4e30-872f-2e480dd9a220");

    public BiliBiliMedia(ModContainer container) {
        AutoConfig.register(BiliBiliMediaConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BiliBiliMediaConfig.class).getConfig();
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) ->
                BiliBiliMediaConfig.getConfigScreen(parent));
        try {
            server = SimpleFileServer.startServer();
        } catch (IOException e) {
            LOGGER.error("服务器启动失败！", e);
            throw new RuntimeException(e);
        }
        if(config.clearOnStart){
            BilibiliMediaUtil.clearFile();
        }
        BilibiliMediaUtil.releaseBBDown();
        BilibiliMediaUtil.tryBBDown();
        BilibiliMediaUtil.loadJson();
    }
}
