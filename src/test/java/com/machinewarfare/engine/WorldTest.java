package com.machinewarfare.engine;

import com.machinewarfare.model.DroneMech;
import com.machinewarfare.model.Machine;
import com.machinewarfare.model.TankMech;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldTest {

    private World world;

    @BeforeEach
    void setUp() {
        // Initialize a standard 10x10 simulation arena before each test run
        world = new World(10, 10);
    }

    @Test
    @DisplayName("Spawning a machine inside bounds should register it successfully")
    void testValidSpawn() {
        Machine tank = new TankMech("TK-01", new Position(2, 3));
        boolean spawnResult = world.spawnMachine(tank);

        assertTrue(spawnResult, "Machine should spawn successfully inside valid arena bounds.");

        // O(1) Lookup validation
        Machine retrieved = world.getMachineAt(new Position(2, 3));
        assertNotNull(retrieved);
        assertEquals("TK-01", retrieved.getId());
    }

    @Test
    @DisplayName("Spawning a machine out of bounds must fail")
    void testOutOfBoundsSpawn() {
        Machine drone1 = new DroneMech("DR-ERR-1", new Position(-1, 5));
        Machine drone2 = new DroneMech("DR-ERR-2", new Position(10, 5)); // 10 is out of bounds on a 10x10 (0-9 index)

        assertFalse(world.spawnMachine(drone1), "Should reject negative coordinate boundaries.");
        assertFalse(world.spawnMachine(drone2), "Should reject coordinate indexes matching or exceeding maximum size.");
    }

    @Test
    @DisplayName("Prevent two machines from occupying the exact same coordinate match")
    void testCoordinateCollision() {
        Machine tank = new TankMech("ALPHA", new Position(5, 5));
        Machine drone = new DroneMech("BETA", new Position(5, 5));

        assertTrue(world.spawnMachine(tank), "First entity should claim position successfully.");
        assertFalse(world.spawnMachine(drone), "Second entity must be blocked due to structural coordinate conflict.");
    }

    @Test
    @DisplayName("Retrieving all machines should yield a separate defensive copy list")
    void testDefensiveCopying() {
        Machine tank = new TankMech("TK-55", new Position(0, 0));
        world.spawnMachine(tank);

        var list1 = world.getAllMachines();
        assertEquals(1, list1.size());

        // Attempting to clear or alter the leaked list outside the controller
        try {
            list1.clear();
        } catch (UnsupportedOperationException e) {
            // Handled if immutable, but since we created a new ArrayList, it won't throw an error,
            // but it shouldn't modify the internal state tracker of the World class.
        }

        var list2 = world.getAllMachines();
        assertEquals(1, list2.size(), "The internal tracking array must remain fully protected against direct leaking leaks.");
    }
}