package com.example.frankstudythird.Protocols;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiService {
    public static JSONObject GetStreamData(String meetingId){
        JSONObject streamsInfo = new JSONObject();
        String ApiEntry = "GetStreamData";
        String targetURL = BasicSettings.urlBaseParlvuStream + ApiEntry + "?id=" + meetingId;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("ApiService.StreamData",response.toString());
                streamsInfo.put("Streams",new JSONArray(response.toString()));
            }else{
                Log.e("ApiService.StreamData","HttpCode:"+responseCode+";Error:"+connection.getResponseMessage());
            }
            streamsInfo.put("responseCode",responseCode);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return streamsInfo;
    }
    public static JSONObject GetEventList(String fromDate, String endDate){
        JSONObject eventList = new JSONObject();
        String ApiEntry = "GetEventList";
        String targetURL = BasicSettings.urlBaseParlvuAPI + ApiEntry + "?maxCount=-1";
        targetURL += (fromDate != null && fromDate != "") ? "&fromDate=" + fromDate : "";
        targetURL += (fromDate != null && endDate != "") ? "&endDate=" + endDate : "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("ApiService.EventList",response.toString());
                eventList = new JSONObject(response.toString());
            }
            eventList.put("responseCode",responseCode);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventList;
    }
}
