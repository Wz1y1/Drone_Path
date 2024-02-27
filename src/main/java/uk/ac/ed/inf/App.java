package uk.ac.ed.inf;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        DroneMap droneMap = new DroneMap();
        Drone drone = new Drone(Date.valueOf("2023-11-11"), droneMap);
        drone.fly();
        System.out.println(drone.availableMoves);
        drone.createMap("2023-11-11.geojson");
    }
}

