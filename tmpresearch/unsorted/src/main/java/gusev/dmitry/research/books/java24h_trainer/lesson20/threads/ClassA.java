package gusev.dmitry.research.books.java24h_trainer.lesson20.threads;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 25.10.12)
 */

class ClassA {
    String marketNews = null;

    void someMethod() {
        // The ClassB needs a reference to the locked object to be able to notify it
        ClassB myB = new ClassB(this);
        myB.start();
        System.out.println("ClassB thread started.");
        synchronized (this) {
            try {
                System.out.println("Start waiting.");
                wait();
                System.out.println("Finished waiting.");
            } catch (InterruptedException e) {
                System.out.println("Interrupted! " + e.getMessage());
            }
        }
        System.out.println("Continue processing.");
        // Some further processing of the MarketData goes here...
    }

    public void setData(String news) {
        marketNews = news;
    }

    public static void main(String[] args) {

        ClassA classA = new ClassA();
        classA.someMethod();

    }
}

class ClassB extends Thread {

    ClassA parent = null;

    ClassB(ClassA caller) {
        parent = caller; // store the reference to the caller
    }

    public void run() {
        // Get some data, and, when done, notify the parent
        parent.setData("Economy is recovering...");

        for (int i = 0; i < 10; i++) {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                System.out.println("ClassB -> interrupted");
            }
            System.out.println("ClassB -> working");
        }

        synchronized (parent) {
            parent.notify(); //notification of the caller
        }
    }
}