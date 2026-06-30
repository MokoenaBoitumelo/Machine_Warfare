package com.machinewarfare.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest {

    private Position position;

    @BeforeEach
    void setUp() {
        // Runs before every single @Test method to guarantee a clean slate
        position = new Position(0, 0);
    }

    @Test
    @DisplayName("Moving North should decrease Y coordinate by 1")
    void testMoveNorth() {
        position.moveNorth();
        assertEquals(-1, position.getY(), "North movement failed: Y should be -1");
        assertEquals(0, position.getX(), "X should remain unchanged");
    }

    @Test
    @DisplayName("Moving South should increase Y coordinate by 1")
    void testMoveSouth() {
        position.moveSouth();
        assertEquals(1, position.getY(), "South movement failed: Y should be 1");
    }

    @Test
    @DisplayName("Lombok @Data should correctly evaluate structural equality")
    void testLombokEquality() {
        Position pos1 = new Position(5, -2);
        Position pos2 = new Position(5, -2);

        // In Java, standard '==' checks memory references.
        // Lombok overrides .equals() so we can check coordinate equality perfectly.
        assertEquals(pos1, pos2, "Lombok failed to recognize matching coordinates as equal");
    }
}