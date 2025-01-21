package org.outfoxedfinal.logic;

// File: Clue.java
// Updated to integrate with the thief and suspect logic

import org.outfoxedfinal.entity.Suspect;

// File: Clue.java
// Updated to reveal clues randomly but match items with the thief

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Clue {
    private String description;
    private boolean revealed;

    // Constructor
    public Clue(String description) {
        this.description = description;
        this.revealed = false;
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    @Override
    public String toString() {
        return "Clue{" +
                "description='" + description + '\'' +
                ", revealed=" + revealed +
                '}';
    }

    // Initialize clues based on thief's items
    public static List<Clue> initializeCluesForThief(Suspect thief) {
        List<Clue> clues = new ArrayList<>();
        Random random = new Random();

        // Add all possible clues
        List<String> allItems = List.of(
                "gloves", "1 eye glasses", "umbrella", "clock", "glasses", "flower",
                "stick", "scarf", "hat", "cloak", "jewelery", "bag"
        );

        for (String item : allItems) {
            clues.add(new Clue(item));
        }

        // Randomly reveal clues
        for (Clue clue : clues) {
            boolean shouldReveal = random.nextBoolean();
            clue.setRevealed(shouldReveal);
        }

        return clues;
    }

    // Check if the revealed clue belongs to the thief
    public static boolean doesClueBelongToThief(Clue clue, Suspect thief) {
        if (clue.isRevealed() && List.of(thief.getItems()).contains(clue.getDescription())) {
            return true;
        }
        return false;
    }
}
