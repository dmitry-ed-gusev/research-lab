package gusev.dmitry.jtils;

import gusev.dmitry.jtils.calc.Calculator;
import gusev.dmitry.jtils.downloadManager.DownloadManager;
import gusev.dmitry.jtils.watch.PresentationWatch;

/**
 * JTils main class - for starting some useful applications from this library.
 * @author Gusev Dmitry (Чебурашка)
 * @version 1.0 (DATE: 21.12.2014)
*/

public class Main {

    private static void showUsageScreen() {
        System.out.println("JTILS library. Useful classes and applications. Created by Gusev Dmitry, 2014.");
        System.out.println("Usage: java -jar jtils [option]");
        System.out.println("Supported options (use one of options at a time):");
        for (Options option : Options.values()) {
            System.out.printf("%-" + (Options.getMaxOptionLength() + 2) + "s %s\n",
                    option.getOptionName(), option.getDescription());
        }
    }

    /***/
    public static void main(String[] args) {

        if (args != null && args.length == 1) {
            if (Options.CALC.getOptionName().equals(args[0])) { // starting calculator
                new Calculator();
            } else if (Options.WATCH.getOptionName().equals(args[0])) { // starting presentation timer
                PresentationWatch.startWatch();
            } else if (Options.DLMANAGER.getOptionName().equals(args[0])) { // download manager application
                DownloadManager.start();
            } else {
                Main.showUsageScreen();
            }
        } else { // no options or options count > 1
            Main.showUsageScreen();
        }

    }

}