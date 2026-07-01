package com.machinewarfare.engine;

import com.machinewarfare.model.Machine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
    private final int width;
    private final int height;

    // Spatial index optimization: O(1) coordinate queries
    private final Map<Position, Machine> grid;
    private final List<Machine> machines;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new HashMap<>();
        this.machines = new ArrayList<>();
    }

    /**
     * Spawns a machine into the simulation boundary safely.
     */
    public boolean spawnMachine(Machine machine) {
        Position pos = machine.getPosition();

        // Out of bounds validation
        if (pos.getX() < 0 || pos.getX() >= width || pos.getY() < 0 || pos.getY() >= height) {
            return false;
        }

        // Occupancy collision check
        if (grid.containsKey(pos)) {
            return false;
        }

        grid.put(pos, machine);
        machines.add(machine);
        return true;
    }

    /**
     * Queries coordinates to find targets
     */
    public Machine getMachineAt(Position position) {
        return grid.get(position);
    }

    public List<Machine> getAllMachines() {
        return new ArrayList<>(this.machines); // Defensive copying to avoid modification leaks
    }
}