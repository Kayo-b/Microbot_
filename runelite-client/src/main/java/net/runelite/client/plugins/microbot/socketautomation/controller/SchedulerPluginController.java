package net.runelite.client.plugins.microbot.socketautomation.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.pluginscheduler.SchedulerPlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.model.PluginScheduleEntry;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;
import net.runelite.client.plugins.microbot.socketautomation.controller.PluginController;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

@Slf4j
public class SchedulerPluginController implements PluginController {

    private final SchedulerPlugin schedulerPlugin;
    private final Gson gson = new Gson();

    public SchedulerPluginController(SchedulerPlugin schedulerPlugin) {
        this.schedulerPlugin = schedulerPlugin;
    }

    @Override
    public String getPluginName() {
        return "scheduler";
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{"start", "stop", "pause", "resume", "status", "schedules", "add_schedule", "remove_schedule", "load_schedules", "save_schedules"};
    }

    @Override
    public ApiResponse processCommand(String action, JsonObject data) {
        try {
            switch (action.toLowerCase()) {
                case "start":
                    return handleStart();
                case "stop":
                    return handleStop();
                case "pause":
                    return handlePause();
                case "resume":
                    return handleResume();
                case "status":
                    return handleStatus();
                case "schedules":
                    return handleGetSchedules();
                case "add_schedule":
                    return handleAddSchedule(data);
                case "remove_schedule":
                    return handleRemoveSchedule(data);
                case "load_schedules":
                    return handleLoadSchedules(data);
                case "save_schedules":
                    return handleSaveSchedules(data);
                default:
                    return new ApiResponse(false, "Unknown scheduler action: " + action, 0);
            }
        } catch (Exception e) {
            log.error("Error processing scheduler command", e);
            return new ApiResponse(false, "Error processing command: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleStart() {
        if (schedulerPlugin.resumeScheduler()) {
            return new ApiResponse(true, "Scheduler started successfully", 0);
        } else {
            return new ApiResponse(false, "Failed to start scheduler", 0);
        }
    }

    private ApiResponse handleStop() {
        if (schedulerPlugin.pauseScheduler()) {
            return new ApiResponse(true, "Scheduler stopped successfully", 0);
        } else {
            return new ApiResponse(false, "Failed to stop scheduler", 0);
        }
    }

    private ApiResponse handlePause() {
        if (schedulerPlugin.pauseScheduler()) {
            return new ApiResponse(true, "Scheduler paused successfully", 0);
        } else {
            return new ApiResponse(false, "Failed to pause scheduler", 0);
        }
    }

    private ApiResponse handleResume() {
        if (schedulerPlugin.resumeScheduler()) {
            return new ApiResponse(true, "Scheduler resumed successfully", 0);
        } else {
            return new ApiResponse(false, "Failed to resume scheduler", 0);
        }
    }

    private ApiResponse handleStatus() {
        JsonObject statusData = new JsonObject();
        statusData.addProperty("paused", schedulerPlugin.isPaused());
        
        PluginScheduleEntry upcoming = schedulerPlugin.getUpComingPlugin();
        if (upcoming != null) {
            JsonObject upcomingData = new JsonObject();
            upcomingData.addProperty("name", upcoming.getName());
            upcomingData.addProperty("enabled", upcoming.isEnabled());
            upcomingData.addProperty("lastRunStartTime", upcoming.getLastRunStartTime().toString());
            statusData.add("upcoming", upcomingData);
        }
        
        statusData.addProperty("totalSchedules", schedulerPlugin.getScheduledPlugins().size());
        statusData.addProperty("timestamp", Instant.now().toString());
        
        return new ApiResponse(true, "Scheduler status retrieved", 0, statusData);
    }

    private ApiResponse handleGetSchedules() {
        List<PluginScheduleEntry> schedules = schedulerPlugin.getScheduledPlugins();
        JsonArray schedulesArray = new JsonArray();
        
        for (PluginScheduleEntry entry : schedules) {
            JsonObject scheduleObj = new JsonObject();
            scheduleObj.addProperty("name", entry.getName());
            scheduleObj.addProperty("enabled", entry.isEnabled());
            scheduleObj.addProperty("lastRunStartTime", entry.getLastRunStartTime().toString());
            scheduleObj.addProperty("lastRunDuration", entry.getLastRunDuration().toMillis());
            schedulesArray.add(scheduleObj);
        }
        
        JsonObject responseData = new JsonObject();
        responseData.add("schedules", schedulesArray);
        responseData.addProperty("count", schedules.size());
        
        return new ApiResponse(true, "Schedules retrieved successfully", schedules.size(), responseData);
    }

    private ApiResponse handleAddSchedule(JsonObject data) {
        try {
            if (!data.has("schedule")) {
                return new ApiResponse(false, "Missing 'schedule' data", 0);
            }
            
            String scheduleJson = data.get("schedule").toString();
            List<PluginScheduleEntry> newEntries = PluginScheduleEntry.fromJson(scheduleJson, SchedulerPlugin.VERSION);
            
            if (newEntries.isEmpty()) {
                return new ApiResponse(false, "No valid schedule entries found", 0);
            }
            
            for (PluginScheduleEntry entry : newEntries) {
                schedulerPlugin.resolvePluginReferences(entry);
            }
            
            schedulerPlugin.updateScheduledPluginFromJson(newEntries);
            schedulerPlugin.saveScheduledPlugins();
            
            return new ApiResponse(true, "Schedule(s) added successfully", newEntries.size());
            
        } catch (Exception e) {
            log.error("Error adding schedule", e);
            return new ApiResponse(false, "Failed to add schedule: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleRemoveSchedule(JsonObject data) {
        try {
            if (!data.has("name")) {
                return new ApiResponse(false, "Missing 'name' parameter", 0);
            }
            
            String scheduleName = data.get("name").getAsString();
            List<PluginScheduleEntry> schedules = schedulerPlugin.getScheduledPlugins();
            
            boolean removed = schedules.removeIf(entry -> entry.getName().equals(scheduleName));
            
            if (removed) {
                schedulerPlugin.saveScheduledPlugins();
                return new ApiResponse(true, "Schedule removed successfully", 0);
            } else {
                return new ApiResponse(false, "Schedule not found: " + scheduleName, 0);
            }
            
        } catch (Exception e) {
            log.error("Error removing schedule", e);
            return new ApiResponse(false, "Failed to remove schedule: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleLoadSchedules(JsonObject data) {
        try {
            String filePath = data.has("file") ? data.get("file").getAsString() : null;
            
            if (filePath != null) {
                File file = new File(filePath);
                if (!file.exists()) {
                    return new ApiResponse(false, "File not found: " + filePath, 0);
                }
                
                String content = Files.readString(file.toPath());
                JsonArray jsonArray = new JsonParser().parse(content).getAsJsonArray();
                String json = gson.toJson(jsonArray);
                
                List<PluginScheduleEntry> loadedEntries = PluginScheduleEntry.fromJson(json, SchedulerPlugin.VERSION);
                
                for (PluginScheduleEntry entry : loadedEntries) {
                    schedulerPlugin.resolvePluginReferences(entry);
                }
                
                schedulerPlugin.updateScheduledPluginFromJson(loadedEntries);
                schedulerPlugin.saveScheduledPlugins();
                
                return new ApiResponse(true, "Schedules loaded successfully from " + filePath, loadedEntries.size());
            } else {
                return new ApiResponse(true, "Config-based schedule loading not supported via API", 0);
            }
            
        } catch (Exception e) {
            log.error("Error loading schedules", e);
            return new ApiResponse(false, "Failed to load schedules: " + e.getMessage(), 0);
        }
    }

    private ApiResponse handleSaveSchedules(JsonObject data) {
        try {
            String filePath = data.has("file") ? data.get("file").getAsString() : null;
            
            if (filePath != null) {
                File file = new File(filePath);
                if (schedulerPlugin.saveScheduledPluginsToFile(file)) {
                    return new ApiResponse(true, "Schedules saved successfully to " + filePath, 0);
                } else {
                    return new ApiResponse(false, "Failed to save schedules to file", 0);
                }
            } else {
                schedulerPlugin.saveScheduledPlugins();
                return new ApiResponse(true, "Schedules saved to configuration", 0);
            }
            
        } catch (Exception e) {
            log.error("Error saving schedules", e);
            return new ApiResponse(false, "Failed to save schedules: " + e.getMessage(), 0);
        }
    }
}
