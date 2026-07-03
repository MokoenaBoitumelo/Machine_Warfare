package com.machinewarfare.engine;

import com.machinewarfare.model.Machine;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class World {
    private final int width;
    private final int height;

    // Upgrade 1: Use ConcurrentHashMap for lock-free multi-threaded reading safety
    private final Map<Position, Machine> grid;
    private final List<Machine> machines;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new ConcurrentHashMap<>();
        this.machines = new ArrayList<>(); // Guarded by explicit synchronization below
    }

    /**
     * Spawns a machine safely even when bombarded by multiple threads simultaneously.
     */
    public boolean spawnMachine(Machine machine) {
        Position pos = machine.getPosition();

        if (pos.getX() < 0 || pos.getX() >= width || pos.getY() < 0 || pos.getY() >= height) {
            return false;
        }

        // Upgrade 2: Critical Section. Only one thread can execute this block at a time per world instance.
        synchronized (this) {
            // Check-then-act atomic safety
            if (grid.containsKey(pos)) {
                return false;
            }

            grid.put(pos, machine);
            machines.add(machine);
            return true;
        }
    }

    public Machine getMachineAt(Position position) {
        return grid.get(position); // ConcurrentHashMap allows safe, concurrent reads without locks
    }

    public List<Machine> getAllMachines() {
        synchronized (this) {
            return new ArrayList<>(this.machines); // Safe defensive copy under lock protection
        }
    }

    /**
     * Atomically moves a machine from its current position to a new target position.
     * Returns true if the move was successful, false if the target space was blocked or out of bounds.
     */
    public boolean moveMachine(String machineId, Position newPosition) {
        // 1. Boundary check before acquiring locks
        if (newPosition.getX() < 0 || newPosition.getX() >= width || newPosition.getY() < 0 || newPosition.getY() >= height) {
            return false;
        }

        // 2. Lock the entire world state during the coordinate key translation
        synchronized (this) {
            // Find the machine matching our target ID
            Machine targetMachine = null;
            for (Machine m : machines) {
                if (m.getId().equals(machineId)) {
                    targetMachine = m;
                    break;
                }
            }

            // If the machine doesn't exist or the destination coordinate is occupied, reject the move
            if (targetMachine == null || grid.containsKey(newPosition)) {
                return false;
            }

            // 3. Perform the atomic swap across our indexing structures
            Position oldPosition = targetMachine.getPosition();
            grid.remove(oldPosition);                  // Wipe out old coordinate reference
            targetMachine.setPosition(newPosition);    // Mutate internal position vector
            grid.put(newPosition, targetMachine);      // Register new coordinate reference

            return true;
        }
    }
}