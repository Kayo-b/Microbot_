package net.runelite.client.plugins.microbot.socketautomation.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pluginscheduler.SchedulerPlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.SchedulerState;
import net.runelite.client.plugins.microbot.pluginscheduler.model.PluginScheduleEntry;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class SchedulerPluginController implements PluginController {
    private static final String SCHEDULES_DIR = System.getProperty("user.home") + "/.microbot/schedules";
    private static final String VERSION = "0.1.0";
    
    private final SchedulerPlugin schedulerPlugin;
    private final Gson gson = new Gson();
    
    public SchedulerPluginController(SchedulerPlugin schedulerPlugin) {
        this.schedulerPlugin = schedulerPlugin;
        createSchedulesDirectory();
    }
    
    @Override
    public ApiResponse processCommand(String action, JsonObject command) {
        switch (action) {
            case "load_schedule_file":
                return loadScheduleFromFile(command);
            case "load_schedule_json":
                return loadScheduleFromJson(command);
            case "start_scheduler":
                return startScheduler();
            case "stop_scheduler":
                return stopScheduler();
            case "pause_scheduler":
                return pauseScheduler();
            case "resume_scheduler":
                return resumeScheduler();
            case "get_status":
                return getStatus();
            case "list_schedules":
                return listAvailableScheduleFiles();
            case "clear_schedules":
                return clearAllSchedules();
            case "add_schedule_entry":
                return addScheduleEntry(command);
            default:
                return new ApiResponse(false, "Unknown scheduler action: " + action, 0);
        }
    }
    
    @Override
    public String getPluginName() {
        return "Scheduler";
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{
            "load_schedule_file", "load_schedule_json", "start_scheduler", 
            "stop_scheduler", "pause_scheduler", "resume_scheduler", 
            "get_status", "list_schedules", "clear_schedules", "add_schedule_entry"
        };
    }
    
    private ApiResponse loadScheduleFromFile(JsonObject command) {
        try {
            if (!command.has("filename")) {
                return new ApiResponse(false, "Missing 'filename' field", 0);
            }
            
            String filename = command.get("filename").getAsString();
            Path filePath = Paths.get(SCHEDULES_DIR, filename);
            
            if (!Files.exists(filePath)) {
                return new ApiResponse(false, "Schedule file not found: " + filename, 0);
            }
            
            String content = Files.readString(filePath);
            return loadScheduleFromJson(content);
            
        } catch (Exception e) {
            log.error("Error loading schedule from file: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to load schedule file: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse loadScheduleFromJson(JsonObject command) {
        try {
            if (!command.has("schedule")) {
                return new ApiResponse(false, "Missing 'schedule' field", 0);
            }
            
            String scheduleJson = command.get("schedule").getAsString();
            return loadScheduleFromJson(scheduleJson);
            
        } catch (Exception e) {
            log.error("Error loading schedule from JSON: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to load schedule from JSON: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse loadScheduleFromJson(String json) {
        try {
            List<PluginScheduleEntry> loadedPlugins = PluginScheduleEntry.fromJson(json, VERSION);
            
            if (loadedPlugins == null) {
                log.error("Failed to parse JSON from file");
                return new ApiResponse(false, "Failed to parse JSON from file", 0);
            }
            
            for (PluginScheduleEntry entry : loadedPlugins) {
                schedulerPlugin.resolvePluginReferences(entry);
                schedulerPlugin.registerStopCompletionCallback(entry);
            }
            
            schedulerPlugin.updateScheduledPluginFromJson(loadedPlugins);
            SwingUtilities.invokeLater(() -> schedulerPlugin.updatePanels());
            
            Microbot.log("Loaded " + loadedPlugins.size() + " schedule entries from JSON");
            log.info("Successfully loaded {} schedule entries from JSON", loadedPlugins.size());
            
            return new ApiResponse(true, "Schedule loaded successfully", loadedPlugins.size());
        } catch (Exception e) {
            log.error("Failed to load schedule from JSON: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to load schedule: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse addScheduleEntry(JsonObject command) {
        try {
            if (!command.has("entry")) {
                return new ApiResponse(false, "Missing 'entry' field", 0);
            }
            
            String entryJson = command.get("entry").getAsJsonObject().toString();
            PluginScheduleEntry entry = gson.fromJson(entryJson, PluginScheduleEntry.class);
            
            if (entry == null) {
                return new ApiResponse(false, "Invalid schedule entry JSON", 0);
            }
            
            schedulerPlugin.getScheduledPlugins().add(entry);
            schedulerPlugin.saveScheduledPlugins();
            
            Microbot.log("Added schedule entry: " + entry.getName());
            log.info("Successfully added schedule entry: {}", entry.getName());
            
            return new ApiResponse(true, "Schedule entry added successfully", 1);
        } catch (Exception e) {
            log.error("Failed to add schedule entry from JSON: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to add schedule entry: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse startScheduler() {
        try {
            schedulerPlugin.startScheduler();
            Microbot.log("Scheduler started via automation API");
            log.info("Scheduler started via automation API");
            return new ApiResponse(true, "Scheduler started", 0);
        } catch (Exception e) {
            log.error("Failed to start scheduler: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to start scheduler: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse stopScheduler() {
        try {
            schedulerPlugin.stopScheduler();
            Microbot.log("Scheduler stopped via automation API");
            log.info("Scheduler stopped via automation API");
            return new ApiResponse(true, "Scheduler stopped", 0);
        } catch (Exception e) {
            log.error("Failed to stop scheduler: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to stop scheduler: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse pauseScheduler() {
        try {
            boolean wasPaused = schedulerPlugin.pauseScheduler();
            String message = wasPaused ? "Scheduler was already paused" : "Scheduler paused";
            Microbot.log(message + " via automation API");
            log.info(message + " via automation API");
            return new ApiResponse(true, message, 0);
        } catch (Exception e) {
            log.error("Failed to pause scheduler: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to pause scheduler: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse resumeScheduler() {
        try {
            schedulerPlugin.resumeScheduler();
            Microbot.log("Scheduler resumed via automation API");
            log.info("Scheduler resumed via automation API");
            return new ApiResponse(true, "Scheduler resumed", 0);
        } catch (Exception e) {
            log.error("Failed to resume scheduler: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to resume scheduler: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse getStatus() {
        try {
            SchedulerStatus status = new SchedulerStatus();
            status.setState(schedulerPlugin.getCurrentState());
            status.setCurrentPlugin(schedulerPlugin.getCurrentPlugin());
            status.setUpcomingPlugin(schedulerPlugin.getUpComingPlugin());
            status.setScheduledPluginCount(schedulerPlugin.getScheduledPlugins().size());
            status.setPaused(schedulerPlugin.isPaused());
            
            return new ApiResponse(true, "Status retrieved", 0, status);
        } catch (Exception e) {
            log.error("Failed to get scheduler status: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to get status: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse clearAllSchedules() {
        try {
            int count = schedulerPlugin.getScheduledPlugins().size();
            schedulerPlugin.getScheduledPlugins().clear();
            schedulerPlugin.saveScheduledPlugins();
            
            Microbot.log("Cleared " + count + " schedule entries via automation API");
            log.info("Cleared {} schedule entries via automation API", count);
            
            return new ApiResponse(true, "All schedules cleared", count);
        } catch (Exception e) {
            log.error("Failed to clear schedules: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to clear schedules: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse listAvailableScheduleFiles() {
        try {
            Path schedulesPath = Paths.get(SCHEDULES_DIR);
            if (!Files.exists(schedulesPath)) {
                return new ApiResponse(true, "No schedules directory found", 0, new String[0]);
            }
            
            String[] scheduleFiles = Files.list(schedulesPath)
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
            
            return new ApiResponse(true, "Schedule files listed", scheduleFiles.length, scheduleFiles);
            
        } catch (Exception e) {
            log.error("Error listing schedule files: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to list schedule files: " + e.getMessage(), 0);
        }
    }
    
    private void createSchedulesDirectory() {
        try {
            Path schedulesPath = Paths.get(SCHEDULES_DIR);
            if (!Files.exists(schedulesPath)) {
                Files.createDirectories(schedulesPath);
                log.info("Created schedules directory: {}", SCHEDULES_DIR);
            }
        } catch (Exception e) {
            log.error("Failed to create schedules directory: {}", e.getMessage(), e);
        }
    }
    
    public static class SchedulerStatus {
        private SchedulerState state;
        private PluginScheduleEntry currentPlugin;
        private PluginScheduleEntry upcomingPlugin;
        private int scheduledPluginCount;
        private boolean paused;
        
        public SchedulerState getState() { return state; }
        public void setState(SchedulerState state) { this.state = state; }
        
        public PluginScheduleEntry getCurrentPlugin() { return currentPlugin; }
        public void setCurrentPlugin(PluginScheduleEntry currentPlugin) { this.currentPlugin = currentPlugin; }
        
        public PluginScheduleEntry getUpcomingPlugin() { return upcomingPlugin; }
        public void setUpcomingPlugin(PluginScheduleEntry upcomingPlugin) { this.upcomingPlugin = upcomingPlugin; }
        
        public int getScheduledPluginCount() { return scheduledPluginCount; }
        public void setScheduledPluginCount(int scheduledPluginCount) { this.scheduledPluginCount = scheduledPluginCount; }
        
        public boolean isPaused() { return paused; }
        public void setPaused(boolean paused) { this.paused = paused; }
    }
}
