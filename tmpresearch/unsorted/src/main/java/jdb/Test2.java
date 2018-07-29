package jdb;

class a {
    int x = 1;

    public void printX() {
        System.out.println(getX());
    }

    public int getX() {
        return x;
    }
}

class b extends a {
    int x = 2;

    public int getX() {
        return x + 1;
    }
}

public class Test2 {
    public static void main(String[] args) {
        a classA = new b();
        classA.printX();
    }
}