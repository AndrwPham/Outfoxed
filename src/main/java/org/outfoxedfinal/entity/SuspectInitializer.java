package org.outfoxedfinal.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuspectInitializer {

    public static List<Suspect> initializeSuspects() {
        List<Suspect> suspects = new ArrayList<>(List.of(
                new Suspect("Maggie", false, new String[]{"gloves", "1 eye glasses", "umbrella"}),
                new Suspect("Gertrude", false, new String[]{"stick", "jewelry", "flower"}),
                new Suspect("Belvedere", false, new String[]{"hat", "1 eye glasses", "clock"}),
                new Suspect("Beatrice", false, new String[]{"umbrella", "scarf", "gloves"}),
                new Suspect("Riley", false, new String[]{"hat", "clock", "scarf"}),
                new Suspect("Daisy", false, new String[]{"stick", "cloak", "jewelery"}),
                new Suspect("Arthur", false, new String[]{"bag", "hat", "scarf"}),
                new Suspect("Edith", false, new String[]{"bag", "cloak", "1 eye glasses"}),
                new Suspect("Mary", false, new String[]{"bag", "scarf", "flower"}),
                new Suspect("Ingrid", false, new String[]{"umbrella", "gloves", "glasses"}),
                new Suspect("Henry", false, new String[]{"stick", "hat", "glasses"}),
                new Suspect("Harold", false, new String[]{"bag", "glasses", "cloak"})
        ));

        Collections.shuffle(suspects); // ✅ Shuffle list for a random suspect order

        return suspects;
    }

}

