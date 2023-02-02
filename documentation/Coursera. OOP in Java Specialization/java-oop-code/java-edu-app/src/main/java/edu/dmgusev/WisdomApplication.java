package edu.dmgusev;

/**
 * Print the line that contains the first occurrence of the word "wisdom" in the given text file.
 */

import edu.duke.FileResource;

public class WisdomApplication {
    public void findWisdom() {
        FileResource fr = new FileResource("wisdom/confucius.txt");
        for (String l : fr.lines()) {
            if (l.contains("wisdom")) {
                System.out.println(l);
                break;
            }
        }
    }
}
