package com.mycompany.portmanagement;

/**
 * Represents a ship in the port management system
 */
public class Ship {
    private String id;
    private int maxDocks;
    private int maxCranes;
    private int maxForklifts;
    private int allocatedDocks;
    private int allocatedCranes;
    private int allocatedForklifts;

    /**
     * Constructor for Ship
     * 
     * @param id           The ship's identifier
     * @param maxDocks     Maximum docks needed
     * @param maxCranes    Maximum cranes needed
     * @param maxForklifts Maximum forklifts needed
     */
    public Ship(String id, int maxDocks, int maxCranes, int maxForklifts) {
        this.id = id;
        this.maxDocks = maxDocks;
        this.maxCranes = maxCranes;
        this.maxForklifts = maxForklifts;
        this.allocatedDocks = 0;
        this.allocatedCranes = 0;
        this.allocatedForklifts = 0;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public int getMaxDocks() {
        return maxDocks;
    }

    public int getMaxCranes() {
        return maxCranes;
    }

    public int getMaxForklifts() {
        return maxForklifts;
    }

    public int getAllocatedDocks() {
        return allocatedDocks;
    }

    public void setAllocatedDocks(int allocatedDocks) {
        this.allocatedDocks = allocatedDocks;
    }

    public int getAllocatedCranes() {
        return allocatedCranes;
    }

    public void setAllocatedCranes(int allocatedCranes) {
        this.allocatedCranes = allocatedCranes;
    }

    public int getAllocatedForklifts() {
        return allocatedForklifts;
    }

    public void setAllocatedForklifts(int allocatedForklifts) {
        this.allocatedForklifts = allocatedForklifts;
    }

    /**
     * Calculate remaining needs for each resource type
     * 
     * @return Array of remaining needs [docks, cranes, forklifts]
     */
    public int[] getRemainingNeeds() {
        return new int[] {
                maxDocks - allocatedDocks,
                maxCranes - allocatedCranes,
                maxForklifts - allocatedForklifts
        };
    }

    /**
     * Get needed docks
     * 
     * @return Number of docks still needed
     */
    public int getNeededDocks() {
        return maxDocks - allocatedDocks;
    }

    /**
     * Get needed cranes
     * 
     * @return Number of cranes still needed
     */
    public int getNeededCranes() {
        return maxCranes - allocatedCranes;
    }

    /**
     * Get needed forklifts
     * 
     * @return Number of forklifts still needed
     */
    public int getNeededForklifts() {
        return maxForklifts - allocatedForklifts;
    }

    /**
     * Update allocated resources
     * 
     * @param docks     Number of docks to add (can be negative for release)
     * @param cranes    Number of cranes to add (can be negative for release)
     * @param forklifts Number of forklifts to add (can be negative for release)
     */
    public void updateAllocated(int docks, int cranes, int forklifts) {
        this.allocatedDocks += docks;
        this.allocatedCranes += cranes;
        this.allocatedForklifts += forklifts;
    }

    @Override
    public String toString() {
        return String.format("%s (Docks: %d/%d/%d, Cranes: %d/%d/%d, Forklifts: %d/%d/%d)",
                id, allocatedDocks, maxDocks, getNeededDocks(),
                allocatedCranes, maxCranes, getNeededCranes(),
                allocatedForklifts, maxForklifts, getNeededForklifts());
    }
}
