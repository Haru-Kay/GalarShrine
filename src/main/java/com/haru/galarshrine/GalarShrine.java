package com.haru.galarshrine;

import com.haru.galarshrine.command.GiveGalarOrbCommand;
import com.haru.galarshrine.config.GalarShrineConfig;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(GalarShrine.MOD_ID)
@Mod.EventBusSubscriber(modid = GalarShrine.MOD_ID)
public class GalarShrine {

    public static final String MOD_ID = "galarshrine";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static GalarShrine instance;

    private GalarShrineConfig config;

    public GalarShrine() {
        instance = this;

        reloadConfig();

        MinecraftForge.EVENT_BUS.register(this);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(GalarShrine::onModLoad);
    }

    public static void onModLoad(FMLCommonSetupEvent event) {
        // Here is how you register a listener for Pixelmon events
        // Pixelmon has its own event bus for its events, as does TCG
        // So any event listener for those mods need to be registered to those specific event buses
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        // Logic for when the server is starting here
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(GalarShrineConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event) {
        // Logic for once the server has started here
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new GiveGalarOrbCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        // Logic for when the server is stopping
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        // Logic for when the server is stopped
    }

    public static GalarShrine getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static GalarShrineConfig getConfig() {
        return instance.config;
    }
}
