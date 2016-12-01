package se.mathenri.bandapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MattiasHe on 2016-12-01.
 */

public class ServerCommunicator {

    private static final String URL = "http://10.0.2.2:8080/api/events";
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private static final ServerCommunicator instance = new ServerCommunicator();

    private static final String TAG = EventListActivity.class.getSimpleName();

    private ServerCommunicator() {
        // private constructor to stop other classes from instantiating object (singelton)
    }

    public static ServerCommunicator getInstance() {
        return instance;
    }

    public List<Event> getEvents() {

        // get data from server
        HttpURLConnection conn;
        InputStream serverResponse;
        try {
            conn = initiateConnection();
            serverResponse = conn.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get response from server: " + e);
            return null;
        }

        // translate data to JSON object
        JSONArray eventsJson;
        try {
            eventsJson = inputStreamToJSON(serverResponse);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Failed to parse server response to JSON: " + e);
            return null;
        }

        // translate JSON to list of event objects
        return jsonToEventList(eventsJson);
    }

    private HttpURLConnection initiateConnection() throws IOException{
        java.net.URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        // int response = conn.getResponseCode();

        return conn;
    }

    private JSONArray inputStreamToJSON(InputStream is) throws IOException, JSONException {
        // input stream to JSONObject
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return new JSONArray(stringBuilder.toString());
    }

    private List<Event> jsonToEventList(JSONArray eventsJson) {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < eventsJson.length(); i++) {
            try {
                JSONObject eventJson = eventsJson.getJSONObject(i);
                String type = eventJson.getString("type");
                String location = eventJson.getString("location");
                String date = eventJson.getString("date");
                Event event = new Event(Event.EventType.valueOf(type), new Date(Long.parseLong(date)), location);
                events.add(event);
            } catch (JSONException e) {
                Log.w(TAG, "Were not able to parse json item since one or more of the required " +
                        "fields where missing! Discarding object. Exception: " + e);
                continue;
            }
        }
        return events;
    }
}
