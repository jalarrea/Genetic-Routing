package com.ga.routing;

public class Coordinates {
    double lat = 0d;
    double lng = 0d;

    public Coordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getKey(){
        return "[lat:" + this.lat + ",lng:" + this.lng+"]";
    }

    public int getEuclideanDistance(Coordinates c2) {
        /*System.out.println("EUCLIDEAN DISTANCE FALLBACK : "
                + "("+c1.lat+","+c1.lng+") to "
                + "("+c2.lat+","+c2.lng+")");

         */
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(c2.lat - this.lat);
        double lonDistance = Math.toRadians(c2.lng - this.lng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(c2.lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double finalDistance = distance; //In meters
       /*
        System.out.println("EUCLIDEAN Distance:"+finalDistance);
        */
        return (int)finalDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinates co = (Coordinates) o;
        return this.lat == co.lat &&
                this.lng == co.lng;
    }

    public String getMasterKey(Coordinates c) {
        return "["+this.getKey()+"|"+c.getKey()+"]";
    }

}
