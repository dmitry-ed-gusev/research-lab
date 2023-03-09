package edu.dmgusev.finders.dna;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import edu.duke.FileResource;
import edu.duke.StorageResource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/** */

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

    // Write the method cgRatio that has one String parameter dna, and returns the ratio of C’s and G’s in 
    // dna as a fraction of the entire strand of DNA. For example if the String were “ATGCCATAG,” then 
    // cgRatio would return 4/9 or .4444444.
    // ----------
    // Hint: 9/2 uses integer division because you are dividing an integer by an integer and thus Java 
    //       thinks you want the result to be an integer. If you want the result to be a decimal number, 
    //       then make sure you convert one of the integers to a decimal number by changing it to a float. 
    //       For example, (float) 9/2 is interpreted by Java as 9.0/2 and if one of the numbers is a decimal, 
    //       then Java assumes you want the result to be a decimal number. Thus (float) 9/2 is 4.5.
    public void cgRatio() {
        // todo: implementation
    }

    // Write a method countCTG that has one String parameter dna, and returns the number of times the codon 
    // CTG appears in dna.
    public void countCTG(){
        // todo: implementation
    }

    // Write the void method processGenes that has one parameter sr, which is a StorageResource of strings. 
    // This method processes all the strings in sr to find out information about them. 
    // Specifically, it should: 
    //      * print all the Strings in sr that are longer than 9 characters
    //      * print the number of Strings in sr that are longer than 9 characters
    //      * print the Strings in sr whose C-G-ratio is higher than 0.35
    //      * print the number of strings in sr whose C-G-ratio is higher than 0.35
    //      * print the length of the longest gene in sr
    public void processGenes() {
        // todo: implementation
    }

    // Write a method testProcessGenes. This method will call your processGenes method on different test 
    // cases. Think of five DNA strings to use as test cases. These should include: one DNA string that has 
    // some genes longer than 9 characters, one DNA string that has no genes longer than 9 characters, one 
    // DNA string that has some genes whose C-G-ratio is higher than 0.35, and one DNA string that has some 
    // genes whose C-G-ratio is lower than 0.35. Write code in testProcessGenes to call processGenes five 
    // times with StorageResources made from each of your five DNA string test cases.
    public void testProcessGenes() {
        // todo: implementation
    }

    public void processGenes(StorageResource sr){
        //print strings longer than 9 characters
        
        int geneCount = 0;
        int geneCountAbove60 = 0;
        int cgRatioCount = 0;
        int geneLength = 0;
        String longestGene = "";
        
        for (String s: sr.data()){
            geneCount++;
           // if (s.length() > 9){
              if (s.length() > 60){ //modified for second test case
                //System.out.println("This string has a length greater than 60: " + s);
                geneCountAbove60 = geneCountAbove60 + 1;           
            }
            
            double out = cgRatio(s);
            
            if (out > 0.35){
               // System.out.println("This string has a C-G-ratio greater than .35: " + s);
                cgRatioCount = cgRatioCount + 1;
            }
            
            if (s.length() > geneLength){
                geneLength = s.length();
                longestGene = s;
            }
        }
        
        System.out.println("The number of strings in sr longer than 60 characters: " + geneCountAbove60);
        System.out.println("The number of strings in sr with C-G-ratio higher than 0.35: " + cgRatioCount);
        System.out.println("Length of longest gene: " + geneLength);
        System.out.println("Longest gene is: " + longestGene);
        System.out.println("Number of genes in the storage list is: " + geneCount);
    }
    
    public void testProcessGenes(){
        String dna2 = "ATGTTAATAGTGATTTAAATGTAA";//multiple stop codons
        System.out.println("Dna 2 is " + dna2);
        StorageResource dnaList = getAllGenes(dna2);
        
        for (String s: dnaList.data()){
            System.out.println("Gene is " + s);
        }
        
        processGenes(dnaList);   
    }
    public void testProcessGenesFromFile(){
        FileResource fr = new FileResource("brca1line.fa");
        String dna = fr.asString().toUpperCase();
        
        System.out.println("dna is " + dna);
        StorageResource geneList = getAllGenes(dna);    
        //for (String s: geneList.data()){
         //   System.out.println("Gene is " + s);
       // }
       processGenes(geneList);
    }
        
        
    public double cgRatio(String dna){
       int startIndex = 0;
       int num = 0;
       int denom = dna.length();
       int currIndex = 0;
       
       while (true){
           int cIndex = dna.indexOf("C", startIndex);
           //System.out.println("cIndex is " + cIndex);
           int gIndex = dna.indexOf("G", startIndex);
           //System.out.println("gIndex is " + gIndex);
           
            if (cIndex == -1){
                currIndex = gIndex;
                //System.out.println("currIndex is " + currIndex);
            }
            else if (gIndex == -1){
                currIndex = cIndex;
               // System.out.println("currIndex is " + currIndex);
            }
            else {
                currIndex = Math.min(cIndex, gIndex);
                //System.out.println("currIndex is " + currIndex);
            }
              
           if (currIndex == -1){
               break;
            }
            else
            {
                num = num + 1;
                startIndex = currIndex + 1;
            }
        }
        return ((double) num)/denom ;  
    }
           
    
    public String findGene(String dna, int where){
            int startIndex = dna.indexOf("ATG", where);
            System.out.println("Start Index is: " + startIndex);
            if (startIndex == -1){
                return "";
            }
            int stopIndexTAA = findStopCodon(dna, startIndex, "TAA");
            System.out.println("stopIndexTAA: " + stopIndexTAA);
            int stopIndexTAG = findStopCodon(dna, startIndex, "TAG");
            System.out.println("stopIndexTAG: " + stopIndexTAG);
            int stopIndexTGA = findStopCodon(dna, startIndex, "TGA");
            System.out.println("stopIndexTGA " + stopIndexTGA);
            
            int minStopIndex1 = Math.min(stopIndexTAA, stopIndexTAG);
            int minStopIndex2 = Math.min(minStopIndex1, stopIndexTGA);
            
            if (minStopIndex2 == dna.length()){
                return "";
            }
            else
            {
            //System.out.println("Gene Stop index is: " + minStopIndex2);
            String gene = dna.substring(startIndex, minStopIndex2+3);
           // System.out.println("Gene is " + gene);
            return gene;   
        }
    }
    public StorageResource getAllGenes(String dna){
        StorageResource geneList = new StorageResource();
        int startIndex = dna.indexOf("ATG");
        //System.out.println("start Index is " + startIndex);
        
        
        while (startIndex != -1){
            String currGene = findGene(dna,startIndex);
           // System.out.println("Current gene is: " + currGene);
            
            if (currGene.isEmpty()){
                startIndex = dna.indexOf("ATG",startIndex + 3);
               // System.out.println("New Start index is " + startIndex);
            }
            else{
            geneList.add(currGene);
           // System.out.println("Gene added to storage list");
            startIndex = dna.indexOf(currGene, startIndex) + currGene.length();
            //System.out.println("New Start index is " + startIndex);
            
        }
        
      
        }
        return geneList;
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
