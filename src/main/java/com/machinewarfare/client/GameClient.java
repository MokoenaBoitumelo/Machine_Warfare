package com.machinewarfare.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinewarfare.engine.Position;
import com.machinewarfare.model.TankMech;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        Scanner scanner = new Scanner(System.in);

        System.out.println("🤖 MACHINE_WARFARE CLI CONTROLLER CLIENT 🤖");
        System.out.print("Enter your Mech Callsign ID (e.g., VANGUARD-1): ");
        String mechId = scanner.nextLine().trim();

        System.out.println("Connecting to battle server at " + SERVER_HOST + ":" + SERVER_PORT + "...");

        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Read initial server welcome handshake string
            System.out.println("\n📡 Server Handshake: " + serverReader.readLine());

            // 1. Initial Deployment: Spawn a fresh TankMech at starting coordinates (2, 2)
            TankMech initialSpawn = new TankMech(mechId, new Position(2, 2));
            String spawnJson = objectMapper.writeValueAsString(initialSpawn);

            // Transmit structural frames over socket pipelines
            serverWriter.println(spawnJson);
            System.out.println("📥 Response: " + serverReader.readLine());

            // 2. Interactive Input Processing Loop
            System.out.println("\n🎮 CONTROLS ACTIVATED!");
            System.out.println("Type 'MOVE X Y' to shift positions (e.g., MOVE 3 4)");
            System.out.println("Type 'QUIT' to extract from combat layout boundaries.\n");

            while (true) {
                System.out.print("[" + mechId + "] Matrix Command > ");
                String command = scanner.nextLine().trim();

                if (command.equalsIgnoreCase("QUIT")) {
                    serverWriter.println("QUIT");
                    System.out.println("📥 Response: " + serverReader.readLine());
                    break;
                }

                if (command.toUpperCase().startsWith("MOVE ")) {
                    try {
                        String[] segments = command.split(" ");
                        int targetX = Integer.parseInt(segments[1]);
                        int targetY = Integer.parseInt(segments[2]);

                        // Packages vector movements into the exact JSON format expected by Jackson
                        TankMech movementUpdate = new TankMech(mechId, new Position(targetX, targetY));
                        String movementJson = objectMapper.writeValueAsString(movementUpdate);

                        serverWriter.println(movementJson);
                        System.out.println("📥 Response: " + serverReader.readLine());
                    } catch (Exception e) {
                        System.out.println("❌ Formatting parsing failure. Use syntax: MOVE X Y");
                    }
                } else {
                    System.out.println("⚠️ Unknown system command instruction frame.");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Network link dropped unexpectedly: " + e.getMessage());
        }

        System.out.println("🔌 Session safely closed.");
    }
}