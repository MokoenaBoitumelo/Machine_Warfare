package com.machinewarfare.model;

import com.machinewarfare.engine.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MachineTest {

    @Test
    @DisplayName("TankMech should initialize with high health and take damage correctly")
    void testTankMechDamage() {
        // Arrange: Instantiate a TankMech using a generic Machine reference
        Machine tank = new TankMech("TK-99", new Position(0, 0));

        // Act & Assert Initial State
        assertEquals(200, tank.getHealth(), "TankMech should start with 200 HP.");
        assertFalse(tank.isDestroyed(), "TankMech should not be destroyed initially.");

        // Act: Apply damage less than total health
        tank.takeDamage(50);
        assertEquals(150, tank.getHealth(), "TankMech health should reduce to 150.");
        assertFalse(tank.isDestroyed());
    }

    @Test
    @DisplayName("DroneMech should be destroyed when damage exceeds its health")
    void testDroneMechDestruction() {
        // Arrange: Instantiate a DroneMech
        Machine drone = new DroneMech("DR-01", new Position(5, 5));

        // Act: Apply lethal damage
        drone.takeDamage(100);

        // Assert
        assertEquals(0, drone.getHealth(), "Health should clamp to 0 upon destruction.");
        assertTrue(drone.isDestroyed(), "DroneMech should be flagged as destroyed.");
    }

    @Test
    @DisplayName("Destroyed machines should not accept further damage or change state")
    void testDamageAfterDestruction() {
        Machine drone = new DroneMech("DR-02", new Position(1, 1));

        // Overkill damage
        drone.takeDamage(100);
        assertTrue(drone.isDestroyed());

        // Subsequent damage should be ignored by the encapsulation shield
        drone.takeDamage(50);
        assertEquals(0, drone.getHealth(), "Health must remain clamped to 0.");
    }
}