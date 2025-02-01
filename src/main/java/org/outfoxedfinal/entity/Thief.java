package org.outfoxedfinal.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Thief {
    private Suspect thief;

    public void selectThief(List<Suspect> suspects) {
        if (suspects != null && !suspects.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(suspects.size());
            this.thief = suspects.get(randomIndex);
            System.out.println("Thief selected: " + thief.getName());
        }
    }

    public Suspect getThief() {
        return thief;
    }

    public boolean isThief(Suspect accused) {
        return thief != null && thief.equals(accused);
    }
    public List<String> getThiefItems() {
        if (thief == null) {
            throw new IllegalStateException("Thief has not been initialized");
        }
        return Arrays.asList(thief.getItems()); // Assuming `getItems()` returns an array of strings
    }

}