package edu.dmgusev.hello;

import edu.duke.*;

public class HelloWorld {

    public void runHello () {
        FileResource res = new FileResource("hello/hello_unicode.txt");
        for (String line : res.lines()) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        new HelloWorld().runHello();
    }

}
