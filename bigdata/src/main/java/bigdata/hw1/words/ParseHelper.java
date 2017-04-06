package bigdata.hw1.words;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.io.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for useful parsing methods.
 * Created by gusevdm on 11/28/2016.
 */

public final class ParseHelper {

    private ParseHelper() {}

    private static final String PUNCTUATION_REGEX = "[.,:;!?%$#@*+\\-<>'\"]";

    /***/
    public static Pair<Integer, List<Text>> parseDataRow(String dataRow) {

        // processing input row
        int maxLength = Integer.MIN_VALUE;
        List<Text> words = new ArrayList<>();

        if (!StringUtils.isEmpty(dataRow)) { // process if not empty
            String tmpWord;
            for (String word : StringUtils.split(dataRow)) {
                tmpWord = word.replaceAll(PUNCTUATION_REGEX, ""); // remove all punctuation

                if (tmpWord.length() > maxLength) { // <- new max length value
                    maxLength = tmpWord.length(); // get new length value
                    words = new ArrayList<>();    // reset words with max length list
                    words.add(new Text(tmpWord)); // add new longest word to list
                } else if (tmpWord.length() == maxLength) { // <- word with same length
                    words.add(new Text(tmpWord));
                }
            } // end of FOR
        }

        return new ImmutablePair<>(maxLength, words);
    }

}
