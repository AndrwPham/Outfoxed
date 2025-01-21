package org.outfoxedfinal.entity;

public class Suspect {
    private String name;
    private boolean revealed;
    private String[] item;

    public Suspect(String name, boolean revealed, String[] item) {
        this.name = name;
        this.revealed = revealed;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public String[] getItems() {
        return item;
    }
    @Override
    public String toString() {
        return name; // Return the suspect's name
    }
}
