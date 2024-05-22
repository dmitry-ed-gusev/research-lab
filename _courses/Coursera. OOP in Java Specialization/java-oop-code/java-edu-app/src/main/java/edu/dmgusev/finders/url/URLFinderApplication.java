package edu.dmgusev.finders.url;

/**
 * Prints out all links within the HTML source of a web page.
 */
import edu.duke.StorageResource;
import edu.duke.URLResource;

public class URLFinderApplication {
    public StorageResource findURLs(String url) {
        URLResource page = new URLResource(url);
        String source = page.asString();
        StorageResource store = new StorageResource();
        int start = 0;
        while (true) {
            int index = source.indexOf("href=", start);
            if (index == -1) {
                break;
            }
            int firstQuote = index + 6; // after href="
            int endQuote = source.indexOf("\"", firstQuote);
            String sub = source.substring(firstQuote, endQuote);
            if (sub.startsWith("http")) {
                store.add(sub);
            }
            start = endQuote + 1;
        }
        return store;
    }

    public void testURL() {
        StorageResource s1 = findURLs("https://www.whitehouse.gov");
        StorageResource s2 = findURLs("http://www.doctorswithoutborders.org");
        for (String link : s2.data()) {
            System.out.println(link);
        }
        System.out.println("size = " + s1.size());
        System.out.println("size = " + s2.size());
    }
}
