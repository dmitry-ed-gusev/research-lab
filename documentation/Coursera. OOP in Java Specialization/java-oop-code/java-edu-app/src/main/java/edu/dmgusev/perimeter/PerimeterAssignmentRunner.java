package edu.dmgusev.perimeter;

import java.io.File;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import edu.duke.DirectoryResource;
import edu.duke.FileResource;
import edu.duke.Point;
import edu.duke.Shape;

public class PerimeterAssignmentRunner {

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

    public int getNumPoints (Shape s) { // OK

        if (s != null) {
            return s.getNumPoints();
        }

        return 0;
    }

    public double getAverageLength(Shape s) { // OK

        if (s != null) {
            return this.getPerimeter(s) / this.getNumPoints(s);
        }

        return 0.0;
    }

    public double getLargestSide(Shape s) { // OK

        if (s != null) {
            double theLargestSide = 0.0;
            Point prevPt = s.getLastPoint();
            for (Point currPt : s.getPoints()) {
                double currDist = prevPt.distance(currPt);
                if (currDist > theLargestSide) {
                    theLargestSide = currDist;
                }
                prevPt = currPt;
            } // end of FOR

            return theLargestSide;
        }

        return 0.0;

    }

    public double getLargestX(Shape s) { // OK

        if (s != null) {
            int theLargestX = s.getLastPoint().getX();
            for (Point point : s.getPoints()) {
                if (point.getX() > theLargestX) {
                    theLargestX = point.getX();
                }
            } // end of FOR

            return theLargestX;
        }

        return 0.0;
    }

    public double getLargestPerimeterMultipleFiles() {
        // Put code here
        return 0.0;
    }

    public String getFileWithLargestPerimeter() {
        // Put code here
        File temp = null;    // replace this code
        return temp.getName();
    }

    public double testPerimeter (String relativeFileName) {

        // select file
        FileResource fr;
        if (StringUtils.trimToNull(relativeFileName) != null) {
            fr = new FileResource(relativeFileName);
        } else {
            fr = new FileResource();
        }

        // load the shape's data from file
        Shape s = new Shape(fr);

        // perform the calculations
        double length = this.getPerimeter(s);
        int pointsNum = this.getNumPoints(s);
        double avgLength = this.getAverageLength(s);
        double theLargestSide = this.getLargestSide(s);
        double theLargestX = this.getLargestX(s);

        // do calculations output
        System.out.println(String.format("file: [%s]; perimeter = [%s]; # of points = [%s]; " +
            "avg. length = [%s]; the largest side = [%s]; the largest X = [%s]",
            fr.getMyPath() , length, pointsNum, avgLength, theLargestSide, theLargestX));

        return length;
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

        // iterate over set of files and perform calculations for each
        double maxPerimeter = 0.0;
        String fileName = "";

        for (String dataFile : files) {
            try {
                double perimeter = this.testPerimeter(dataFile);
                if (perimeter > maxPerimeter) {
                    maxPerimeter = perimeter;
                    fileName = dataFile;
                }
            } catch (Exception e) {
                // just leave it empty - no message
                // e.printStackTrace(); // <- use it just for debug!
            } // end of TRY...CATCH

        } // end of FOR

        System.out.println(String.format("Max perimeter = [%s]; file = [%s].",
            maxPerimeter, fileName));
    }

    public void testPerimeterMultipleFiles() { // not needed
        // Put code here
    }

    public void testFileWithLargestPerimeter() { // not needed
        // Put code here
    }

    // This method creates a triangle that you can use to test your other methods
    public void triangle() {

        Shape triangle = new Shape();
        triangle.addPoint(new Point(0,0));
        triangle.addPoint(new Point(6,0));
        triangle.addPoint(new Point(3,6));

        for (Point p : triangle.getPoints()){
            System.out.println(p);
        }

        double peri = getPerimeter(triangle);
        System.out.println("perimeter = "+peri);
    }

    // This method prints names of all files in a chosen folder that you can use to test your other methods
    public void printFileNames() {

        DirectoryResource dr = new DirectoryResource();
        for (File f : dr.selectedFiles()) {
            System.out.println(f);
        }

    }

    public static void main (String[] args) {
        PerimeterAssignmentRunner pr = new PerimeterAssignmentRunner();
        pr.testPerimeterForAllData();
    }

}
