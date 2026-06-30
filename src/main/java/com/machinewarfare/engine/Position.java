package com.machinewarfare.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private int x;
    private int y;

    /**
     * Updates coordinates based on direction.
     * North: dy = -1, South: dy = 1, East: dx = 1, West: dx = -1
     */
    public void moveNorth() { this.y -= 1; }
    public void moveSouth() { this.y += 1; }
    public void moveEast()  { this.x += 1; }
    public void moveWest()  { this.x -= 1; }
}