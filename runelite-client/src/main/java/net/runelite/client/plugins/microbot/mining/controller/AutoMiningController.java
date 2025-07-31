package net.runelite.client.plugins.microbot.mining.controller;

import com.google.gson.JsonObject;
import net.runelite.client.plugins.microbot.mining.AutoMiningConfig;
import net.runelite.client.plugins.microbot.mining.AutoMiningPlugin;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginController;

import java.util.HashMap;
import java.util.Map;

public class AutoMiningController implements PluginController {
    private final AutoMiningPlugin plugin;

    public AutoMiningController(AutoMiningPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        switch (action) {
            case "start_mining":
                return startMining();
            case "stop_mining":
                return stopMining();
            case "get_status":
                return getStatus();
            case "set_config":
                return setConfig(command);
            case "get_config":
                return getConfig();
            default:
                return new ApiResponse(false, "Unknown mining action: " + action, 0);
        }
    }
    
    @Override
    public String getPluginName() {
        return "auto_mining";
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{
            "start_mining", "stop_mining", "get_status", "set_config", "get_config"
        };
    }
    
    private ApiResponse startMining() {
        try {
            plugin.startMining();
            return new ApiResponse(true, "Mining started successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to start mining: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse stopMining() {
        try {
            plugin.stopMining();
            return new ApiResponse(true, "Mining stopped successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to stop mining: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse getStatus() {
        try {
            boolean isRunning = plugin.isRunning();
            String status = isRunning ? "running" : "stopped";
            return new ApiResponse(true, "Mining status: " + status, isRunning ? 1 : 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get status: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse setConfig(JsonObject command) {
        try {
            if (command != null) {
                if (command.has("ore")) {
                    plugin.updateOreConfig(command.get("ore").getAsString());
                }
                if (command.has("distance")) {
                    plugin.updateDistanceConfig(command.get("distance").getAsInt());
                }
                if (command.has("use_bank")) {
                    plugin.updateBankConfig(command.get("use_bank").getAsBoolean());
                }
                if (command.has("items_to_bank")) {
                    plugin.updateItemsToBankConfig(command.get("items_to_bank").getAsString());
                }
                if (command.has("max_players")) {
                    plugin.updateMaxPlayersConfig(command.get("max_players").getAsInt());
                }
                
                return new ApiResponse(true, "Configuration updated successfully", 1);
            } else {
                return new ApiResponse(false, "No configuration data provided", 0);
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to update configuration: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse getConfig() {
        try {
            AutoMiningConfig config = plugin.getConfig();
            Map<String, Object> configData = new HashMap<>();
            configData.put("ore", config.ORE().name());
            configData.put("distance", config.distanceToStray());
            configData.put("use_bank", config.useBank());
            configData.put("items_to_bank", config.itemsToBank());
            configData.put("max_players", config.maxPlayersInArea());
            configData.put("items_to_keep", config.itemsToKeep());
            
            return new ApiResponse(true, "Configuration retrieved successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get configuration: " + e.getMessage(), 0);
        }
    }
}
