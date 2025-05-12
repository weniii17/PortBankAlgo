package com.mycompany.portmanagement;

import java.util.ArrayList;
import java.util.List;

public class BankerAlgorithm {
    // Available resources
    private int[] available;

    // Ship identifiers
    private ArrayList<String> shipIds;

    // Maximum resources that can be allocated to each ship
    private ArrayList<int[]> maximum;

    // Currently allocated resources to each ship
    private ArrayList<int[]> allocation;

    // Resources needed by each ship (maximum - allocation)
    private ArrayList<int[]> need;

    // Number of resource types
    private final int NUM_RESOURCES = 3; // Docks, Cranes, Forklifts

    // Constructor
    public BankerAlgorithm(int totalDocks, int totalCranes, int totalForklifts) {
        // Initialize resources
        available = new int[NUM_RESOURCES];
        available[0] = totalDocks;
        available[1] = totalCranes;
        available[2] = totalForklifts;

        // Initialize data structures
        shipIds = new ArrayList<>();
        maximum = new ArrayList<>();
        allocation = new ArrayList<>();
        need = new ArrayList<>();
    }

    /**
     * Find the index of a ship by its ID
     * 
     * @param shipId The identifier for the ship
     * @return Index of the ship, or -1 if not found
     */
    private int findShipIndex(String shipId) {
        for (int i = 0; i < shipIds.size(); i++) {
            if (shipIds.get(i).equals(shipId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Add a new ship to the system
     * 
     * @param shipId       The identifier for the ship
     * @param maxDocks     Maximum docks needed
     * @param maxCranes    Maximum cranes needed
     * @param maxForklifts Maximum forklifts needed
     */
    public void addShip(String shipId, int maxDocks, int maxCranes, int maxForklifts) {
        int[] max = { maxDocks, maxCranes, maxForklifts };
        int[] alloc = { 0, 0, 0 };
        int[] needResources = { maxDocks, maxCranes, maxForklifts };

        shipIds.add(shipId);
        maximum.add(max);
        allocation.add(alloc);
        need.add(needResources);
    }

    /**
     * Request resources for a ship
     * 
     * @param shipId           The identifier for the ship
     * @param requestDocks     Docks requested
     * @param requestCranes    Cranes requested
     * @param requestForklifts Forklifts requested
     * @return True if the request can be granted safely, false otherwise
     */
    public boolean requestResources(String shipId, int requestDocks, int requestCranes, int requestForklifts) {
        int[] request = { requestDocks, requestCranes, requestForklifts };

        int shipIndex = findShipIndex(shipId);
        if (shipIndex == -1) {
            return false; // Ship does not exist in the system
        }

        int[] needResources = need.get(shipIndex);

        // Check if the request exceeds the maximum claim
        for (int i = 0; i < NUM_RESOURCES; i++) {
            if (request[i] > needResources[i]) {
                return false; // Request exceeds maximum claim
            }
        }

        // Check if resources are available
        for (int i = 0; i < NUM_RESOURCES; i++) {
            if (request[i] > available[i]) {
                return false; // Resources not available
            }
        }

        // Try to allocate resources to see if it's safe
        // Save current state
        int[] savedAvailable = available.clone();
        int[] savedAllocation = allocation.get(shipIndex).clone();
        int[] savedNeed = need.get(shipIndex).clone();

        // Temporarily allocate the resources
        for (int i = 0; i < NUM_RESOURCES; i++) {
            available[i] -= request[i];
            allocation.get(shipIndex)[i] += request[i];
            need.get(shipIndex)[i] -= request[i];
        }

        // Check if the system is in a safe state
        boolean safe = isSafe();

        if (!safe) {
            // Restore previous state if not safe
            available = savedAvailable;
            allocation.set(shipIndex, savedAllocation);
            need.set(shipIndex, savedNeed);
        }

        return safe;
    }

    /**
     * Release resources allocated to a ship
     * 
     * @param shipId           The identifier for the ship
     * @param releaseDocks     Docks to release
     * @param releaseCranes    Cranes to release
     * @param releaseForklifts Forklifts to release
     * @return True if resources were released successfully
     */
    public boolean releaseResources(String shipId, int releaseDocks, int releaseCranes, int releaseForklifts) {
        int[] release = { releaseDocks, releaseCranes, releaseForklifts };

        int shipIndex = findShipIndex(shipId);
        if (shipIndex == -1) {
            return false; // Ship does not exist
        }

        int[] alloc = allocation.get(shipIndex);

        // Check if release request is valid
        for (int i = 0; i < NUM_RESOURCES; i++) {
            if (release[i] > alloc[i]) {
                return false; // Trying to release more than allocated
            }
        }

        // Release resources
        for (int i = 0; i < NUM_RESOURCES; i++) {
            allocation.get(shipIndex)[i] -= release[i];
            need.get(shipIndex)[i] += release[i];
            available[i] += release[i];
        }

        return true;
    }

    /**
     * Remove a ship from the system and release all its resources
     * 
     * @param shipId The identifier for the ship
     * @return True if the ship was successfully removed
     */
    public boolean removeShip(String shipId) {
        int shipIndex = findShipIndex(shipId);
        if (shipIndex == -1) {
            return false; // Ship does not exist
        }

        int[] alloc = allocation.get(shipIndex);

        // Release all resources
        for (int i = 0; i < NUM_RESOURCES; i++) {
            available[i] += alloc[i];
        }

        // Remove ship from data structures
        shipIds.remove(shipIndex);
        maximum.remove(shipIndex);
        allocation.remove(shipIndex);
        need.remove(shipIndex);

        return true;
    }

    /**
     * Check if the system is in a safe state
     * 
     * @return True if the system is in a safe state, false otherwise
     */
    public boolean isSafe() {
        return getSafeSequence() != null;
    }

    /**
     * Calculate and return a safe sequence of ship IDs
     * 
     * @return List of ship IDs in a safe sequence or null if no safe sequence
     *         exists
     */
    public List<String> getSafeSequence() {
        // Create working copies
        int[] work = available.clone();
        boolean[] finish = new boolean[shipIds.size()];
        List<String> safeSequence = new ArrayList<>();

        // Find a safe sequence
        int count = 0;
        while (count < shipIds.size()) {
            boolean found = false;

            for (int i = 0; i < shipIds.size(); i++) {
                if (!finish[i]) {
                    boolean canAllocate = true;

                    // Check if all needed resources are available
                    for (int j = 0; j < NUM_RESOURCES; j++) {
                        if (need.get(i)[j] > work[j]) {
                            canAllocate = false;
                            break;
                        }
                    }

                    if (canAllocate) {
                        // All resources can be allocated
                        for (int k = 0; k < NUM_RESOURCES; k++) {
                            work[k] += allocation.get(i)[k];
                        }
                        finish[i] = true;
                        found = true;
                        count++;
                        safeSequence.add(shipIds.get(i));
                    }
                }
            }

            if (!found) {
                // No suitable process found, system is not safe
                return null;
            }
        }

        // If we've processed all ships, return the safe sequence
        return safeSequence;
    }

    /**
     * Get current available resources
     * 
     * @return Array of available resources [docks, cranes, forklifts]
     */
    public int[] getAvailable() {
        return available.clone();
    }

    /**
     * Get current allocation for a specific ship
     * 
     * @param shipId The identifier for the ship
     * @return Array of allocated resources [docks, cranes, forklifts]
     */
    public int[] getAllocation(String shipId) {
        int shipIndex = findShipIndex(shipId);
        if (shipIndex != -1) {
            return allocation.get(shipIndex).clone();
        }
        return null;
    }

    /**
     * Get needed resources for a specific ship
     * 
     * @param shipId The identifier for the ship
     * @return Array of needed resources [docks, cranes, forklifts] or null if ship
     *         not found
     */
    public int[] getNeeded(String shipId) {
        int shipIndex = findShipIndex(shipId);
        if (shipIndex != -1) {
            return need.get(shipIndex).clone();
        }
        return null;
    }

    /**
     * Get maximum resources for a specific ship
     * 
     * @param shipId The identifier for the ship
     * @return Array of maximum resources [docks, cranes, forklifts] or null if ship
     *         not found
     */
    public int[] getMaximum(String shipId) {
        int shipIndex = findShipIndex(shipId);
        if (shipIndex != -1) {
            return maximum.get(shipIndex).clone();
        }
        return null;
    }

    /**
     * Get all ships in the system
     * 
     * @return List of ship IDs
     */
    public List<String> getShips() {
        return new ArrayList<>(shipIds);
    }

    /**
     * Check if a ship exists in the system
     * 
     * @param shipId The identifier for the ship
     * @return True if the ship exists, false otherwise
     */
    public boolean shipExists(String shipId) {
        return findShipIndex(shipId) != -1;
    }

    /**
     * Get a formatted string representation of the current state
     * 
     * @return String with detailed info about the current algorithm state
     */
    public String getStateInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available: [D:").append(available[0])
                .append(", C:").append(available[1])
                .append(", F:").append(available[2])
                .append("]\n");

        for (int i = 0; i < shipIds.size(); i++) {
            sb.append("Ship ").append(shipIds.get(i))
                    .append(" - Max: [D:").append(maximum.get(i)[0])
                    .append(", C:").append(maximum.get(i)[1])
                    .append(", F:").append(maximum.get(i)[2])
                    .append("], Allocated: [D:").append(allocation.get(i)[0])
                    .append(", C:").append(allocation.get(i)[1])
                    .append(", F:").append(allocation.get(i)[2])
                    .append("], Need: [D:").append(need.get(i)[0])
                    .append(", C:").append(need.get(i)[1])
                    .append(", F:").append(need.get(i)[2])
                    .append("]\n");
        }

        return sb.toString();
    }
}
