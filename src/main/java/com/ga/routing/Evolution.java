package com.ga.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Evolution {
    /**
     * Static configuration
     */
    private static int POPULATION_SIZE = 30;
    private static double CROSSOVER_PROBABILITY = 0.9;
    private static double MUTATION_PROBABILITY  = 0.01;
    /**
     * ======================================
     */

    private Coordinates [] points;

    private int maxGenerations = 10;

    private boolean isReturnToDepot = false;

    private HashMap<String, Integer> distances = new HashMap<String, Integer>();

    private ArrayList<int[]> population = new ArrayList<int[]>();

    private int [] values;

    private double [] fitnessValues;

    private double [] roulette;

    private int bestValue = -1;

    private int[] bestInd;
    /**
     * This counter is used to control the generations that we are going to create.
     */
    private int noGenerations = 1;

    private int noMutations = 0;

    private int UNCHANGED_GENS;


    public Evolution(Coordinates [] points, int generations, boolean isReturnToDepot) {
        this.points = points;
        this.maxGenerations = generations;
        this.isReturnToDepot = isReturnToDepot;
    }

    public Evolution(Coordinates [] points, int generations) {
        this.points = points;
        this.maxGenerations = generations;
    }

    public Evolution(Coordinates [] points){
        this.points = points;
    }

    /**
     * This is a init function.
     */
    public void run() {
        init();
        for (int i = 0; i < this.maxGenerations; i++) {
            this.nextGeneration();
        }
    }

    private void init() {

        this.values = new int[POPULATION_SIZE];
        this.fitnessValues = new double[POPULATION_SIZE];
        this.roulette = new double[POPULATION_SIZE];
        this.UNCHANGED_GENS = 0;

        /**
         * Step 1. Computing the distance matrix.
         */
        generateDistanceMatrix();
        /**
         * Step 2. Generate the first population.
         */
        for(int i = 0; i < POPULATION_SIZE; i++) {
            population.add(randomInd(points.length));
        }
        /**
         * Step 3. Set a first best value from this population.
         */
        setBestValue();
    }

    public void nextGeneration() {
        noGenerations++;
        selection();
        crossover();
        mutation();
        setBestValue();
    }

    /**
     * This is the "Natural" selection function.
     * First of all, we are going to do a list of Genetic Operations with the best canditate,
     * to try to create a better canditates for the next generation:
     *  1.-  (Last Best Ind) We include the best ind of the last generation.
     *  2.-  (Swapping) We create a mutated candidate, using "Swapping" operation to reorganize the genome.
     *  3.-  (Crossover) We create a mutated candidate, using "Crossover" operation, we split the best candidate to reorganize their genome.
     *  4.-  (Random) We select the rest of the candidates, using a "Random" operation.
     */
    private void selection() {
        ArrayList<int[]> parents = new ArrayList<int[]>();
        parents.add(bestInd);
        printInd(bestInd);
        int[] swapInd = swapInd(bestInd);
        printInd(swapInd);
        parents.add(swapInd);
        int[]crossInd = crossoverInd(bestInd);
        printInd(crossInd);
        parents.add(crossInd);
        setRoulette();
        for(int i = 3; i < POPULATION_SIZE; i++) {
            parents.add(population.get(wheelOut(Math.random())));
        }

        population = parents;
    }

    private void setRoulette() {
        /**
         * Computing all the fitness
         */

        for (int i = 0; i< values.length; i++) {
            this.fitnessValues[i] = 1.0/values[i];
        }
        /**
         * Set the roulette
         */
        double sum = 0;
        for(int i = 0; i < fitnessValues.length; i++) {
            sum += fitnessValues[i];
        }
        for (int i = 0; i < roulette.length; i++) { roulette[i] = fitnessValues[i]/sum; }
        for (int j = 1; j < roulette.length; j++) { roulette[j] += roulette[j-1]; }
    }

    private int wheelOut(double rand ) {
        int index = 0;
        for(index = 0; index < this.roulette.length; index++) {
            if( rand <= roulette[index] ) {
                break;
            }
        }
        return index;
    }

    /**
     * We swap the element of the ind to get a possible better candidate.
     * @param seq
     * @return
     */

    private int[] swapInd(int [] seq) {
        this.noMutations++;

        int[] res = Arrays.copyOfRange(seq, 0, seq.length);
        printInd(res);
        int ind1 = Utils.randomNumber(seq.length - 2);
        int ind2 = Utils.randomNumber(seq.length);
        while (ind1 >= ind2) {
            ind1 = Utils.randomNumber(seq.length - 2);
            ind2 = Utils.randomNumber(seq.length);
        }

        for (int i=0, j = (ind2-ind1+1)>> 1; i < j; i++) {
            int index1 = ind1+i;
            int index2 = ind2-i;

            index1 = index1 >= 0 ? index1 : 0;
            index1 = index1 < res.length ? index1 : res.length-1;
            index2 = index2 >= 0 ? index2 : 0;
            index2 = index2 < res.length ? index2 : res.length-1;

            int tmp = res[index1];
            res[index1] = res[index2];
            res[index2] = tmp;
        }
        return res;
    }

    /**
     * We do a crossover operation to create a possible better candidate.
     * @param seq
     * @return
     */
    private int[] crossoverInd(int [] seq) {
        this.noMutations++;

        int ind1 = Utils.randomNumber(seq.length - 2);
        int ind2 = Utils.randomNumber(seq.length);
        while (ind1 >= ind2) {
            ind1 = Utils.randomNumber(seq.length - 2);
            ind2 = Utils.randomNumber(seq.length);
        }

        int index1 = ind1;
        int index2 = ind2;
        index1 = index1 >= 0 ? index1 : 0;
        index1 = index1 < seq.length ? index1 : seq.length-1;

        index2 = index2 >= 0 ? index2 : 0;
        index2 = index2 < seq.length ? index2 : seq.length-1;

        int[] s1 = Arrays.copyOfRange(seq, 0, index2);

        int index3 = index2 + index1;
        index3 = index3 < seq.length ? index3 : seq.length-1;

        int[] s2 = Arrays.copyOfRange(seq, index2, index3);

        int[] s3 = Arrays.copyOfRange(seq, index3, seq.length);

        int [] res = new int[seq.length];
        /**
         * Joing all the chunks to create a new Ind.
         */
        System.arraycopy( s2, 0, res, 0, s2.length);
        System.arraycopy( s1, 0, res, s2.length, s1.length );
        System.arraycopy( s3, 0, res, s2.length + s1.length, s3.length);

        return res;
    }

    private void crossover() {
        ArrayList<Integer> queue = new ArrayList<Integer>();
        for(int i = 0; i < POPULATION_SIZE; i++) {
            if( Math.random() < CROSSOVER_PROBABILITY ) {
                queue.add(i);
            }
        }

        Collections.shuffle(queue);

        for (int i = 0, j = queue.size()-1; i < j; i+=2) {
            doCrossover(queue.get(i), queue.get(i+1));
        }
    }

    private void doCrossover(int x, int y) {
        /**
         * Forward
         */
        int [] child1 = getChild(1, x, y);
        /**
         * Backward
         */
        int [] child2 = getChild(-1, x, y);
        population.remove(x);
        population.add(x, child1);
        population.remove(y);
        population.add(x, child2);
    }

    private int [] getChild(int f, int x, int y) {

        int [] px = population.get(x);
        int [] py = population.get(y);
        int [] solution = new int[px.length];

        int index = Utils.randomNumber(px.length);
        index = index >= 0 ? index : 0;
        index = index < px.length ? index : px.length-1;

        int c = px[index];
        solution[0] = c;
        int j = 0;
        while (px.length > 1) {
            int dx = f >= 0? Utils.getNext(px, Utils.indexOf(px, c)): Utils.getPrevious(px, Utils.indexOf(px, c));
            int dy = f >= 0? Utils.getNext(py, Utils.indexOf(py, c)): Utils.getPrevious(py, Utils.indexOf(py, c));

            px = Utils.removeElem(px, c);
            printInd(px);
            py = Utils.removeElem(py, c);
            printInd(py);

            c = distances.get(getMasterKey(points[c], points[dx])) < distances.get(getMasterKey(points[c], points[dy]))
                    ? dx : dy;
            j++;
            solution[j] = c;
        }
        return solution;
    }

    private void mutation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if(Math.random() < MUTATION_PROBABILITY) {
                if(Math.random() > 0.5) {
                    int [] tmpInd = population.get(i);
                    this.population.remove(i);
                    this.population.add(i, crossoverInd(tmpInd));
                } else {
                    int [] tmpInd = population.get(i);
                    this.population.remove(i);
                    this.population.add(i, swapInd(tmpInd));
                }
            }
        }
    }

    private void setBestValue() {
        for (int i = 0; i < this.population.size(); i++) {
            values[i] = evaluate(this.population.get(i));
        }

        int bestIndex = getCurrentBest();
//        System.out.println("Best index:"+bestIndex);
//        System.out.println("bestValue:"+bestValue);
//        System.out.println("values:"+values[bestIndex]);
        if (bestValue == -1 || bestValue > values[bestIndex]) {
            bestInd = population.get(bestIndex);
            bestValue = values[bestIndex];
            UNCHANGED_GENS = 0;
            printBest();
            return;
        }
        UNCHANGED_GENS += 1;
    }

    private void printBest(){
       System.out.println("[Ge "+noGenerations+"] Best distance:"+this.bestValue+" mts");
    }

    private void printInd(int [] ind) {
        //System.out.print("IND");
        //System.out.print("[");
        for (int k =0; k < ind.length; k++) {
            //System.out.print(ind[k]+",");
        }
        //System.out.print("]");
        //System.out.println("");
    }

    private int getCurrentBest() {
        int bestIndex = 0;
        double currentBestValue = values[0];

        for (int i = 1; i < population.size(); i++) {
            if(values[i] < currentBestValue) {
                currentBestValue = values[i];
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    /**
     * This is a key function that determinate the valuation of this individual.
     * You can mutate this function to get a different focus of the route.
     * Right now, the focus is creating a route and also return to depot with the best path.
     * @param ind
     * @return sum
     */
    private int evaluate(int[] ind) {

        int sum = this.isReturnToDepot
                ? distances.get(getMasterKey(points[ind[0]], points[ind[ind.length - 1]]))
                :0;
        for(int i = 1; i < ind.length; i++) {
            sum += distances.get(getMasterKey(points[ind[i-1]], points[ind[i]]));
        }
        return sum;
    }

    private int[] randomInd(int n) {
        int[] in = new int[n];
        for (int i = 0; i < n; i++) {
            in[i]= i;
        }
        Utils.shuffleArray(in);
        return in;
    }

    private String getMasterKey(Coordinates c1, Coordinates c2) {
        return "["+c1.getKey()+"|"+c2.getKey()+"]";
    }

    /**
     * This method generate a matrix of distance [ 2x2] based on coordinates.
     */
    private void generateDistanceMatrix() {
        int length = points.length;
        /**
         * Number of threads to compute the Matrix.
         */
        int totalExecutors = 150;

        if (length < 30) {
            totalExecutors = 30;
        }

        if (length < 75 ) {
            totalExecutors = 100;
        }

        ExecutorService executor = Executors.newFixedThreadPool(totalExecutors);
        /**
         * Semaphore to access matrixBuilder object inside threads
         */
        Semaphore sem = new Semaphore(1);
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++) {

                Coordinates c1 = this.points[i];
                Coordinates c2 = this.points[j];
                Runnable worker = new EuclideanDistance(c1, c2, sem, distances);
                executor.execute(worker);
            }
        }
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("=============End from the calculating distances and times==========");
        } catch (InterruptedException e) {
            System.out.println("=============End from the calculating distances and times with errors==========");
            e.printStackTrace();
        }
    }

    public static class EuclideanDistance implements Runnable {
        private Coordinates c1;
        private Coordinates c2;
        private Semaphore sem;
        private HashMap<String, Integer> distances;

        public EuclideanDistance(Coordinates c1, Coordinates c2, Semaphore sem, HashMap<String, Integer> distances) {
            this.c1 = c1;
            this.c2 = c2;
            this.sem = sem;
            this.distances = distances;
        }

        public void run() {
            try {
                /**
                 * First of all, we need to check if it's already in the matrix
                 */
                if (distances.containsKey(c1.getMasterKey(c2))) {
                    return;
                }
                /**
                 * This is the same point, then distance is zero.
                 */
                if (c1.equals(c2)) {

                    this.sem.acquire();
                    this.distances.put(c1.getMasterKey(c2), 0);
                    this.sem.release();
                    return;
                }

                /**
                 * Computing the distance between c1 and c2.
                 */

                this.sem.acquire();
                this.distances.put(c1.getMasterKey(c2), c1.getEuclideanDistance(c2));
                this.sem.release();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
