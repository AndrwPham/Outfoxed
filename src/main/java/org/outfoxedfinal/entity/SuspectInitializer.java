package org.outfoxedfinal.entity;

import java.util.ArrayList;
import java.util.List;

public class SuspectInitializer {

    public static List<Suspect> initializeSuspects() {
        List<Suspect> suspects = new ArrayList<>();

        // Assign names, set revealed to false, and assign predefined clues
        suspects.add(new Suspect("Maggie", false, new String[]{"gloves", "1 eye glasses", "umbrella"}));
        suspects.add(new Suspect("Gertrude", false, new String[]{"stick", "jewelry", "flower"}));
        suspects.add(new Suspect("Belvedere", false, new String[]{"hat", "1 eye glasses", "clock"}));
        suspects.add(new Suspect("Beatrice", false, new String[]{"umbrella", "scarf", "gloves"}));
        suspects.add(new Suspect("Riley", false, new String[]{"hat", "clock", "scarf"}));
        suspects.add(new Suspect("Daisy", false, new String[]{"stick", "cloak", "jewelery"}));
        suspects.add(new Suspect("Arthur", false, new String[]{"bag", "hat", "scarf"}));
        suspects.add(new Suspect("Edith", false, new String[]{"bag", "cloak", "1 eye glasses"}));
        suspects.add(new Suspect("Mary", false, new String[]{"bag", "scarf", "flower"}));
        suspects.add(new Suspect("Ingrid", false, new String[]{"umbrella", "gloves", "glasses"}));
        suspects.add(new Suspect("Henry", false, new String[]{"stick", "hat", "glasses"}));
        suspects.add(new Suspect("Harold", false, new String[]{"bag", "glasses", "cloak"}));

        return suspects;
    }
}

