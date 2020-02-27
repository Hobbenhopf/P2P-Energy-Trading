package model;

import sirius.kernel.commons.Amount;

public class SmartMeter {
    private String smartMeterId;
    private String location;

    public String generateBlockchainReference() {
        return "resource:de.tbi.prototyp.SmartMeter#" + smartMeterId;
    }

    /**
     * Calculates the distance between the current and the given smart meter.
     *
     * @param other the given smart meter
     * @return {@Amount} of the distance, something greater than 0
     */
    public Amount calculateDistance(SmartMeter other) {
        return Amount.of(Math.random() * 100);
    }

    public String getSmartMeterId() {
        return smartMeterId;
    }

    public void setSmartMeterId(String smartMeterId) {
        this.smartMeterId = smartMeterId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
