package net.runelite.client.plugins.microbot.woodcutting;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pluginscheduler.api.SchedulablePlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.event.PluginScheduleEntrySoftStopEvent;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.microbot.socketautomation.SocketAutomationPlugin;
import net.runelite.client.plugins.microbot.woodcutting.controller.AutoWoodcuttingController;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Auto Woodcutting",
        description = "Microbot woodcutting plugin",
        tags = {"Woodcutting", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoWoodcuttingPlugin extends Plugin implements SchedulablePlugin {
    @Inject
    private AutoWoodcuttingConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoWoodcuttingOverlay woodcuttingOverlay;
    @Inject
    @Getter
    public AutoWoodcuttingScript autoWoodcuttingScript;
    
    private AutoWoodcuttingController socketController;

    @Provides
    AutoWoodcuttingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoWoodcuttingConfig.class);
    }

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
            overlayManager.add(woodcuttingOverlay);
        }
        autoWoodcuttingScript.run(config);
        
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null && pluginManager.isPluginEnabled(socketPlugin)) {
            socketController = new AutoWoodcuttingController(this);
            socketPlugin.getControllerRegistry().registerController(socketController);
            log.info("Successfully registered AutoWoodcuttingController with SocketAutomationPlugin");
        }
    }

    protected void shutDown() {
        SocketAutomationPlugin socketPlugin = (SocketAutomationPlugin) pluginManager.getPlugins().stream()
            .filter(p -> p instanceof SocketAutomationPlugin)
            .findFirst()
            .orElse(null);
            
        if (socketPlugin != null) {
            socketPlugin.getControllerRegistry().unregisterController("auto_woodcutting");
        }
        
        autoWoodcuttingScript.shutdown();
        overlayManager.remove(woodcuttingOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = event.getMessage().toLowerCase();
            if (message.equals("you can't light a fire here.")){
                autoWoodcuttingScript.cannotLightFire = true;
            }
        }
    }
    
    // Socket automation methods
    public void startWoodcutting() {
        if (!isRunning()) {
            autoWoodcuttingScript.run(config);
        }
    }
    
    public void stopWoodcutting() {
        if (autoWoodcuttingScript != null) {
            autoWoodcuttingScript.shutdown();
        }
    }
    
    public boolean isRunning() {
        return autoWoodcuttingScript != null && autoWoodcuttingScript.isRunning();
    }
    
    public boolean isCannotLightFire() {
        return autoWoodcuttingScript.cannotLightFire;
    }
    
    public AutoWoodcuttingConfig getConfig() {
        return config;
    }
    
    public void updateTreeTypeConfig(String treeType) {
        if (configManager != null) {
            configManager.setConfiguration("Woodcutting", "Tree", treeType);
        }
    }
    
    public void updateDistanceToStrayConfig(int distance) {
        if (configManager != null) {
            configManager.setConfiguration("Woodcutting", "DistanceToStray", distance);
        }
    }
    
    public void updateHopWhenPlayerDetectedConfig(boolean hopWhenPlayerDetected) {
        if (configManager != null) {
            configManager.setConfiguration("Woodcutting", "Hop", hopWhenPlayerDetected);
        }
    }
    
    public void updateFiremakeOnlyConfig(boolean firemakeOnly) {
        if (configManager != null) {
            configManager.setConfiguration("Woodcutting", "Firemake", firemakeOnly);
        }
    }
    
    public void updateResetOptionsConfig(String resetOptions) {
        if (configManager != null) {
            configManager.setConfiguration("Woodcutting", "ItemAction", resetOptions);
        }
    }
    
    public void updateItemsToBankConfig(String itemsToBank) {
        if (configManager != null) {
            configManager.setConfiguration("Woodcutting", "ItemsToBank", itemsToBank);
        }
    }
    
    public void updateWalkBackConfig(String walkBack) {
        if (configManager != null) {
            configManager.setConfiguration("Woodcutting", "WalkBack", walkBack);
        }
    }
}
