package net.runelite.client.plugins.microbot.socketautomation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.socketautomation.api.ApiResponse;
import net.runelite.client.plugins.microbot.socketautomation.controllers.PluginControllerRegistry;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SocketAutomationManager {
    private final SocketAutomationConfig config;
    private final Gson gson = new Gson();
    private final PluginControllerRegistry controllerRegistry;
    
    private ServerSocket serverSocket;
    private boolean running = false;
    private Thread socketThread;
    private ExecutorService executorService;
    
    private final ConcurrentHashMap<Integer, ServerSocket> additionalSockets = new ConcurrentHashMap<>();
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    
    public SocketAutomationManager(SocketAutomationConfig config) {
        this.config = config;
        this.controllerRegistry = new PluginControllerRegistry();
        this.executorService = Executors.newFixedThreadPool(config.maxConnections());
    }
    
    public void startSocketListener() {
        if (running) {
            log.warn("Socket automation manager is already running on port {}", config.primaryPort());
            Microbot.log("Socket automation manager is already running on port " + config.primaryPort());
            return;
        }
        
        socketThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(config.primaryPort());
                running = true;
                Microbot.log("Socket automation listener started on port " + config.primaryPort());
                log.info("Socket automation listener started on port {}", config.primaryPort());
                
                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        Socket client = serverSocket.accept();
                        
                        if (connectionCount.get() >= config.maxConnections()) {
                            log.warn("Max connections reached, rejecting connection");
                            client.close();
                            continue;
                        }
                        
                        executorService.submit(() -> handleClientConnection(client));
                    } catch (IOException e) {
                        if (running) {
                            log.error("Error accepting client connection: {}", e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                if (running) {
                    log.error("Socket automation server error: {}", e.getMessage());
                    Microbot.log("Socket automation server error: " + e.getMessage());
                }
            } finally {
                log.info("Socket automation listener stopped");
            }
        });
        
        socketThread.setName("SocketAutomationManager");
        socketThread.setDaemon(true);
        socketThread.start();
    }
    
    public ApiResponse startNewSocketListener() {
        try {
            int newPort = findNextAvailablePort();
            
            Thread newSocketThread = new Thread(() -> {
                ServerSocket newServerSocket = null;
                try {
                    newServerSocket = new ServerSocket(newPort);
                    additionalSockets.put(newPort, newServerSocket);
                    
                    Microbot.log("New socket automation listener started on port " + newPort);
                    log.info("New socket automation listener started on port {}", newPort);
                    
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Socket client = newServerSocket.accept();
                            
                            if (connectionCount.get() >= config.maxConnections()) {
                                log.warn("Max connections reached, rejecting connection on port {}", newPort);
                                client.close();
                                continue;
                            }
                            
                            executorService.submit(() -> handleClientConnection(client));
                        } catch (IOException e) {
                            if (!Thread.currentThread().isInterrupted()) {
                                log.error("Error accepting client connection on port {}: {}", newPort, e.getMessage());
                            }
                            break;
                        }
                    }
                } catch (IOException e) {
                    log.error("Failed to start socket automation server on port {}: {}", newPort, e.getMessage());
                } finally {
                    if (newServerSocket != null && !newServerSocket.isClosed()) {
                        try {
                            newServerSocket.close();
                        } catch (IOException e) {
                            log.error("Error closing server socket on port {}: {}", newPort, e.getMessage());
                        }
                    }
                    additionalSockets.remove(newPort);
                    log.info("Socket automation listener on port {} stopped", newPort);
                }
            });
            
            newSocketThread.setName("SocketAutomationManager-" + newPort);
            newSocketThread.setDaemon(true);
            newSocketThread.start();
            
            return new ApiResponse(true, "New socket listener started on port " + newPort, newPort);
            
        } catch (Exception e) {
            log.error("Failed to start new socket listener: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to start new socket listener: " + e.getMessage(), 0);
        }
    }
    
    private int findNextAvailablePort() {
        for (int port = config.portRangeStart(); port <= config.portRangeEnd(); port++) {
            try (ServerSocket testSocket = new ServerSocket(port)) {
                return port;
            } catch (IOException e) {
                continue;
            }
        }
        throw new RuntimeException("No available ports found in range " + 
                                   config.portRangeStart() + "-" + config.portRangeEnd());
    }
    
    private void handleClientConnection(Socket client) {
        connectionCount.incrementAndGet();
        
        try {
            if (config.allowLocalOnly() && !client.getInetAddress().isLoopbackAddress()) {
                log.warn("Rejected non-localhost connection from: {}", client.getInetAddress());
                return;
            }
            
            if (config.logConnections()) {
                log.info("Accepted connection from: {}", client.getInetAddress());
            }
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                
                String request = in.readLine();
                if (request != null && !request.trim().isEmpty()) {
                    if (config.logConnections()) {
                        log.debug("Received automation request: {}", request);
                    }
                    
                    ApiResponse response = processAutomationCommand(request);
                    String responseJson = gson.toJson(response);
                    
                    out.println(responseJson);
                    
                    if (config.logConnections()) {
                        log.debug("Sent response: {}", responseJson);
                    }
                } else {
                    ApiResponse errorResponse = new ApiResponse(false, "Empty request received", 0);
                    out.println(gson.toJson(errorResponse));
                }
                
            } catch (Exception e) {
                log.error("Error handling automation request: {}", e.getMessage(), e);
                try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                    ApiResponse errorResponse = new ApiResponse(false, "Internal error: " + e.getMessage(), 0);
                    out.println(gson.toJson(errorResponse));
                } catch (IOException ioException) {
                    log.error("Error sending error response: {}", ioException.getMessage());
                }
            }
            
        } finally {
            connectionCount.decrementAndGet();
            try {
                client.close();
            } catch (IOException e) {
                log.debug("Error closing client connection: {}", e.getMessage());
            }
        }
    }
    
    private ApiResponse processAutomationCommand(String request) {
        try {
            JsonObject command = gson.fromJson(request, JsonObject.class);
            
            if (!command.has("action")) {
                return new ApiResponse(false, "Missing 'action' field in request", 0);
            }
            
            String action = command.get("action").getAsString();
            
            switch (action) {
                case "start_new_socket":
                    return startNewSocketListener();
                case "get_socket_info":
                    return getSocketInfo();
                case "list_supported_plugins":
                    return listSupportedPlugins();
                default:
                    return controllerRegistry.processCommand(action, command);
            }
        } catch (Exception e) {
            log.error("Error processing automation command: {}", e.getMessage(), e);
            return new ApiResponse(false, "Error processing command: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse getSocketInfo() {
        try {
            JsonObject info = new JsonObject();
            info.addProperty("primaryPort", config.primaryPort());
            info.addProperty("isPrimarySocketRunning", isRunning());
            info.addProperty("activeConnections", connectionCount.get());
            info.addProperty("maxConnections", config.maxConnections());
            info.addProperty("additionalSocketCount", additionalSockets.size());
            
            return new ApiResponse(true, "Socket information retrieved", 1, info);
            
        } catch (Exception e) {
            log.error("Error getting socket info: {}", e.getMessage(), e);
            return new ApiResponse(false, "Failed to get socket info: " + e.getMessage(), 0);
        }
    }
    
    private ApiResponse listSupportedPlugins() {
        return new ApiResponse(true, "Supported plugins listed", 
                              controllerRegistry.getSupportedPlugins().size(), 
                              controllerRegistry.getSupportedPlugins());
    }
    
    public void shutdown() {
        running = false;
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("Error closing server socket: {}", e.getMessage());
            }
        }
        
        additionalSockets.values().forEach(socket -> {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Error closing additional socket: {}", e.getMessage());
            }
        });
        additionalSockets.clear();
        
        if (socketThread != null && socketThread.isAlive()) {
            socketThread.interrupt();
            try {
                socketThread.join(5000);
            } catch (InterruptedException e) {
                log.debug("Interrupted while waiting for socket thread to finish");
                Thread.currentThread().interrupt();
            }
        }
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        log.info("Socket automation manager shutdown complete");
    }
    
    public boolean isRunning() {
        return running && serverSocket != null && !serverSocket.isClosed();
    }
    
    public int getPort() {
        return config.primaryPort();
    }
    
    public PluginControllerRegistry getControllerRegistry() {
        return controllerRegistry;
    }
}
