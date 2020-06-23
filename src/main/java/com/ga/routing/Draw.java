package com.ga.routing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class Draw extends Canvas{

    boolean hasTitle = false;

    Coordinates [] points;

    private int[] bestInd;

    private int noGenerations = 0;

    private int maxGenerations = 0;

    private long time = 0;

    public Draw(Coordinates [] points, int maxGenerations) {
        this.points = points;
        this.maxGenerations = maxGenerations;
    }

    public void setBestInd(int[] bestInd) {
        this.bestInd = bestInd;
        this.repaint();
    }

    public void setNoGenerations(int noGenerations) {
        this.noGenerations = noGenerations;
    }

    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private int computeLatZoom(double lat){
        int preLat = (int)((lat + 90)*1000);
        preLat = preLat - (17550*5) +100;
        return preLat;
    }

    private int computeLngZoom(double lng){
        int preLng = (int)((lng + 180)*1000);
        preLng = preLng - (19900*5) - 300;
        return preLng;
    }

    @Override
    public void paint(Graphics g) {
//        if(!hasTitle) {
//            hasTitle = true;
//            g.drawString("[Points: "+points.length+" ] ", 40, 40);
//        }

        for (int p = 0; p < points.length; p++) {
            int lng = computeLngZoom(points[p].lng);
            int lat = computeLatZoom(points[p].lat);
           // System.out.println("POINT:["+lng+"||"+lat+"]");
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

            Coordinates p1 = points[bestInd[0]];
            Coordinates d1 = points[bestInd[bestInd.length - 1]];

            g.setColor(Color.blue);
            g.fillRect(computeLngZoom(p1.lng), computeLatZoom(p1.lat), 5, 5);

            g.setColor(Color.green);
            g.fillRect(computeLngZoom(d1.lng), computeLatZoom(d1.lat), 5, 5);

        }
        if (bestInd !=null && this.noGenerations >= this.maxGenerations) {
            g.setColor(Color.black);
            //g.drawString("[GE: "+this.noGenerations+" / Time: "+this.time+" ms. ] ", 40, 60);
        }
    }
}
