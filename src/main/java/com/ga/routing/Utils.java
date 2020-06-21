package com.ga.routing;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    /**
     * @param ar
     */
    public static void shuffleArray(int[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static int randomNumber(int boundary) {
        return (int)(Math.random() * (boundary + 1));
    }

    public static int getNext(int[] a, int index){
        if(index == a.length-1) {
            return a[0];
        } else {
            return a[index+1];
        }
    }

    public static int indexOf(int[] a, int value) {
        int index = -1;
        for (int i = 0; i < a.length; i++) {
            if(a[i] == value) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static int getPrevious(int[] a, int index){
        if(index == 0) {
            return a[a.length-1];
        } else {
            return a[index-1];
        }
    }

    public static int[] removeElem(int[] a, int n) {
        int [] b = new int[a.length-1];
        for (int i = 0; i < a.length; i++) {
            if(a[i] == n){
                // shifting elements
                for (int j = i; j < a.length - 1; j++) {
                    b[j] = a[j+1];
                }
                break;
            }
            b[i] = a[i];
        }
        return b;
    }
}
