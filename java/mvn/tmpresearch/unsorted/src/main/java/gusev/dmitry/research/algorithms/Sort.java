package gusev.dmitry.research.algorithms;

import java.util.Arrays;

/**
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 03.11.2015)
 */

public class Sort {

    /**
     * Merge sort, recursive algorithm.
     * The worst time: O(n log n)
     * The best time:  O(n log n) <- usual, O(n) <- on sorted array
     * Average time:   O(n log n)
     * Memory: O(n) additional
     */
    public static int[] mergeSort(int[] array) {
        if (array == null) { // null-check (safety)
            return null;
        }

        if (array.length > 2) { // split and recursive call
            int splitLen = array.length / 2;
            int[] left  = new int[splitLen];
            int[] right = new int[array.length - splitLen];

            System.arraycopy(array, 0, left, 0, splitLen);                        // get left part
            System.arraycopy(array, splitLen, right, 0, array.length - splitLen); // get right part
            int[] sortedLeft  = Sort.mergeSort(left);  // recursive call
            int[] sortedRight = Sort.mergeSort(right); // recursive call

            // merge step
            int leftCounter  = 0;
            int rightCounter = 0;
            int[] result = new int[array.length];
            for (int i = 0; i < array.length; i++) { // fills in resulting array
                if (leftCounter < sortedLeft.length && rightCounter < sortedRight.length) {
                    if (sortedLeft[leftCounter] <= sortedRight[rightCounter]) {
                        result[i] = sortedLeft[leftCounter];
                        leftCounter++;
                    } else {
                        result[i] = sortedRight[rightCounter];
                        rightCounter++;
                    }
                } else if (leftCounter >= sortedLeft.length && rightCounter < sortedRight.length) {
                    result[i] = sortedRight[rightCounter];
                    rightCounter++;
                } else if (rightCounter >= sortedRight.length && leftCounter < sortedLeft.length) {
                    result[i] = sortedLeft[leftCounter];
                    leftCounter++;
                }
                //System.out.println("middle result -> " + Arrays.toString(result));
            }  // end of FOR (merge step)

            return result;

        } else if (array.length == 2) { // sort 2-elements array
            if (array[0] > array[1]) { // swap if needed
                int i = array[0];
                array[0] = array[1];
                array[1] = i;
            }
            return array;
        } else { // just return 1-element array
            return array;
        }

    } // end of merge sort method

    /**
     * Selection sort, non-recursive algorithm. Algorithm may be stable/not stable.
     * The worst time: О(n^2)
     * The best time:  О(n^2)
     * Average time:   О(n^2)
     * Memory: О(n) total, O(1) additional.
     */
    public static int[] selectionSort(int[] array) {
        if (array == null) { // null-check (safety)
            return null;
        }

        int tmp;
        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) { // iterate over rest of array and find min
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex > i) { // if found minimum - swap with first element
                tmp = array[minIndex];
                array[minIndex] = array[i];
                array[i] = tmp;
            }
        } // end of main FOR statement
        return array;
    } // end of selection sort method

    /**
     * Bubble sort, non-recorsive algorithm. Algorithm isn't stable.
     * The worst time: O(n^2)
     * The best time:  O(n)
     * Average time:   O(n^2)
     * Memory: O(1) additional.
     */
    public static int[] bubbleSort(int[] array) {
        if (array == null) { // null-check (safety)
            return null;
        }

        int tmp;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) { // swap if necessary
                    tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                }
            }
        } // end of main FOR loop

        return array;
    } // end of bubble sort method

    /**
     * Coctail sort, non-recursive algorithm. Other name: shaker sort.
     * This is an improved variety of bubble sort.
     * The worst time: O(n^2) - sorted in reverse order array
     * The best time:  O(n) - already sorted array
     * Average time:   O(n^2)
     * Memory: O(1) additional.
     */
    public static int[] coctailSort(int[] array) {
        if (array == null) { // null-check (safety)
            return null;
        }
        int left  = 0;                // left bound
        int right = array.length - 1; // right bound
        int tmp;
        do {
            for (int i = left; i < right; i++) { // shift "heavy" elements to end
                if(array[i] > array[i+1]) {
                    tmp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = tmp;
                }
            } // end of FOR - shift to end

            right--; // decrease right bound (shift it left)

            for (int i = right; i > left ; i--) { // shift "light" elements to begin
                if(array[i] < array[i-1]) {
                    tmp = array[i];
                    array[i] = array[i - 1];
                    array[i - 1] = tmp;
                }
            } // end of FOR - shift to begin

            left++; // increase left bound (shift it right)
        } while (left <= right);

        return array;
    }

    public static void main(String[] args) {
        int[] array = {34, 55, 5, 7, 8, 8, 8, 9, 90, 126, 6, 4323, 2};
        //int[] array = {1, 7, 2};
        System.out.println("input          -> " + Arrays.toString(array));

        System.out.println("merge sort     -> " +
                Arrays.toString(Sort.mergeSort(Arrays.copyOf(array, array.length))));

        System.out.println("selection sort -> " +
                Arrays.toString(Sort.selectionSort(Arrays.copyOf(array, array.length))));

        System.out.println("bubble sort    -> " +
                Arrays.toString(Sort.bubbleSort(Arrays.copyOf(array, array.length))));

        System.out.println("coctail sort   -> " +
                        Arrays.toString(Sort.coctailSort(Arrays.copyOf(array, array.length))));
    }

}