package com.machinewarfare.network;

import com.machinewarfare.engine.World;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer {
    private final int port;
    private final World world;
    private boolean isRunning;
    private ServerSocket serverSocket;

    public NetworkServer(int port, World world) {
        this.port = port;
        this.world = world;
        this.isRunning = false;
    }

    /**
     * Starts the server network engine loop.
     */
    public void start() {
        this.isRunning = true;

        // Using Java Thread to spin up the listener on its own OS thread pipeline
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("🌐 Machine_Warfare Server listening on port " + port);

                while (isRunning) {
                    // This blocks execution until a client client connects (Standard Blocking I/O)
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("🔌 New machine link established from: " + clientSocket.getRemoteSocketAddress());

                    // Instantiate a runnable task and hand it immediately to a new true OS thread
                    ClientHandler handler = new ClientHandler(clientSocket, world);
                    new Thread(handler).start();
                }
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("❌ Network Server exception encountered: " + e.getMessage());
                }
            }
        }).start();
    }

    public void stop() {
        this.isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Closing the socket unblocks the .accept() call
            }
        } catch (IOException e) {
            System.err.println("Error shutting down server socket: " + e.getMessage());
        }
    }
}