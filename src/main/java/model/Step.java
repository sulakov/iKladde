package model;

import java.io.Serializable;

/**
 * Repräsentiert einen einzelnen Schritt in der Zubereitung eines Rezepts.
 */
public class Step {

    private String description;

    public Step() {
    }

    public Step(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}