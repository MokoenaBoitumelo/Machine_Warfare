package com.machinewarfare.model;

import com.machinewarfare.engine.Position;
import lombok.NoArgsConstructor;

@NoArgsConstructor // Added for Jackson deserialization
public class DroneMech extends Machine {
    public DroneMech(String id, Position position) {
        super(id, 60, position, false);
    }

    @Override
    protected void onDestroyed() {
        System.out.println("⚡ DroneMech [" + getId() + "] system failure! Falling out of the sky.");
    }
}