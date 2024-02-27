package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.FeatureCollection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;



public class WebConnection {

    private static HttpClient client = HttpClient.newHttpClient();
    public String machineName;
    public String port;

    /**WebConnection constructor*/
    public WebConnection(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }



    //inner class
    public class Shop {

        public String name;
        public String location;
        public List<WebConnection.Shop.ItemDetail> menu;

        public class ItemDetail{
            public String item ;
            public String pence;
        }
    }
    //method
    /**
     * Calculate the cost of all items delivered by drone
     *
     * @param   food is the food need to be delivered
     * @return the total delivery cost
     */
    public int getDeliveryCost(String... food) {
        //calculate cost
        int penceCount = 50;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + this.machineName + ":" + this.port + "/menus/menus.json"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
            ArrayList<Shop> resList = new Gson().fromJson(responseJson, listType);
            for (Shop shop : resList) {      
                List<WebConnection.Shop.ItemDetail> foodList = shop.menu;
                for (WebConnection.Shop.ItemDetail itemDetail : foodList) {
                    for (String Name : food) {
                        if (Name.equals(itemDetail.item)){
                            penceCount = penceCount + Integer.parseInt(itemDetail.pence);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return penceCount;
    }

    /**
     *
     * @param threeWord
     * @return A URL string
     * Contained WhatThreeWords, used to find the coordinates of WhatThreeWords.
     */
    public String createURL(String threeWord){
        String[] words = threeWord.split("\\.");
        return ("http://" + machineName + ":" + port  + "/words/" + words[0] + "/" + words[1] + "/" + words[2] + "/details.json");
    }

    /**
     *
     * @param foodName
     * @return The WhatThreeWords location of the corresponding food.
     */
    public String findLocation(String foodName) {
        String threeWords = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + this.machineName + ":" + this.port + "/menus/menus.json"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            Type listType = new TypeToken<ArrayList<Shop>>() {
            }.getType();
            ArrayList<Shop> resList = new Gson().fromJson(responseJson, listType);
            for (Shop shop : resList) {
                List<WebConnection.Shop.ItemDetail> foodList = shop.menu;
                for (WebConnection.Shop.ItemDetail itemDetail : foodList) {
                    if (foodName.equals(itemDetail.item)) {
                        threeWords = shop.location;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return threeWords;
    }


}

