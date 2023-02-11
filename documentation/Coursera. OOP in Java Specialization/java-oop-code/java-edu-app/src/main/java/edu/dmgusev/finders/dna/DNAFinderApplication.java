package edu.dmgusev.finders.dna;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DNAFinderApplication {

    public static final String START_CODON = "ATG";
    public static final String STOP_CODON1 = "TAA";
    public static final String STOP_CODON2 = "TAG";
    public static final String STOP_CODON3 = "TGA";    

    // Write the method findStopCodon that has three parameters, a String parameter named dna, an integer 
    // parameter named startIndex that represents where the first occurrence of ATG occurs in dna, and a 
    // String parameter named stopCodon. This method returns the index of the first occurrence of stopCodon 
    // that appears past startIndex and is a multiple of 3 away from startIndex. If there is no such 
    // stopCodon, this method returns the length of the dna strand.
    public int findStopCodon(@NonNull String dna, int startIndex, @NonNull String stopCodon) {

        if (startIndex < 0 || startIndex > dna.length() - stopCodon.length()) { // fail-fast - wrong index
            return dna.length();
        }

        var currentIndex = dna.toLowerCase().indexOf(stopCodon.toLowerCase(), startIndex);
        while (currentIndex != -1) {
            if ((currentIndex - startIndex) % 3 == 0) {
                return currentIndex;
            }
            currentIndex = dna.toLowerCase().indexOf(stopCodon.toLowerCase(), currentIndex + 1);
        }

        return dna.length();
    }

    // Write the void method testFindStopCodon that calls the method findStopCodon with several examples and 
    // prints out the results to check if findStopCodon is working correctly. Think about what types of 
    // examples you should check. For example, you may want to check some strings of DNA that have genes 
    // and some that do not. What other examples should you check?
    public void testFindStopCodon() {

        Map<Pair<String, Integer>, Integer> testGenes = Map.of(
            // invalid, no ATG/TAA, return length
            new ImmutablePair<>("atttggaaggTGgttgaaggTTG",             0), 23,
            // invalid, ATG exists, no TAA, return length
            new ImmutablePair<>("agggaatttaaaggATGggggagagagaattttt", 14), 34,
            // invalid, ATG exists, no TAA, return length
            new ImmutablePair<>("agagggtttgggggaagtggttatggaggtag",   22), 32,
            // valid -> ATG + TAA -> str of multiple 3 between
            new ImmutablePair<>("agggggGGtATGgggtTgTAATttaagg",        9), 18,
            // invalid -> ATG + TAA -> string not a multiple 3 in between
            new ImmutablePair<>("agggggGGtATGgtTgTAATttgagg",          9), 26,
            // valid
            new ImmutablePair<>("ATGGGTTAAGTC",                        0), 6,
            // valid
            new ImmutablePair<>("gatgctataat",                         1), 7
        );

        for (Map.Entry<Pair<String, Integer>, Integer> entry : testGenes.entrySet()) {
            int stopCodonIndex = this.findStopCodon(entry.getKey().getLeft(),
                entry.getKey().getRight(), STOP_CODON1);
            // print result of check
            System.out.println(
                String.format(
                    "For the string [%s] the proper stop codon [%s] was [%s]! Status: [%s].",
                        entry.getKey().getLeft(), STOP_CODON1,
                        (stopCodonIndex == entry.getKey().getLeft().length() ? "NOT FOUND" :
                            String.format("FOUND at %s", stopCodonIndex)),
                        (stopCodonIndex == entry.getValue() ? "PASSED" : "FAILED")));
        }

    }

    // Write the method findGene that has one String parameter dna, representing a string of DNA. In this 
    // method you should do the following:
    //      * Find the index of the first occurrence of the start codon “ATG”. If there is no “ATG”, 
    //      *   return the empty string.
    //      * Find the index of the first occurrence of the stop codon “TAA” after the first occurrence 
    //      *   of “ATG” that is a multiple of three away from the “ATG”. Hint: call findStopCodon.
    //      * Find the index of the first occurrence of the stop codon “TAG” after the first occurrence 
    //      *   of “ATG” that is a multiple of three away from the “ATG”. Find the index of the first 
    //      *   occurrence of the stop codon “TGA” after the first occurrence of “ATG” that is a multiple of 
    //      *   three away from the “ATG”. 
    //      * Return the gene formed from the “ATG” and the closest stop codon that is a multiple of three 
    //      *   away. If there is no valid stop codon and therefore no gene, return the empty string.
    public String findGene(@NonNull String dna) {

        var startCodonIndex = dna.toUpperCase().indexOf(START_CODON); // index of the start codon

        if (startCodonIndex == -1) { // start codon index, if not - return the empty string
            return "";
        }

        // check for stop codon #1: TAA
        var stopCodon1Index = this.findStopCodon(dna, startCodonIndex, STOP_CODON1);
        // check for stop codon #2: TAG
        var stopCodon2Index = this.findStopCodon(dna, startCodonIndex, STOP_CODON2);
        // check for stop codon #3: TGA
        var stopCodon3Index = this.findStopCodon(dna, startCodonIndex, STOP_CODON3);

        // if no valid stop codon - return empty string
        if (stopCodon1Index == dna.length() && (stopCodon2Index == dna.length()) 
            && (stopCodon3Index == dna.length())) {
            return "";
        }

        // find the minimum amongs of three stop codons indexes
        var stopCodonIndex = Math.min(stopCodon1Index, Math.min(stopCodon2Index, stopCodon3Index));

        // generate the valid gene and return it
        return dna.substring(startCodonIndex, stopCodonIndex + 3); // todo: move 3 - to constants
    }

    // Write the void method testFindGene that has no parameters. You should create five DNA strings. 
    // The strings should have specific test cases such as DNA with no “ATG”, DNA with “ATG” and one valid 
    // stop codon, DNA with “ATG” and multiple valid stop codons, DNA with “ATG” and no valid stop codons. 
    // Think carefully about what would be good examples to test. For each DNA string you should: 
    //      * Print the DNA string. 
    //      * Calculate the gene by sending this DNA string as an argument to findGene. If a gene exists 
    //      *   following our algorithm above, then print the gene, otherwise print the empty string.
    public void testFindGene() {

        Map<String, String> testGenes = Map.of(
            "atttggaaggTGgttgaaggTTG",            "", // invalid, no ATG/TAA, return empty string
            "agggaatttaaaggATGggggagagagaattttt", "", // invalid, ATG exists, no TAA/TGA/TAG, return empty string
            "agagggtttgggggaagtggttatggaggtag",   "", // invalid, ATG exists, no TAA/TGA/TAG, return empty string
            "agggggGGtATGgggtTgTAATttaagg",       "ATGgggtTgTAA", // valid -> ATG + TAA -> str of multiple 3 between
            "agggggGGtATGgtTgTAATttgagg",         "", // invalid -> ATG + TAA -> string not a multiple 3 in between
            "ATGGGTTAAGTC",                       "ATGGGTTAA", // valid, return gene
            "gatgctatGaTAAtAGt",                  "atgctatGa"  // valid, return gene
        );

        for (Map.Entry<String, String> entry : testGenes.entrySet()) {
            var gene = this.findGene(entry.getKey());

            System.out.println(String.format(
                "For the string: [%s] the found gene is: [%s]. Result: [%s].",
                    entry.getKey(), gene, (gene.equals(entry.getValue()) ? "PASSED" : "FAILED")));
        }

    }

    // Write the void method printAllGenes that has one String parameter dna, representing a string of DNA. 
    // In this method you should repeatedly find genes and print each one until there are no more genes. 
    // Hint: remember you learned a while(true) loop and break.
    public void printAllGenes() {
        // todo: implementation
    }

    public static void main(String[] args) throws IOException {
        log.info("Tag Finder application is starting...");

        var application = new DNAFinderApplication();

        // test the first method
        System.out.println("\n");
        log.info("Testing: findStopCodon()...");
        application.testFindStopCodon();

        // test the first method
        System.out.println("\n");
        log.info("Testing: findGene()...");
        application.testFindGene();

        // test the first method
        System.out.println("\n");
        log.info("Testing: printAllGenes()...");
        application.printAllGenes();

        System.out.println("\n");

    }

}
