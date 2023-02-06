package edu.dmgusev.finders.dna;

import java.io.IOException;
import java.util.Map;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DNAFinderApplication {

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
            if (currentIndex - startIndex % 3 == 0) {
                return currentIndex;
            }

            currentIndex = dna.toLowerCase().indexOf(stopCodon.toLowerCase(), currentIndex);
        }

        return dna.length();
    }

    // Write the void method testFindStopCodon that calls the method findStopCodon with several examples and 
    // prints out the results to check if findStopCodon is working correctly. Think about what types of 
    // examples you should check. For example, you may want to check some strings of DNA that have genes 
    // and some that do not. What other examples should you check?
    public void testFindStopCodon() {

        var stopCodon = "TAA";

        Map<String, Integer> testGenes = Map.of(
            "atttggaaggTGgttgaaggTTG",            23, // invalid, no TAA, return length
            "agggaatttaaaggATGggggagagagaattttt", 34, // invalid, no TAA, return length
            "agagggtttgggggaagtggttatggaggtag",   32, // invalid, no ATG or TAA, return length
            "agggggGGtATGgggtTgTAATttaagg",       18, // valid -> ATG + TAA -> str of multiple 3 between
            "agggggGGtATGgtTgTAATttaagg",         26, // invalid -> ATG + TAA -> string not a multiple 3 between
            "ATGGGTTAAGTC",                       6,  // valid
            "gatgctataat",                        7   // valid
        );

        for (Map.Entry<String, Integer> entry : testGenes.entrySet()) {
            int stopCodonIndex = this.findStopCodon(entry.getKey(), 0, stopCodon);
            System.out.println(
                String.format(
                    "For the string [%s] the stop codon [%s] was [%s]! Status: [%s].",
                        entry.getKey(), stopCodon, 
                        (stopCodonIndex == entry.getKey().length() ? "NOT FOUND" : 
                            String.format("FOUND at %s", stopCodonIndex)), 
                        (stopCodonIndex == entry.getValue() ? "PASSED" : "FAILED")));
        }

    }

    //5. Write the method findGene that has one String parameter dna, representing a string of DNA. In this method you should do the following:
// * Find the index of the first occurrence of the start codon “ATG”. If there is no “ATG”, return the empty string.
// * Find the index of the first occurrence of the stop codon “TAA” after the first occurrence of “ATG” that is a multiple of three away from the “ATG”. Hint: call findStopCodon.
// * Find the index of the first occurrence of the stop codon “TAG” after the first occurrence of “ATG” that is a multiple of three away from the “ATG”. Find the index of the first occurrence of the stop codon “TGA” after the first occurrence of “ATG” that is a multiple of three away from the “ATG”. 
// * Return the gene formed from the “ATG” and the closest stop codon that is a multiple of three away. If there is no valid stop codon and therefore no gene, return the empty string.

//6. Write the void method testFindGene that has no parameters. You should create five DNA strings. The strings should have specific test cases such as DNA with no “ATG”, DNA with “ATG” and one valid stop codon, DNA with “ATG” and multiple valid stop codons, DNA with “ATG” and no valid stop codons. Think carefully about what would be good examples to test. For each DNA string you should: 
// * Print the DNA string. 
// * Calculate the gene by sending this DNA string as an argument to findGene. If a gene exists following our algorithm above, then print the gene, otherwise print the empty string.

//7. Write the void method printAllGenes that has one String parameter dna, representing a string of DNA. In this method you should repeatedly find genes and print each one until there are no more genes. Hint: remember you learned a while(true) loop and break.   

    public static void main(String[] args) throws IOException {
        log.info("Tag Finder application is starting...");

        var application = new DNAFinderApplication();

        // test the first method
        System.out.println("\n\n");
        log.info("Testing: findStopCodon()...");
        application.testFindStopCodon();

    }

}
