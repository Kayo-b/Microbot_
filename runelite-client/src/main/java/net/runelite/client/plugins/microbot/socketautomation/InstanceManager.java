package net.runelite.client.plugins.microbot.socketautomation;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginControllerRegistry;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InstanceManager {
    private static final InstanceManager INSTANCE = new InstanceManager();
    private final Map<String, InstanceContext> instances = new ConcurrentHashMap<>();
    private String currentInstanceId;
    
    private InstanceManager() {
        this.currentInstanceId = generateInstanceId();
        log.info("Instance Manager initialized with ID: {}", currentInstanceId);
    }
    
    public static InstanceManager getInstance() {
        return INSTANCE;
    }
    
    public String getCurrentInstanceId() {
        return currentInstanceId;
    }
    
    public InstanceContext getCurrentInstance() {
        return instances.get(currentInstanceId);
    }
    
    public InstanceContext getOrCreateInstance(String instanceId) {
        return instances.computeIfAbsent(instanceId, id -> {
            log.info("Creating new instance context: {}", id);
            return new InstanceContext(id);
        });
    }
    
    public void registerCurrentInstance(PluginControllerRegistry controllerRegistry) {
        InstanceContext context = new InstanceContext(currentInstanceId);
        context.setControllerRegistry(controllerRegistry);
        context.setActive(true);
        instances.put(currentInstanceId, context);
        log.info("Registered current instance: {}", currentInstanceId);
    }
    
    public InstanceContext getInstance(String instanceId) {
        return instances.get(instanceId);
    }
    
    public Map<String, InstanceContext> getAllInstances() {
        return new ConcurrentHashMap<>(instances);
    }
    
    public void removeInstance(String instanceId) {
        InstanceContext removed = instances.remove(instanceId);
        if (removed != null) {
            removed.setActive(false);
            log.info("Removed instance: {}", instanceId);
        }
    }
    
    private String generateInstanceId() {
        String baseId = "rl-" + UUID.randomUUID().toString().substring(0, 8);
        String processId = System.getProperty("user.name", "unknown");
        return baseId + "-" + processId;
    }
    
    public static class InstanceContext {
        private final String instanceId;
        private PluginControllerRegistry controllerRegistry;
        private boolean active;
        private long lastActivity;
        
        public InstanceContext(String instanceId) {
            this.instanceId = instanceId;
            this.lastActivity = System.currentTimeMillis();
        }
        
        public String getInstanceId() { 
            return instanceId; 
        }
        
        public PluginControllerRegistry getControllerRegistry() { 
            return controllerRegistry; 
        }
        
        public void setControllerRegistry(PluginControllerRegistry controllerRegistry) { 
            this.controllerRegistry = controllerRegistry; 
        }
        
        public boolean isActive() { 
            return active; 
        }
        
        public void setActive(boolean active) { 
            this.active = active;
            if (active) {
                this.lastActivity = System.currentTimeMillis();
            }
        }
        
        public long getLastActivity() { 
            return lastActivity; 
        }
        
        public void updateActivity() {
            this.lastActivity = System.currentTimeMillis();
        }
    }
}
