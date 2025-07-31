package net.runelite.client.plugins.microbot.nateplugins.skilling.natefishing.controller;

import com.google.gson.JsonObject;
import net.runelite.client.plugins.microbot.nateplugins.skilling.natefishing.AutoFishConfig;
import net.runelite.client.plugins.microbot.nateplugins.skilling.natefishing.AutoFishPlugin;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginController;

import java.util.HashMap;
import java.util.Map;

public class AutoFishController implements PluginController {
    private final AutoFishPlugin plugin;

    public AutoFishController(AutoFishPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        switch (action) {
            case "start_fishing":
                return startFishing();
            case "stop_fishing":
                return stopFishing();
            case "get_status":
                return getStatus();
            case "set_config":
                return setConfig(command);
            case "get_config":
                return getConfig();
            default:
                return new ApiResponse(false, "Unknown fishing action: " + action, 0);
        }
    }
    
    @Override
    public String getPluginName() {
        return "auto_fish";
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{
            "start_fishing", "stop_fishing", "get_status", "set_config", "get_config"
        };
    }
    
    private ApiResponse startFishing() {
        try {
            plugin.startFishing();
            return new ApiResponse(true, "Fishing started successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to start fishing: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse stopFishing() {
        try {
            plugin.stopFishing();
            return new ApiResponse(true, "Fishing stopped successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to stop fishing: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse getStatus() {
        try {
            boolean isRunning = plugin.isRunning();
            String status = isRunning ? "running" : "stopped";
            return new ApiResponse(true, "Fishing status: " + status, isRunning ? 1 : 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get status: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse setConfig(JsonObject command) {
        try {
            if (command != null) {
                if (command.has("fish")) {
                    plugin.updateFishConfig(command.get("fish").getAsString());
                }
                if (command.has("use_bank")) {
                    plugin.updateBankConfig(command.get("use_bank").getAsBoolean());
                }
                if (command.has("use_deposit_box")) {
                    plugin.updateDepositBoxConfig(command.get("use_deposit_box").getAsBoolean());
                }
                if (command.has("use_echo_harpoon")) {
                    plugin.updateEchoHarpoonConfig(command.get("use_echo_harpoon").getAsBoolean());
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
            AutoFishConfig config = plugin.getConfig();
            Map<String, Object> configData = new HashMap<>();
            configData.put("fish", config.fish().name());
            configData.put("use_bank", config.useBank());
            configData.put("use_deposit_box", config.useDepositBox());
            configData.put("use_echo_harpoon", config.useEchoHarpoon());
            
            return new ApiResponse(true, "Configuration retrieved successfully", 1);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to get configuration: " + e.getMessage(), 0);
        }
    }
}
