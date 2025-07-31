package net.runelite.client.plugins.microbot.cooking;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cooking.controller.AutoCookingController;
import net.runelite.client.plugins.microbot.cooking.scripts.AutoCookingScript;
import net.runelite.client.plugins.microbot.cooking.scripts.BurnBakingScript;
import net.runelite.client.plugins.microbot.socketautomation.SocketAutomationPlugin;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.plugins.microbot.pluginscheduler.api.SchedulablePlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.event.PluginScheduleEntrySoftStopEvent;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.GMason + "Auto Cooking",
        description = "Microbot cooking plugin",
        tags = {"cooking", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoCookingPlugin extends Plugin implements SchedulablePlugin {
    public static double version = 1.1;
    @Inject
    @Getter
    public AutoCookingScript autoCookingScript;
    @Inject
    @Getter  
    public BurnBakingScript burnBakingScript;
    @Inject
    private AutoCookingConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoCookingOverlay overlay;
    
    private AutoCookingController socketController;

    @Provides
    AutoCookingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCookingConfig.class);
    }

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


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        switch (config.cookingActivity()) {
            case COOKING:
                autoCookingScript.run(config);
                break;
            case BURN_BAKING:
                burnBakingScript.run(config);
                break;
            default:
                Microbot.log("Invalid Cooking Activity");
        }
        
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null && pluginManager.isPluginEnabled(socketPlugin)) {
            socketController = new AutoCookingController(this);
            socketPlugin.getControllerRegistry().registerController(socketController);
            log.info("Successfully registered AutoCookingController with SocketAutomationPlugin");
        }
    }

    protected void shutDown() {
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null) {
            socketPlugin.getControllerRegistry().unregisterController("auto_cooking");
        }
        
        autoCookingScript.shutdown();
        burnBakingScript.shutdown();
        overlayManager.remove(overlay);
    }
    
    public void startCooking() {
        if (!isRunning()) {
            switch (config.cookingActivity()) {
                case COOKING:
                    autoCookingScript.run(config);
                    break;
                case BURN_BAKING:
                    burnBakingScript.run(config);
                    break;
                default:
                    Microbot.log("Invalid Cooking Activity");
            }
        }
    }
    
    public void stopCooking() {
        if (autoCookingScript != null) {
            autoCookingScript.shutdown();
        }
        if (burnBakingScript != null) {
            burnBakingScript.shutdown();
        }
    }
    
    public boolean isRunning() {
        boolean autoRunning = autoCookingScript != null && autoCookingScript.isRunning();
        boolean burnRunning = burnBakingScript != null && burnBakingScript.isRunning();
        return autoRunning || burnRunning;
    }
    
    public AutoCookingConfig getConfig() {
        return config;
    }
    
    public void updateCookingActivityConfig(String activity) {
        if (configManager != null) {
            configManager.setConfiguration("autocooking", "cookingActivity", activity);
        }
    }
    
    public void updateCookingItemConfig(String item) {
        if (configManager != null) {
            configManager.setConfiguration("autocooking", "cookingItem", item);
        }
    }
    
    public void updateCookingLocationConfig(String location) {
        if (configManager != null) {
            configManager.setConfiguration("autocooking", "cookingLocation", location);
        }
    }
    
    public void updateNearestLocationConfig(boolean useNearest) {
        if (configManager != null) {
            configManager.setConfiguration("autocooking", "useNearestCookingLocation", useNearest);
        }
    }
    
    public void updateDropBurntItemsConfig(boolean dropBurnt) {
        if (configManager != null) {
            configManager.setConfiguration("autocooking", "shouldDropBurntItems", dropBurnt);
        }
    }
}
