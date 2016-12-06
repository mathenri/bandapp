package se.mathenri.bandapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MattiasHe on 2016-12-01.
 */

public class ServerCommunicator {

    private static final String SERVER_URL = "http://10.0.2.2:8080/api/events";
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private static final ServerCommunicator instance = new ServerCommunicator();
    private static final String TAG = ServerCommunicator.class.getSimpleName();

    private static final String DATABASE_ID_FIELD = "_id";
    private static final String DATABASE_TYPE_FIELD = "type";
    private static final String DATABASE_LOCATION_FIELD = "location";
    private static final String DATABASE_DATE_FIELD = "date";
    private static final String DATABASE_FOOD_RESPONSIBLE_FIELD = "foodResponsible";


    private ServerCommunicator() {
        // private constructor to stop other classes from instantiating object (singelton)
    }

    public static ServerCommunicator getInstance() {
        return instance;
    }

    public void addEvent(Event event) throws Exception {
        Log.i(TAG, "Sending addEvent() request to server.");
        sendRequest(HTTP_METHOD_POST, SERVER_URL, event.toJson());
    }

    public void deleteEvent(Event event) throws Exception {
        Log.i(TAG, "Sending deleteEvent() request to server");
        sendRequest(HTTP_METHOD_DELETE, SERVER_URL + "/" + event.getDataBaseId(), null);
    }

    public List<Event> getEvents() throws Exception {
        Log.i(TAG, "Sending getEvents() request to server.");

        // get data from server
        String response = sendRequest(HTTP_METHOD_GET, SERVER_URL, null);
        return createEventList(response);
    }

    private String sendRequest(String method, String urlString, String content)
            throws IOException, UnexpectedResponseCodeException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        conn.setDoInput(true);
        if (method == HTTP_METHOD_POST) {
            conn.setDoOutput(true);
        }
        conn.connect();

        if (content != null) {
            // add content to http request

            Log.i(TAG, "Sending content to server: " + content);
            byte[] contentBytes = content.getBytes("UTF-8");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(contentBytes);
            outputStream.close();
        }

        // if the server did not respond with a status code OK, throw exception
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new UnexpectedResponseCodeException(responseCode + "");
        }

        InputStream responseInputStream = conn.getInputStream();
        return inputStreamToString(responseInputStream);
    }

    private String inputStreamToString(InputStream is) throws IOException {
        // input stream to JSONObject
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private List<Event> createEventList(String eventsString) throws JSONException{
        JSONArray eventsJson = new JSONArray(eventsString);
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < eventsJson.length(); i++) {
            try {
                JSONObject eventJson = eventsJson.getJSONObject(i);
                String databaseId = eventJson.getString(DATABASE_ID_FIELD);
                String type = eventJson.getString(DATABASE_TYPE_FIELD);
                String location = eventJson.getString(DATABASE_LOCATION_FIELD);
                String date = eventJson.getString(DATABASE_DATE_FIELD);

                // extract food responsible (not a required field)
                ArrayList<String> foodResponsible = new ArrayList<>();
                JSONArray foodResponsibleJson = eventJson.getJSONArray(
                        DATABASE_FOOD_RESPONSIBLE_FIELD);
                for (int j = 0; j < foodResponsibleJson.length(); j++) {
                    foodResponsible.add(foodResponsibleJson.getString(j));
                }
                Event event = new Event(Event.EventType.valueOf(type),
                        new Date(Long.parseLong(date)), location, databaseId, foodResponsible);
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
