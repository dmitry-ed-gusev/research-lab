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

    public int getNumPoints (Shape s) {

        if (s != null) {
            return s.getNumPoints();
        }

        return 0;
    }

    public double getAverageLength(Shape s) {
        // Put code here
        return 0.0;
    }

    public double getLargestSide(Shape s) {
        // Put code here
        return 0.0;
    }

    public double getLargestX(Shape s) {
        // Put code here
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
        int pointsNum = s.getNumPoints();

        System.out.println(String.format("file: [%s]; perimeter = [%s]; points = [%s]", 
            fr.getMyPath() , length, pointsNum));

    }
    
    public void testPerimeter () {
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

    public void testPerimeterMultipleFiles() {
        // Put code here
    }

    public void testFileWithLargestPerimeter() {
        // Put code here
    }

    // This method creates a triangle that you can use to test your other methods
    public void triangle(){
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
        // pr.testPerimeter();

        pr.testPerimeterForAllData();
    }

}
