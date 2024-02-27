package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NoFlyZone {
    public static Feature feature;
  //  private Object NoFlyZone;
   // private static HttpClient client = HttpClient.newHttpClient();

    public static class feature {
        public String type;
        public Properties properties;
        public static class properties {
            public String name;
            public String fill;

        }
        public Geometry geometry;
        public static class geometry {
            public String type;
            public ArrayList<LongLat> coordinates;
        }
    }
}
