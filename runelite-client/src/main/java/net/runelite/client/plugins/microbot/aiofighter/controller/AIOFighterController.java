package net.runelite.client.plugins.microbot.aiofighter.controller;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.aiofighter.AIOFighterConfig;
import net.runelite.client.plugins.microbot.aiofighter.AIOFighterPlugin;
import net.runelite.client.plugins.microbot.aiofighter.enums.State;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginController;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;

/**
 * Controller for the AIO Fighter plugin socket automation
 * Provides remote control over combat operations via socket commands
 */
@Slf4j
public class AIOFighterController implements PluginController {
    private final AIOFighterPlugin plugin;

    public AIOFighterController(AIOFighterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPluginName() {
        return "aio_fighter";
    }

    @Override
    public String[] getSupportedActions() {
        return new String[]{
            "start_fighting", "stop_fighting", "get_status", "set_config", "get_config",
            "set_center_tile", "set_safe_spot", "add_npc", "remove_npc"
        };
    }

    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        try {
            switch (action.toLowerCase()) {
                case "start_fighting":
                    plugin.startFighting();
                    return new ApiResponse(true, "Fighter started successfully", 0);

                case "stop_fighting":
                    plugin.stopFighting();
                    return new ApiResponse(true, "Fighter stopped successfully", 0);

                case "get_status":
                    State currentState = plugin.getCurrentState();
                    boolean isRunning = plugin.isRunning();
                    JsonObject statusData = new JsonObject();
                    statusData.addProperty("running", isRunning);
                    statusData.addProperty("state", currentState.toString());
                    statusData.addProperty("cooldown", AIOFighterPlugin.getCooldown());
                    return new ApiResponse(true, "Status retrieved", 0, statusData);

                case "set_center_tile":
                    return handleSetCenterTile(command);

                case "set_safe_spot":
                    return handleSetSafeSpot(command);

                case "set_config":
                    return handleConfigUpdate(command);

                case "get_config":
                    AIOFighterConfig config = plugin.getConfig();
                    JsonObject configData = new JsonObject();
                    configData.addProperty("attackable_npcs", config.attackableNpcs());
                    configData.addProperty("use_food", config.toggleFood());
                    configData.addProperty("use_prayer", config.togglePrayer());
                    configData.addProperty("use_cannon", config.toggleCannon());
                    configData.addProperty("loot_items", config.toggleLootItems());
                    return new ApiResponse(true, "Configuration retrieved", 0, configData);

                case "add_npc":
                    return handleAddNpc(command);

                case "remove_npc":
                    return handleRemoveNpc(command);

                default:
                    return new ApiResponse(false, "Unknown action: " + action, 0);
            }
        } catch (Exception e) {
            log.error("Error processing command for {}: {}", getPluginName(), e.getMessage());
            return new ApiResponse(false, "Command processing failed: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleSetCenterTile(JsonObject command) {
        try {
            int x = command.get("x").getAsInt();
            int y = command.get("y").getAsInt();
            int plane = command.has("plane") ? command.get("plane").getAsInt() : 0;
            
            WorldPoint centerPoint = new WorldPoint(x, y, plane);
            plugin.setCenterTile(centerPoint);
            
            return new ApiResponse(true, "Center tile set successfully", 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to set center tile: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleSetSafeSpot(JsonObject command) {
        try {
            int x = command.get("x").getAsInt();
            int y = command.get("y").getAsInt();
            int plane = command.has("plane") ? command.get("plane").getAsInt() : 0;
            
            WorldPoint safeSpot = new WorldPoint(x, y, plane);
            plugin.setSafeSpotTile(safeSpot);
            
            return new ApiResponse(true, "Safe spot set successfully", 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to set safe spot: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleAddNpc(JsonObject command) {
        try {
            String npcName = command.get("npc_name").getAsString();
            plugin.addNpcToAttackList(npcName);
            
            return new ApiResponse(true, "NPC added to attack list", 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to add NPC: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleRemoveNpc(JsonObject command) {
        try {
            String npcName = command.get("npc_name").getAsString();
            plugin.removeNpcFromAttackList(npcName);
            
            return new ApiResponse(true, "NPC removed from attack list", 0);
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to remove NPC: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleConfigUpdate(JsonObject command) {
        try {
            if (command.has("use_food")) {
                plugin.updateUseFoodConfig(command.get("use_food").getAsBoolean());
            }
            if (command.has("use_prayer")) {
                plugin.updateUsePrayerConfig(command.get("use_prayer").getAsBoolean());
            }
            if (command.has("use_cannon")) {
                plugin.updateUseCannonConfig(command.get("use_cannon").getAsBoolean());
            }
            if (command.has("loot_items")) {
                plugin.updateLootItemsConfig(command.get("loot_items").getAsString());
            }
            if (command.has("attackable_npcs")) {
                plugin.updateAttackableNpcsConfig(command.get("attackable_npcs").getAsString());
            }

            return new ApiResponse(true, "Configuration updated successfully", 0);
        } catch (Exception e) {
            log.error("Error updating configuration for {}: {}", getPluginName(), e.getMessage());
            return new ApiResponse(false, "Configuration update failed: " + e.getMessage(), 0);
        }
    }
}
