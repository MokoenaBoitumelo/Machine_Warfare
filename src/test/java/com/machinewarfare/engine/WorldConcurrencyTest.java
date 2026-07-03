package com.machinewarfare.engine;

import com.machinewarfare.model.Machine;
import com.machinewarfare.model.TankMech;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorldConcurrencyTest {

    @Test
    @DisplayName("Simultaneous threads attacking the same coordinate must result in exactly one successful spawn")
    void testConcurrentSpawnsToSameCoordinate() throws InterruptedException {
        // Arrange: A standard grid and 10 competitive threads
        World world = new World(10, 10);
        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startingGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        // Submit 10 distinct machine deployment tasks simultaneously
        for (int i = 0; i < threadCount; i++) {
            final int idSuffix = i;
            futures.add(executor.submit(() -> {
                // All threads hold execution here until the gate drops
                startingGate.await();

                // Construct a machine targeting the exact same coordinate matrix point
                Machine competitiveMech = new TankMech("MECH-" + idSuffix, new Position(5, 5));
                return world.spawnMachine(competitiveMech);
            }));
        }

        // Act: Drop the gate! All threads execute their spawn calls at the exact same moment
        startingGate.countDown();

        // Gather metrics
        int successCount = 0;
        int failureCount = 0;
        for (Future<Boolean> result : futures) {
            try {
                if (result.get()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                System.err.println("Thread execution failure: " + e.getMessage());
            }
        }

        // Clean up thread pool threads cleanly
        executor.shutdown();

        // Assert: Out of 10 rapid fire attempts, exactly 1 must succeed. 9 must cleanly fail.
        System.out.println("📊 Concurrency Results -> Successful Spawns: " + successCount + " | Blocked Collisions: " + failureCount);

        assertEquals(1, successCount, "Thread safety compromised! More than one machine claimed the same coordinate.");
        assertEquals(9, failureCount, "Coordinate collision rules failed to reject concurrent threads.");
        assertEquals(1, world.getAllMachines().size(), "The total tracking list state must reflect exactly one machine.");
    }
}