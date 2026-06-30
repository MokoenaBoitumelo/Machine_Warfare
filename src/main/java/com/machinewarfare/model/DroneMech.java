package com.machinewarfare.model;

import com.machinewarfare.engine.Position;

public class DroneMech extends Machine {

    public DroneMech(String id, Position position) {
        super(id, 60, position, false); // Drones are agile but fragile (60 HP)
    }

    @Override
    protected void onDestroyed() {
        System.out.println("⚡ DroneMech [" + getId() + "] system failure! Falling out of the sky.");
    }
}