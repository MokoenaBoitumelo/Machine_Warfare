package com.machinewarfare.model;

import com.machinewarfare.engine.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Machine {
    private String id;
    private int health;
    private Position position; // Composition: Machine "has a" Position
    private boolean isDestroyed;

    /**
     * Business Logic Layer: Enforces encapsulation.
     * Prevents external classes from directly altering the health variable.
     */
    public void takeDamage(int damage) {
        if (isDestroyed) return;

        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            this.isDestroyed = true;
            onDestroyed();
        }
    }

    /**
     * Polymorphic Hook: Subclasses must define their unique explosion/death behaviors.
     */
    protected abstract void onDestroyed();
}