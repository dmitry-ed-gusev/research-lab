package gusev.dmitry.research.i18n;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Research demo class for i18n - using Properties Resources Bundles.
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 25.08.2014)
 */

public class PropertiesDemo {

    static void displayValue(Locale currentLocale, String key) {

        ResourceBundle labels = ResourceBundle. getBundle("LabelsBundle", currentLocale);
        String value = labels.getString(key);
        System.out.println(
                "Locale = " + currentLocale.toString() + ", " +
                        "key = " + key + ", " +
                        "value = " + value);

    } // displayValue


    static void iterateKeys(Locale currentLocale) {

        ResourceBundle labels =
                ResourceBundle.getBundle("LabelsBundle", currentLocale);

        Enumeration bundleKeys = labels.getKeys();

        while (bundleKeys.hasMoreElements()) {
            String key = (String) bundleKeys.nextElement();
            String value = labels.getString(key);
            System.out.println("key = " + key + ", " +
                    "value = " + value);
        }

    } // iterateKeys


    static public void main(String[] args) {

        // show default locale
        System.out.println("default -> " + Locale.getDefault());
        // set local - forced
        Locale.setDefault(Locale.US);
        System.out.println("new default -> " + Locale.getDefault());

        Locale[] supportedLocales = {
                new Locale("ru"),
                Locale.GERMAN,
                Locale.ENGLISH
        };

        for (int i = 0; i < supportedLocales.length; i++) {
            displayValue(supportedLocales[i], "s2");
        }

        System.out.println();

        iterateKeys(supportedLocales[0]);

    } // main

} // class
