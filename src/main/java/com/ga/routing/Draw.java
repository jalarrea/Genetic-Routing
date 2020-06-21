package com.ga.routing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class Draw extends Canvas{
    Coordinates [] points;
    private int[] bestInd;

    public Draw(Coordinates [] points) {
        this.points = points;
    }

    public void setBestInd(int[] bestInd) {
        this.bestInd = bestInd;
        this.repaint();
    }

    private int computeLatZoom(double lat){
        int preLat = (int)((lat + 90)*1000);
        preLat = preLat - (17550*5) + 100;
        return preLat;
    }

    private int computeLngZoom(double lng){
        int preLng = (int)((lng + 180)*1000);
        preLng = preLng - (19900*5) - 400;
        return preLng;
    }

    @Override
    public void paint(Graphics g) {
        for (int p = 0; p < points.length; p++) {
            int lng = computeLngZoom(points[p].lng);
            int lat = computeLatZoom(points[p].lat);
            System.out.println("POINT:["+lng+"||"+lat+"]");
            g.setColor(Color.red);
            g.fillRect(lng,lat, 3, 3);
        }
        if (this.bestInd != null){
            for (int k = 0; k < this.bestInd.length-1; k++) {
                Coordinates p1 = points[bestInd[k]];
                Coordinates p2 = points[bestInd[k+1]];
                g.setColor(Color.black);
                g.drawLine(computeLngZoom(p1.lng), computeLatZoom(p1.lat), computeLngZoom(p2.lng), computeLatZoom(p2.lat));
            }
        }
    }
}
