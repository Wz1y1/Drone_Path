package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class DroneMap {
    private static final HttpClient client = HttpClient.newHttpClient();

    private final List<Line2D> noFlyZones;
    private final List<LongLat> landMarks;

    public DroneMap() throws IOException, InterruptedException {
        this.landMarks = readLandMarks();
        this.noFlyZones = readNoFlyZones();
    }

    public List<LongLat> getLandMarks() {
        return landMarks;
    }

    public List<Line2D> getNoFlyZones() {
        return noFlyZones;
    }

    private List<Line2D> readNoFlyZones() throws IOException, InterruptedException {
        List<Line2D> lines = new ArrayList<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + "localhost" + ":" + "9898" + "/buildings/no-fly-zones.geojson"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseGeoJson = response.body();
        List<Feature> buildings = FeatureCollection.fromJson(responseGeoJson).features();

        assert buildings != null;
        for (Feature building : buildings) {
            Polygon polygon = (Polygon) building.geometry();
            assert polygon != null;
            for (int i = 0; i < polygon.coordinates().get(0).size() - 1; i++) {
                Point2D point1 = new Point2D.Double(
                        polygon.coordinates().get(0).get(i).longitude(),
                        polygon.coordinates().get(0).get(i).latitude()
                );
                Point2D point2 = new Point2D.Double(
                        polygon.coordinates().get(0).get(i + 1).longitude(),
                        polygon.coordinates().get(0).get(i + 1).latitude()
                );
                lines.add(new Line2D.Double(point1,point2));
            }
            Point2D pointHead = new Point2D.Double(
                    polygon.coordinates().get(0).get(0).longitude(),
                    polygon.coordinates().get(0).get(0).latitude()
            );
            Point2D pointTail = new Point2D.Double(
                    polygon.coordinates().get(0).get(polygon.coordinates().get(0).size() - 1).longitude(),
                    polygon.coordinates().get(0).get(polygon.coordinates().get(0).size() - 1).latitude()
            );
            lines.add(new Line2D.Double(pointHead, pointTail));
        }
        return lines;
    }

    private List<LongLat> readLandMarks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + "localhost" + ":" + "9898" + "/buildings/landmarks.geojson"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseGeoJson = response.body();
        List<Feature> features = FeatureCollection.fromJson(responseGeoJson).features();

        List<LongLat> longLats = new ArrayList<>();
        assert features != null;
        for(Feature f : features){
            Point point = (Point) f.geometry();
            assert point != null;
            double longitude = point.coordinates().get(0);
            double latitude = point.coordinates().get(1);
            LongLat longLat = new LongLat(longitude,latitude);
            longLats.add(longLat);
        }

        return longLats;
    }


//    public List<List<LongLat>> readNoFlyZone () throws IOException, InterruptedException {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://" + "localhost" + ":" + "9898" + "/buildings/no-fly-zones.geojson"))
//                .build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        String responseGeoJson = response.body();
//        noFlyZone = FeatureCollection.fromJson(responseGeoJson);
//        List<Feature> features = noFlyZone.features();
//        assert features != null;
//        List<List<LongLat>> allBuilding = new ArrayList<>();
//        for (int i = 0; i < features.size(); i++) {
//            List<LongLat> oneBuilding = new ArrayList<>();
//            Feature f = features.get(i);
//            Polygon polygon = (Polygon) f.geometry();
//            assert polygon != null;
//            for (List<Point> pointList : polygon.coordinates()) {
//                for (Point point : pointList) {
//                    double longitude = point.coordinates().get(0);
//                    double latitude = point.coordinates().get(1);
//                    LongLat longLat = new LongLat(longitude, latitude);
//                    oneBuilding.add(longLat);
//                }
//            }
//            allBuilding.add(oneBuilding);
//        }
//        return allBuilding;
//    }
}
