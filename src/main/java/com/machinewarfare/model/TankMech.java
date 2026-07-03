package com.machinewarfare.model;

import com.machinewarfare.engine.Position;
import lombok.NoArgsConstructor;

@NoArgsConstructor // Added so Jackson can instantiate this subclass from JSON
public class TankMech extends Machine {
    public TankMech(String id, Position position) {
        super(id, 200, position, false);
    }

    @Override
    protected void onDestroyed() {
        System.out.println("💥 TankMech [" + getId() + "] heavy armor ruptured! Critical detonation.");
    }
}