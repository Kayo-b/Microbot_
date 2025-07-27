package net.runelite.client.plugins.microbot.nateplugins.skilling.natefishing;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.microbot.nateplugins.skilling.natefishing.controller.AutoFishController;
import net.runelite.client.plugins.microbot.socketautomation.SocketAutomationPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Nate + "Auto Fishing",
        description = "Nate's Power Fisher plugin",
        tags = {"Fishing", "nate", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoFishPlugin extends Plugin {
    @Inject
    private AutoFishConfig config;

    @Provides
    AutoFishConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoFishConfig.class);
    }
    
    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoFishOverlay fishingOverlay;
    
    @Inject
    private PluginManager pluginManager;

    @Inject
    private AutoFishingScript fishingScript;
    
    private AutoFishController socketController;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(fishingOverlay);
        }
        fishingScript.run(config);
        
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null && pluginManager.isPluginEnabled(socketPlugin)) {
            socketController = new AutoFishController(this);
            socketPlugin.getControllerRegistry().registerController(socketController);
            log.info("Successfully registered AutoFishController with SocketAutomationPlugin");
        }
    }

    protected void shutDown() {
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null) {
            socketPlugin.getControllerRegistry().unregisterController("auto_fish");
        }
        
        fishingScript.shutdown();
        overlayManager.remove(fishingOverlay);
    }
    
    public AutoFishingScript getFishingScript() {
        return fishingScript;
    }
    
    public void startFishing() {
        if (!isRunning()) {
            fishingScript.run(config);
        }
    }
    
    public void stopFishing() {
        if (fishingScript != null) {
            fishingScript.shutdown();
        }
    }
    
    public boolean isRunning() {
        return fishingScript != null && fishingScript.isRunning();
    }
    
    public AutoFishConfig getConfig() {
        return config;
    }
    
    public void updateFishConfig(String fish) {
        if (configManager != null) {
            configManager.setConfiguration("micro-fishing", "Fish", fish);
        }
    }
    
    public void updateBankConfig(boolean useBank) {
        if (configManager != null) {
            configManager.setConfiguration("micro-fishing", "useBank", useBank);
        }
    }
    
    public void updateDepositBoxConfig(boolean useDepositBox) {
        if (configManager != null) {
            configManager.setConfiguration("micro-fishing", "useDepositBox", useDepositBox);
        }
    }
    
    public void updateEchoHarpoonConfig(boolean useEchoHarpoon) {
        if (configManager != null) {
            configManager.setConfiguration("micro-fishing", "UseEchoHarpoon", useEchoHarpoon);
        }
    }
}
