package org.outfoxedfinal.logic;

import java.util.*;

public class Clue {
    private final List<String> allClueItems = List.of(
            "umbrella", "gloves", "hat", "glasses", "1 eye glasses",
            "scarf", "clock", "stick", "jewelry", "bag", "flower", "cloak"
    );

    private final List<int[]> clueCenters; // Stores the center of each clue
    private final Map<String, Boolean> activeClues; // Tracks if a clue is still active
    private List<String> randomizedClues;

    public Clue() {
        this.clueCenters = initializeClueCenters();
        this.activeClues = new HashMap<>();
        shuffleClues();
        initializeClueStatus();
    }
    // Define Clue Center Points (Each covers a 2x2 area)
    private List<int[]> initializeClueCenters() {
        return List.of(
                new int[]{1, 15}, new int[]{2, 11}, new int[]{6, 13}, new int[]{15,10},
                new int[]{10, 2}, new int[]{12, 6}, new int[]{15, 2}, new int[]{13, 14},
                new int[]{10, 11}, new int[]{5, 5}, new int[]{4, 1}, new int[]{1, 4}
        );
    }

    // Initialize All Clues as Active
    private void initializeClueStatus() {
        for (int[] center : clueCenters) {
            String key = center[0] + "," + center[1];
            activeClues.put(key, true); // All clues start active
        }
    }

    // Shuffle Clues Once Per Game
    private void shuffleClues() {
        randomizedClues = new ArrayList<>(allClueItems);
        Collections.shuffle(randomizedClues);
    }

    // **Returns Clue Items Only If the Clue is Active
    public List<String> getClueItemsAtLocation(int row, int col) {
        int[] center = getClueCenter(row, col);
        if (center == null || !isClueActive(center[0], center[1])) {
            return new ArrayList<>(); // No clue if it's deactivated
        }
        return new ArrayList<>(randomizedClues);
    }

    // Get the Center Point of a Clue Covering a Given Tile
    private int[] getClueCenter(int row, int col) {
        for (int[] center : clueCenters) {
            if (coversTile(center, row, col)) {
                return center;
            }
        }
        return null;
    }

    // Check if a Tile is Within a 2x2 Clue Area
    private boolean coversTile(int[] center, int row, int col) {
        return (row == center[0] || row == center[0] + 1) &&
                (col == center[1] || col == center[1] + 1);
    }

    // Deactivate Clue When Stepped On
    public void deactivateClue(int row, int col) {
        int[] center = getClueCenter(row, col);

        if (center != null) {
            int centerRow = center[0];
            int centerCol = center[1];

            // Now deactivate all 4 squares of the clue
            activeClues.put(centerRow + "," + centerCol, false);
            activeClues.put((centerRow + 1) + "," + centerCol, false);
            activeClues.put(centerRow + "," + (centerCol + 1), false);
            activeClues.put((centerRow + 1) + "," + (centerCol + 1), false);
        }
    }

    // Check if a Clue is Still Active
    public boolean isClueActive(int row, int col) {
        int[] center = getClueCenter(row, col);
        return center != null && activeClues.getOrDefault(center[0] + "," + center[1], false);
    }


    // Check if a Tile is Part of Any Clue
    public boolean isClueLocation(int row, int col) {
        return getClueCenter(row, col) != null;
    }
}
