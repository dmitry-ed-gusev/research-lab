package gusev.dmitry.research.books.java24h_trainer.lesson21.threads.skipy;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 01.11.12)
 */

public class DeadLockThreadsTest {

    public static void main(String[] args) {
        A a1 = new A();
        A a2 = new A();
        Thread t1 = new Thread(new Tester(a1, a2));
        Thread t2 = new Thread(new Tester(a2, a1));
        t1.start();
        t2.start();
    }

    public static class Tester implements Runnable {

        static int nextId = 1;

        private A obj1;
        private A obj2;
        private int id = 0;

        public Tester(A obj1, A obj2) {
            this.obj1 = obj1;
            this.obj2 = obj2;
            id = nextId++;
        }

        public void run() {
            print("Setting value to obj1... ");
            obj1.setValue(id);
            print("done.");
            print("Comparing objects... ");
            print("Done. Result: " + ((obj1.equals(obj2)) ? "equal" : "not equal"));
        }

        private void print(String msg) {
            System.out.println("Thread #" + id + ": " + msg);
        }
    }

    public static class A {

        private int value = 0;

        synchronized void setValue(int value) {
            this.value = value;
        }

        synchronized int getValue() {
            return value;
        }

        public synchronized boolean equals(Object o) {
            A a = (A) o;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println("Interrupted!");
            }
            return value == a.getValue();
        }
    }
}
