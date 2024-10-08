package edu.dmgusev.finders.letters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ABCFinderApplication {


    public void findAbc(String input){
        int index = input.indexOf("abc");

        while (true){
            if (index == -1 || index >= input.length() - 3){
                break;
            }

            //System.out.println("index before -> " + index);
            String found = input.substring(index+1, index+4);
            System.out.println(found);
            index = input.indexOf("abc",index+3);
            //System.out.println("index after -> " + index);
        }

    }
 
    public void test() {
        //this.findAbc("abcd");
        //this.findAbc("abcdabc");
        //this.findAbc("abcdkfjsksioehgjfhsdjfhksdfhuwabcabcajfieowj");
        this.findAbc("abcabcabcabca");
    }

    public static void main(String[] args) {

        var application = new ABCFinderApplication();
        application.test();
    }

}