package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    /**
     * judge whether the drone is in confined area
     *
     * @param
     * @return true or false
     */
    public boolean isConfined() {
        return -3.192473 < longitude && longitude < -3.184319 && 55.942617 < latitude && latitude < 55.946233;
    }

    /**
     * Calculate the distance between two points
     *
     * @param   p
     * @return the distance
     */
    public double distanceTo(LongLat p) {
        double distance;
        distance = Math.sqrt(Math.pow(p.longitude - longitude, 2) + Math.pow(p.latitude - latitude, 2));
        return distance;
    }

    /**
     * judge whether the points are close to each other
     *
     * @param   p
     * @return true or false
     */
    public boolean closeTo(LongLat p) {
        return distanceTo(p) < 0.00015;
    }

    /**
     * Calculate the position when drone move
     *
     * @param   angle of movement
     * @return the next position
     */
    public LongLat nextPosition(int angle) {
        LongLat nextLongLat = new LongLat(longitude, latitude);
        if (angle < 0 || angle > 350) {
            System.out.println("Error! invalid angle");
        } else if (angle == 999) {   //the co-ordinates not change if hovered!
            return nextLongLat;
        } else {
            nextLongLat.longitude = longitude + 0.00015 * (Math.cos(Math.toRadians(angle)));
            nextLongLat.latitude = latitude + 0.00015 * (Math.sin(Math.toRadians(angle)));
        }
        return nextLongLat;
    }

}
