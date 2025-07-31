package net.runelite.client.plugins.microbot.mining;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.microbot.mining.controller.AutoMiningController;
import net.runelite.client.plugins.microbot.socketautomation.SocketAutomationPlugin;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pluginscheduler.api.SchedulablePlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.event.PluginScheduleEntrySoftStopEvent;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Auto Mining",
        description = "Mines and banks ores",
        tags = {"mining", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoMiningPlugin extends Plugin implements SchedulablePlugin {
    @Inject
    private AutoMiningConfig config;
    @Provides
    AutoMiningConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMiningConfig.class);
    }
    
    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoMiningOverlay autoMiningOverlay;
    
    @Inject
    private PluginManager pluginManager;

    @Inject
    @Getter
    AutoMiningScript autoMiningScript;
    
    @Subscribe
    @Override
    public void onPluginScheduleEntrySoftStopEvent(PluginScheduleEntrySoftStopEvent event) {
        if (event.getPlugin() == this) {
            // Cleanup operations
            Microbot.getClientThread().invokeLater(() -> {
                Microbot.stopPlugin(this);
            });
        }
    }
    
    private AutoMiningController socketController;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(autoMiningOverlay);
        }
        autoMiningScript.run(config);
        
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null && pluginManager.isPluginEnabled(socketPlugin)) {
            socketController = new AutoMiningController(this);
            socketPlugin.getControllerRegistry().registerController(socketController);
            log.info("Successfully registered AutoMiningController with SocketAutomationPlugin");
        }
    }

    protected void shutDown() {
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null) {
            socketPlugin.getControllerRegistry().unregisterController("auto_mining");
        }
        
        autoMiningScript.shutdown();
        overlayManager.remove(autoMiningOverlay);
    }
    
    public AutoMiningScript getMiningScript() {
        return autoMiningScript;
    }
    
    public void startMining() {
        if (!isRunning()) {
            autoMiningScript.run(config);
        }
    }
    
    public void stopMining() {
        if (autoMiningScript != null) {
            autoMiningScript.shutdown();
        }
    }
    
    public boolean isRunning() {
        return autoMiningScript != null && autoMiningScript.isRunning();
    }
    
    public AutoMiningConfig getConfig() {
        return config;
    }
    
    public void updateOreConfig(String ore) {
        if (configManager != null) {
            configManager.setConfiguration("Mining", "Ore", ore);
        }
    }
    
    public void updateDistanceConfig(int distance) {
        if (configManager != null) {
            configManager.setConfiguration("Mining", "DistanceToStray", distance);
        }
    }
    
    public void updateBankConfig(boolean useBank) {
        if (configManager != null) {
            configManager.setConfiguration("Mining", "UseBank", useBank);
        }
    }
    
    public void updateItemsToBankConfig(String items) {
        if (configManager != null) {
            configManager.setConfiguration("Mining", "ItemsToBank", items);
        }
    }
    
    public void updateMaxPlayersConfig(int maxPlayers) {
        if (configManager != null) {
            configManager.setConfiguration("Mining", "maxPlayersInArea", maxPlayers);
        }
    }
}
