package com.machinewarfare.network;

import com.machinewarfare.engine.World;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final World world;

    public ClientHandler(Socket socket, World world) {
        this.socket = socket;
        this.world = world;
    }

    @Override
    public void run() {
        // Automatically close resources when done (Java's Try-With-Resources)
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            writer.println("CONNECTION_SUCCESSFUL: Awaiting incoming telemetry payload...");

            String rawLine;
            // Continuously listen to this specific client's network buffer stream line-by-line
            while ((rawLine = reader.readLine()) != null) {
                if (rawLine.equalsIgnoreCase("QUIT")) {
                    writer.println("DISCONNECTING");
                    break;
                }

                System.out.println("📥 Data received from client thread: " + rawLine);
                // Simple echoing back placeholder for now before we integrate Jackson mapping parsing
                writer.println("ACK: Received: " + rawLine);
            }

        } catch (IOException e) {
            System.err.println("⚠️ Thread transmission error on link: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("🔌 Machine connection cleaned up and severed.");
            } catch (IOException e) {
                System.err.println("Failed to cleanly sever socket link: " + e.getMessage());
            }
        }
    }
}