package com.machinewarfare;

import com.machinewarfare.engine.World;
import com.machinewarfare.network.NetworkServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize a 20x20 thread-safe simulation grid
        World world = new World(20, 20);

        // 2. Initialize a dedicated single-threaded scheduler thread pool
        ScheduledExecutorService gameLoopScheduler = Executors.newSingleThreadScheduledExecutor();

        // Schedule our gameTick method to execute every 500 milliseconds (2 ticks per second)
        // The loop waits 1 second before starting, then continuously triggers.
        gameLoopScheduler.scheduleAtFixedRate(
                world::gameTick,
                1000,
                500,
                TimeUnit.MILLISECONDS
        );

        System.out.println("⏱️ Simulation Heartbeat Loop activated at a rate of 2Hz (500ms intervals).");

        // 3. Bind the asynchronous network gateway engine to port 8080
        NetworkServer server = new NetworkServer(8080, world);
        server.start();
    }
}