package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Drone {

    private static final LongLat INITIAL_POSITION = new LongLat(-3.186874, 55.944494);
    private static final int INITIAL_MOVES = 1500;

    private final List<FlightPath> flightPaths = new ArrayList<>();
    private List<Order> orderList;
    private final DroneMap droneMap;
    public int availableMoves;
    private String currentOrderNo;
    private LongLat currentPosition;

    public Drone(Date date, DroneMap droneMap) throws SQLException {
        this.droneMap = droneMap;
        this.orderList = createOrders(date);
        this.currentPosition = INITIAL_POSITION;
        this.availableMoves = INITIAL_MOVES;
    }

    public void fly() {
        for (Order order : orderList) {
            currentOrderNo = order.orderNo;
            if (!canDeliverCurrentOrder(order)) {
                break;
            }
            for (String shop : order.shops) {
                move(What3Words.findCoordinates(shop));
            }
            move(What3Words.findCoordinates(order.deliverTo));
        }
        currentOrderNo = null;
        move(INITIAL_POSITION);
    }

    private void virtualFly() {
        for (Order order : orderList) {
            currentOrderNo = order.orderNo;
            for (String shop : order.shops) {
                move(What3Words.findCoordinates(shop));
            }
            move(What3Words.findCoordinates(order.deliverTo));
        }
        currentOrderNo = null;
        move(INITIAL_POSITION);
    }

    private boolean canDeliverCurrentOrder(Order order) {
        Drone virtualDrone = this;
        virtualDrone.orderList = new ArrayList<>();
        virtualDrone.orderList.add(order);
        System.out.println(virtualDrone.orderList);
        virtualDrone.virtualFly();
        return virtualDrone.availableMoves >= 0;
    }

    /**
     *
     * @param destination
     * @return The angle that needed to go towards the destination.
     */
    private int calculateAngle (LongLat destination) {
        int angle = (int)Math.toDegrees
                (Math.atan2((destination.latitude - currentPosition.latitude),
                        (destination.longitude - currentPosition.longitude)));

        if (angle < 0) {
            angle = Math.round((angle + 360) / 10) * 10;
        } else if (angle == 360) {
            angle = 0;
        } else {
            angle = Math.round(angle / 10) * 10;
        }
        return angle;
    }

    /**
     *
     * @param destination
     */
    private void moveOneStep (LongLat destination){
        int angle = calculateAngle(destination);
        LongLat nextPosition = currentPosition.nextPosition(angle);
        flightPaths.add(
                new FlightPath(currentOrderNo, currentPosition.longitude, currentPosition.latitude,
                        angle, nextPosition.longitude, nextPosition.latitude)
        );
        currentPosition = nextPosition;
        availableMoves--;
    }

    private void move (LongLat destination) {
        if (isAcrossNFZ(currentPosition, destination)) {
            LongLat landmark1 = droneMap.getLandMarks().get(0);
            LongLat landmark2 = droneMap.getLandMarks().get(1);

            if (isAcrossNFZ(currentPosition, landmark2) && isAcrossNFZ(landmark2, destination)
            || (!isAcrossNFZ(currentPosition, landmark1) && !isAcrossNFZ(landmark1, destination))
                    && landmark1IsShorter(destination)) {
                while (!currentPosition.closeTo(landmark1)) moveOneStep(landmark1);
            } else {
                while (!currentPosition.closeTo(landmark2)) moveOneStep(landmark2);
            }
        }
        while (!currentPosition.closeTo(destination)) moveOneStep(destination);
        hover();
    }

    private boolean landmark1IsShorter(LongLat destination) {
        LongLat landmark1 = droneMap.getLandMarks().get(0);
        LongLat landmark2 = droneMap.getLandMarks().get(1);
        double line1 = Math.sqrt(
                Math.pow((currentPosition.longitude - landmark1.longitude), 2) +
                Math.pow((currentPosition.latitude - landmark1.latitude), 2)
        );

        double line2 = Math.sqrt(
                Math.pow((destination.longitude - landmark1.longitude), 2) +
                Math.pow((destination.latitude - landmark1.latitude), 2)
        );

        double line3 = Math.sqrt(
                Math.pow((currentPosition.longitude - landmark2.longitude), 2) +
                Math.pow((currentPosition.latitude - landmark2.latitude), 2)
        );

        double line4 = Math.sqrt(
                Math.pow((destination.longitude - landmark2.longitude), 2) +
                Math.pow((destination.latitude - landmark2.latitude), 2)
        );

        return line1 + line2 < line3 + line4;
    }

    private void hover(){
        availableMoves--;
    }

    private static List<Order> createOrders (Date date) throws SQLException {
        List<Order> orderList = new ArrayList<>();
        WebConnection webConnection = new WebConnection("localhost","9898");
        List<String> orderNoList = Database.readOrder(date,"orderNo");
        List<String> deliveryToList = Database.readOrder(date,"deliverTo");
        for (int i = 0; i < orderNoList.size(); i++){
            List<String> item = Database.readOrderDetail(orderNoList.get(i),"Item");
            List<String> shops = new ArrayList<>();
            String[] itemsInArray = new String[item.size()];
            for(int j = 0; j < item.size(); j++) {
                shops.add(webConnection.findLocation(item.get(j)));
                itemsInArray[j] = item.get(j);
            }
            List<String> shopsNoDuplicate = new ArrayList<>();
            for(String shop : shops) {
                if (!shopsNoDuplicate.contains(shop)){
                    shopsNoDuplicate.add(shop);
                }
            }
            int cost = webConnection.getDeliveryCost(itemsInArray);
            Order singleOrder = new Order(orderNoList.get(i),item,shopsNoDuplicate,deliveryToList.get(i),cost);
            orderList.add(singleOrder);
        }
        return orderList;
    }

    /**
     *
     * @param source
     * @param destination
     * @return The boolean value of whether the path is across the no fly zone
     */
    private boolean isAcrossNFZ(LongLat source, LongLat destination) {
        Point2D point1 = new Point2D.Double(source.longitude, source.latitude);
        Point2D point2 = new Point2D.Double(destination.longitude,destination.latitude);
        Line2D linePath = new Line2D.Double(point1,point2);
        for (Line2D line : droneMap.getNoFlyZones()) {
            if (line.intersectsLine(linePath)) {
                return true;
            }
        }
        return false;
    }

    public void createMap(String textFile) throws FileNotFoundException {
        List<Point> longLats = flightPaths.stream()
                .map(x -> Point.fromLngLat(x.fromLongitude, x.fromLatitude))
                .collect(Collectors.toList());
        FlightPath lastPath = flightPaths.get(flightPaths.size() - 1);
        longLats.add(Point.fromLngLat(lastPath.toLongitude, lastPath.toLatitude));
        String finalFC = FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromLngLats(longLats))).toJson();
        PrintWriter output = new PrintWriter(textFile);
        output.println(finalFC);
        output.close();
    }

}
