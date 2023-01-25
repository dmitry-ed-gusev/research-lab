package edu.dmgusev.dna;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/** Assignment II. Tag Finder assignment application. */

@Slf4j
public class TagFinderApplication {

    public static String CODON_ATG = "ATG";
    public static String CODON_TAA = "TAA";

    /***/
    public String findFirstSimpleGene(@NonNull String geneString, String startCodon, String stopCodon) {

        // pick up the codons from parameters
        var localStartCodon = (StringUtils.trimToEmpty(startCodon).equals("") ? CODON_ATG : 
            startCodon.toUpperCase());
        var localStopCodon  = (StringUtils.trimToEmpty(stopCodon).equals("") ? CODON_TAA : 
            stopCodon.toUpperCase());

        // find the first (starting) tag
        var startIndex = geneString.toUpperCase().indexOf(localStartCodon);
        if (startIndex == -1) {
            return "";
        }

        // find the last (ending) tag
        var endIndex = geneString.toUpperCase().indexOf(localStopCodon, 
            startIndex + localStopCodon.length());
        if (endIndex == -1 || (endIndex - startIndex) % 3 != 0) {
            return "";
        }

        // return substring between two tags (including both tags)
        return geneString.substring(startIndex, endIndex + localStopCodon.length());
    }

    /***/
    public void testFindFirstSimpleGene() {

        // sample cases
        Map<String, Boolean> genes = Map.of(
            "atttggaaggTGgttgaaggTTG",            false, // invalid - no ATG
            "agggaatttaaaggATGggggagagagaattttt", false, // invalid - no TAA
            "agagggtttgggggaagtggttatggaggtag",   false, // invalid - no ATG or TAA (both)
            "agggggGGtATGgggtTgTAATttaagg",       true,  // valid - gene (ATG + TAA + string of multiple 3 between)
            "agggggGGtATGgtTgTAATttaagg",         false, // invalid - ATG + TAA, but string not a multiple 3 between
            "ATGGGTTAAGTC", true, // valid
            "gatgctataat", true // valid
        );

        for (Map.Entry<String, Boolean> entry : genes.entrySet()) { // testing the method
            String gene = this.findFirstSimpleGene(entry.getKey(), "atg", "taa");
            if (!StringUtils.isBlank(gene)) {
                System.out.println(String.format("FOUND: for string [%s] found gene [%s] - [%s]!", 
                    entry.getKey(), gene, (entry.getValue() ? "OK" : "Failed")));
            } else {
                System.out.println(String.format("NOT FOUND: for the string [%s] gene not found! [%s]",
                    entry.getKey(), (entry.getValue() ? "Failed" : "OK")));
            }
        } // end of FOR

    }

    public boolean twoOccurrences(@NonNull String stringA, @NonNull String stringB) {

        // first occurence of A in B
        var index = stringB.toLowerCase().indexOf(stringA.toLowerCase());
        if (index == -1) {
            return false;
        }

        // second occurence of A in B
        index = stringB.toLowerCase().indexOf(stringA.toLowerCase(), index + stringA.length());
        if (index == -1) {
            return false;
        }

        return true;
    }

    // Write the method named twoOccurrences that has two String parameters named stringa and stringb. 
    // This method returns true if stringa appears at least twice in stringb, otherwise it returns false. 

    // For example, the call twoOccurrences(“by”, “A story by Abby Long”) returns true as there are two 
    // occurrences of “by”, the call twoOccurrences(“a”, “banana”) returns true as there are three occurrences 
    // of “a” so “a” occurs at least twice, and the call twoOccurrences(“atg”, “ctgtatgta”) returns false as 
    // there is only one occurence of “atg”.

    public void testTwoOccurences() {

        Map<Pair<String, String>, Boolean> samples = Map.of(

        );

        for (Map.Entry<Pair<String, String>, Boolean> entry : samples.entrySet()) {
        } // end of FOR

    }

    public static void main(String[] args) throws IOException {
        log.info("Tag Finder application is starting...");

        var application = new TagFinderApplication();

        // test the first method
        application.testFindFirstSimpleGene();

        // test the second method
        application.testTwoOccurences();

        log.info("Tag Finder application is done.");
    }

}
