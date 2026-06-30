package com.machinewarfare.model;

import com.machinewarfare.engine.Position;

public class TankMech extends Machine {

    // Java Rule: Subclass constructors must explicitly call the parent constructor using super()
    public TankMech(String id, Position position) {
        super(id, 200, position, false); // Tanks start with high health (200 HP)
    }

    @Override
    protected void onDestroyed() {
        System.out.println("💥 TankMech [" + getId() + "] heavy armor ruptured! Critical detonation.");
    }
}