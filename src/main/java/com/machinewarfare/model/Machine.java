package com.machinewarfare.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.machinewarfare.engine.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Inject a custom field "type" into the JSON payload for dynamic lookups
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
// Map the "type" property values directly to the correct concrete subclasses
@JsonSubTypes({
        @JsonSubTypes.Type(value = TankMech.class, name = "tank"),
        @JsonSubTypes.Type(value = DroneMech.class, name = "drone")
})
public abstract class Machine {
    private String id;
    private int health;
    private Position position;
    private boolean isDestroyed;

    public void takeDamage(int damage) {
        if (isDestroyed) return;
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            this.isDestroyed = true;
            onDestroyed();
        }
    }

    protected abstract void onDestroyed();
}