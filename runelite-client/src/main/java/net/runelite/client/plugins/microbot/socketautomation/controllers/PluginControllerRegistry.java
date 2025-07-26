package net.runelite.client.plugins.microbot.socketautomation.controllers;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PluginControllerRegistry {
    private final Map<String, PluginController> controllers = new ConcurrentHashMap<>();
    
    public void registerController(PluginController controller) {
        String pluginName = controller.getPluginName().toLowerCase();
        controllers.put(pluginName, controller);
        log.info("Registered plugin controller for: {}", controller.getPluginName());
    }
    
    public void unregisterController(String pluginName) {
        PluginController removed = controllers.remove(pluginName.toLowerCase());
        if (removed != null) {
            log.info("Unregistered plugin controller for: {}", pluginName);
        }
    }
    
    public ApiResponse processCommand(String action, JsonObject command) {
        try {
            if (command.has("plugin")) {
                String pluginName = command.get("plugin").getAsString().toLowerCase();
                PluginController controller = controllers.get(pluginName);
                
                if (controller != null) {
                    return controller.processCommand(action, command);
                } else {
                    return new ApiResponse(false, "No controller registered for plugin: " + pluginName, 0);
                }
            }
            
            for (PluginController controller : controllers.values()) {
                String[] supportedActions = controller.getSupportedActions();
                if (Arrays.asList(supportedActions).contains(action)) {
                    return controller.processCommand(action, command);
                }
            }
            
            return new ApiResponse(false, "Unknown action: " + action, 0);
            
        } catch (Exception e) {
            log.error("Error processing command in controller registry: {}", e.getMessage(), e);
            return new ApiResponse(false, "Error processing command: " + e.getMessage(), 0);
        }
    }
    
    public Set<String> getSupportedPlugins() {
        Set<String> plugins = new HashSet<>();
        for (PluginController controller : controllers.values()) {
            plugins.add(controller.getPluginName());
        }
        return plugins;
    }
    
    public boolean hasController(String pluginName) {
        return controllers.containsKey(pluginName.toLowerCase());
    }
    
    public PluginController getController(String pluginName) {
        return controllers.get(pluginName.toLowerCase());
    }
}
