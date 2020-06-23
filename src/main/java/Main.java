import com.ga.routing.Draw;
import com.ga.routing.Evolution;
import com.ga.routing.Coordinates;

import java.util.Random;

public class Main {
    public static void main(String[] args){

        System.out.println("Faster Routes");

        /**
         * Genereation of points to test!.
         */
        int values = 100;
        Coordinates [] points = new Coordinates[values];
        for (int i = 0; i < values; i++) {
            points[i] = Main.getLocation(-79.908075, -2.1602453, 18000);
        }

        int generations = 300;
        long startTime = System.currentTimeMillis();
        new Evolution(points, generations)
        .run();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("Total Time:"+totalTime+" ms");
    }

    public static Coordinates getLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;

        Coordinates c1 = new Coordinates(foundLatitude, foundLongitude);

        //System.out.println("Longitude: " + foundLongitude + "  Latitude: " + foundLatitude );

        return c1;
    }
}

