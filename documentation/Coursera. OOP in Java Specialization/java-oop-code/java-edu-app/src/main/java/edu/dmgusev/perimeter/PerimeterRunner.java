package edu.dmgusev.perimeter;

import java.io.IOException;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import edu.duke.FileResource;
import edu.duke.Point;
import edu.duke.Shape;

public class PerimeterRunner {

    public double getPerimeter (Shape s) {
        // Start with totalPerim = 0
        double totalPerim = 0.0;
        // Start wth prevPt = the last point 
        Point prevPt = s.getLastPoint();
        // For each point currPt in the shape,
        for (Point currPt : s.getPoints()) {
            // Find distance from prevPt point to currPt 
            double currDist = prevPt.distance(currPt);
            // Update totalPerim by currDist
            totalPerim = totalPerim + currDist;
            // Update prevPt to be currPt
            prevPt = currPt;
        }
        // totalPerim is the answer
        return totalPerim;
    }

    public void testPerimeter (String relativeFileName) {

        // select file
        FileResource fr;
        if (StringUtils.trimToNull(relativeFileName) != null) {
            fr = new FileResource(relativeFileName);
        } else {
            fr = new FileResource();
        }

        // load data and calculate
        Shape s = new Shape(fr);
        double length = getPerimeter(s);
        System.out.println(String.format("file: [%s]; perimeter = [%s]", fr.getMyPath() , length));

    }

    public void testPerimeter() {
        this.testPerimeter(null);
    }

    // perform tests for all resource/data files
    public void testPerimeterForAllData() {

        // build set of files
        var maxNumber = 10;
        var files = new TreeSet<String>();
        for (int i = 1; i <= maxNumber; i++) {
            files.add(String.format("perimeter/datatest%s.txt", i));
            files.add(String.format("perimeter/example%s.txt", i));
        }

        //
        for (String dataFile : files) {
            try {
                this.testPerimeter(dataFile);
            } catch (Exception e) {
                // just leave it empty - no message
                // e.printStackTrace(); // <- use it just for debug!
            }
        }

    }

    public static void main (String[] args) throws IOException {
        PerimeterRunner pr = new PerimeterRunner();
        // pr.testPerimeter();
        pr.testPerimeterForAllData();
    }

}
