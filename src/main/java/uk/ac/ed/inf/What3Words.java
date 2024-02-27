package uk.ac.ed.inf;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class What3Words {
    public static class Words {
        public coordinates coordinates;
        public static class coordinates {
            public double lng;
            public double lat;
            }
        }

    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     *
     * @param threeWord
     * @return A LongLat object(longitude,latitude)
     */
    public static LongLat findCoordinates(String threeWord) {
        WebConnection webConnection = new WebConnection("localhost", "9898");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webConnection.createURL(threeWord)))
                .build();
        LongLat coordinate = new LongLat(0, 0);
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            Words words = new Gson().fromJson(responseJson, Words.class);
            coordinate.latitude = words.coordinates.lat;
            coordinate.longitude = words.coordinates.lng;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return coordinate;
    }
}
