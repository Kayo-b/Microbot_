package net.runelite.client.plugins.microbot.socketautomation;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.socketautomation.controllers.ConfigController;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginControllerRegistry;

import javax.inject.Inject;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Socket Automation",
        description = "Provides socket-based API automation for controlling Microbot plugins remotely",
        tags = {"automation", "socket", "api", "remote", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class SocketAutomationPlugin extends Plugin {
    
    @Inject
    private SocketAutomationConfig config;
    
    @Inject
    private ConfigManager configManager;
    
    private SocketAutomationManager socketManager;
    
    @Provides
    SocketAutomationConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SocketAutomationConfig.class);
    }
    
    @Override
    protected void startUp() {
        socketManager = new SocketAutomationManager(config);
        
        ConfigController configController = new ConfigController(configManager);
        socketManager.getControllerRegistry().registerController(configController);
        
        if (config.autoStartEnabled()) {
            socketManager.startSocketListener();
            Microbot.log("Socket Automation Plugin started - Listening on port: " + config.primaryPort());
        } else {
            Microbot.log("Socket Automation Plugin started - Auto-start disabled");
        }
    }
    
    @Override
    protected void shutDown() {
        if (socketManager != null) {
            socketManager.shutdown();
            Microbot.log("Socket Automation Plugin stopped");
        }
    }
    
    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(SocketAutomationConfig.GROUP_NAME)) {
            return;
        }
        
        String key = event.getKey();
        
        if ("autoStartEnabled".equals(key)) {
            if (config.autoStartEnabled() && !socketManager.isRunning()) {
                socketManager.startSocketListener();
                Microbot.log("Socket listener started via config change");
            } else if (!config.autoStartEnabled() && socketManager.isRunning()) {
                socketManager.shutdown();
                Microbot.log("Socket listener stopped via config change");
            }
        }
        
        if ("primaryPort".equals(key)) {
            if (socketManager.isRunning()) {
                socketManager.shutdown();
                socketManager = new SocketAutomationManager(config);
                socketManager.startSocketListener();
                Microbot.log("Socket listener restarted on new port: " + config.primaryPort());
            }
        }
    }
    
    public SocketAutomationManager getSocketManager() {
        return socketManager;
    }
    
    public PluginControllerRegistry getControllerRegistry() {
        return socketManager != null ? socketManager.getControllerRegistry() : null;
    }
}
