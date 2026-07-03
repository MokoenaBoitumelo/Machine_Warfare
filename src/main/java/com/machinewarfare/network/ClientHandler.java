package com.machinewarfare.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinewarfare.engine.World;
import com.machinewarfare.model.Machine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final World world;
    // Jackson structural parser mapping engine instance
    private final ObjectMapper objectMapper;

    public ClientHandler(Socket socket, World world) {
        this.socket = socket;
        this.world = world;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            writer.println("CONNECTION_SUCCESSFUL: Awaiting JSON deployment frame...");

            String rawLine;
            while ((rawLine = reader.readLine()) != null) {
                if (rawLine.equalsIgnoreCase("QUIT")) {
                    writer.println("DISCONNECTING");
                    break;
                }

                try {
                    // Statically typed parsing: Jackson maps the JSON string directly to the correct sub-mech type!
                    Machine deployedMachine = objectMapper.readValue(rawLine, Machine.class);

                    // Attempt to commit the parsed target to the simulation boundaries
                    boolean spawnSuccess = world.spawnMachine(deployedMachine);

                    if (spawnSuccess) {
                        writer.println("DEPLOY_SUCCESS: Registered ID [" + deployedMachine.getId() + "]");
                        System.out.println("🤖 Simulation Update: Spawned " + deployedMachine.getClass().getSimpleName() + " at position: " + deployedMachine.getPosition());
                    } else {
                        writer.println("DEPLOY_FAILED: Position collision or out-of-bounds error.");
                    }
                } catch (Exception parseException) {
                    writer.println("ERROR: Invalid payload structural frame. " + parseException.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("⚠️ Thread transmission error on link: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to cleanly sever socket link: " + e.getMessage());
            }
        }
    }
}