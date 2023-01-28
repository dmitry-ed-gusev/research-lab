package edu.dmgusev.dna;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import edu.duke.FileResource;
import edu.duke.IResource;
import edu.duke.URLResource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/** Assignment II. Tag Finder assignment application. */

@Slf4j
public class TagFinderApplication {

    public static final String CODON_ATG = "ATG";
    public static final String CODON_TAA = "TAA";

    // 1. Write the method findSimpleGene that has one String parameter dna, representing a string of DNA. 
    // This method does the following:
    //      * Finds the index position of the start codon “ATG”. If there is no “ATG”, return the empty string.
    //      * Finds the index position of the first stop codon “TAA” appearing after the “ATG” that was found. 
    //          If there is no such “TAA”, return the empty string. 
    //      * If the length of the substring between the “ATG” and “TAA” is a multiple of 3, then return the 
    //          substring that starts with that “ATG” and ends with that “TAA”.
    //
    // 2. The method findSimpleGene has one parameter for the DNA string named dna. Modify findSimpleGene 
    // to add two additional parameters, one named startCodon for the start codon and one named stopCodon 
    // for the stop codon. What additional changes do you need to make for the program to compile? After 
    // making all changes, run your program to check that you get the same output as before.
    //
    // 3. Modify the findSimpleGene method to work with DNA strings that are either all uppercase letters 
    // such as “ATGGGTTAAGTC” or all lowercase letters such as “gatgctataat”. Calling findSimpleGene with 
    // “ATGGGTTAAGTC” should return the answer with uppercase letters, the gene “ATGGGTTAA”, and calling 
    // findSimpleGene with  “gatgctataat” should return the answer with lowercase letters, the gene “atgctataa”. 
    // HINT: there are two string methods toUpperCase() and toLowerCase(). If dna is the string “ATGTAA” then 
    // dna.toLowerCase() results in the string “atgtaa”.
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
            "ATGGGTTAAGTC",                       true,  // valid
            "gatgctataat",                        true   // valid
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

    // Write the method named twoOccurrences that has two String parameters named stringa and stringb. 
    // This method returns true if stringa appears at least twice in stringb, otherwise it returns false. 
    // For example, the call twoOccurrences(“by”, “A story by Abby Long”) returns true as there are two 
    // occurrences of “by”, the call twoOccurrences(“a”, “banana”) returns true as there are three occurrences 
    // of “a” so “a” occurs at least twice, and the call twoOccurrences(“atg”, “ctgtatgta”) returns false as 
    // there is only one occurence of “atg”.
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

    public void testTwoOccurences() {

        // samples for testing
        Map<Pair<String, String>, Boolean> samples = Map.of(
            new ImmutablePair<>("by", "A story by Abby Long"), true,
            new ImmutablePair<>("a", "banana"),                true,
            new ImmutablePair<>("atg", "ctgtatgta"),           false,
            new ImmutablePair<>("<", "fdfdf<sfdf5656<dd"),     true
        );

        // test by provided samples
        for (Map.Entry<Pair<String, String>, Boolean> entry : samples.entrySet()) {
            boolean result = twoOccurrences(entry.getKey().getLeft(), entry.getKey().getRight());

            System.out.println(
                String.format("Value [%s] occured in [%s] more than 2 times: [%s] -> [%s].",
                    entry.getKey().getLeft(), entry.getKey().getRight(), result, entry.getValue()));

        } // end of FOR

    }

    // Write the method named lastPart that has two String parameters named stringa and stringb. 
    // This method finds the first occurrence of stringa in stringb, and returns the part of stringb that follows 
    // stringa.  If stringa does not occur in stringb, then return stringb. For example, the call 
    // lastPart(“an”, “banana”) returns the string “ana”, the part of the string after the first “an”. 
    // The call lastPart(“zoo”, “forest”) returns the string “forest” since “zoo” does not appear in that word.
    public String lastPart(@NonNull String stringA, @NonNull String stringB) {

        // first occurence of A in B
        var index = stringB.toLowerCase().indexOf(stringA.toLowerCase());
        if (index == -1) {
            return stringB;
        }

        return stringB.substring(index + stringA.length());
    }

    public void testLastPart() {

        // samples for testing
        Map<Pair<String, String>, String> samples = Map.of(
            new ImmutablePair<>("an", "banana"), "ana",
            new ImmutablePair<>("zoo", "forest"), "forest",
            new ImmutablePair<>("<", "fdfdf<sfdf5656<dd"), "sfdf5656<dd"
        );

        // test by provided samples
        for (Map.Entry<Pair<String, String>, String> entry : samples.entrySet()) {
            String result = lastPart(entry.getKey().getLeft(), entry.getKey().getRight());

            System.out.println(
                String.format("The last part of string [%s] after string [%s] is [%s] -> [%s].",
                    entry.getKey().getRight(), entry.getKey().getLeft(), result, 
                    (entry.getValue().equals(result) ? "OK" : "Failed")));

        } // end of FOR
    }

    //
    public Collection<String> extractWebLinks(@NonNull IResource resource, @NonNull String searchTarget) {

        var webLinks = new ArrayList<String>();

        for (String word : resource.words()) {
            if (word.toLowerCase().contains(searchTarget.toLowerCase())) {
                String webLink = lastPart("\"", word);
                webLink = webLink.substring(0, webLink.lastIndexOf("\""));
                webLinks.add(webLink);
                // print web link
                // System.out.println("web link -> " + webLink);
            }
        }

        return webLinks;
    }

    //
    public Collection<String> readWebLinksFromUrl(@NonNull String url, @NonNull String searchTarget) {
        // read and process the URL resource
        return extractWebLinks(new URLResource(url), searchTarget);
    }

    //
    public Collection<String> readWebLinksFromFile(@NonNull String fileName, @NonNull String searchTarget) {
        // read and process the file resource
        return extractWebLinks(new FileResource(fileName), searchTarget);
    }

    //
    public void testReadWebLinks() {
        var resultFromUrl = readWebLinksFromUrl("https://www.dukelearntoprogram.com/course2/data/manylinks.html", 
            "youtube.com");

        var resultFromFile = readWebLinksFromFile("links/Computer Science Articles.html", 
            "youtube.com");

        // compare and print results 
        boolean areEqual = resultFromUrl.containsAll(resultFromFile) && 
        resultFromFile.containsAll(resultFromUrl);

        System.out.println(Arrays.toString(resultFromUrl.toArray()));
        System.out.println(Arrays.toString(resultFromFile.toArray()));

        System.out.println(String.format("Collections size match: [%s], collections match: [%s].",
            resultFromUrl.size() == resultFromFile.size(), areEqual));
    }

    public static void main(String[] args) throws IOException {
        log.info("Tag Finder application is starting...");

        var application = new TagFinderApplication(); // init the application class

        // test the first method
        System.out.println("\n\n");
        log.info("Testing: findFirstSimpleGene()...");
        application.testFindFirstSimpleGene();

        // test the second method
        System.out.println("\n\n");
        log.info("Testing: twoOccurences()...");
        application.testTwoOccurences();

        // test the third method
        System.out.println("\n\n");
        log.info("Testing: lastPart()...");
        application.testLastPart();

        // test the fourth method
        System.out.println("\n\n");
        log.info("Testing: readWebLinksFromFile() and readWebLinksFromUrl()...");
        application.testReadWebLinks();

        System.out.println("\n\n");
        log.info("Tag Finder application is done.");
    }

}
