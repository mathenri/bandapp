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

    private static final String SERVER_URL = "http://10.0.2.2:8080/api/";
    private static final String EVENTS_ROUTE = "events";
    private static final String SONGS_ROUTE = "songs";
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_DELETE = "DELETE";
    private static final String HTTP_METHOD_PUT = "PUT";

    private static final ServerCommunicator instance = new ServerCommunicator();
    private static final String TAG = ServerCommunicator.class.getSimpleName();

    private ServerCommunicator() {
        // private constructor to stop other classes from instantiating object (singelton)
    }

    public static ServerCommunicator getInstance() {
        return instance;
    }

    public void addEvent(Event event) throws Exception {
        Log.i(TAG, "Sending addEvent() request to server.");
        sendRequest(HTTP_METHOD_POST, SERVER_URL + EVENTS_ROUTE, event.toJson());
    }

    public void deleteEvent(Event event) throws Exception {
        Log.i(TAG, "Sending deleteEvent() request to server");
        sendRequest(HTTP_METHOD_DELETE, SERVER_URL + EVENTS_ROUTE + "/" + event.getDataBaseId(),
                null);
    }

    public List<Event> getEvents() throws Exception {
        Log.i(TAG, "Sending getEvents() request to server.");

        // get data from server
        String response = sendRequest(HTTP_METHOD_GET, SERVER_URL + EVENTS_ROUTE, null);
        return createEventList(response);
    }

    public List<Song> getSongs() throws Exception {
        Log.i(TAG, "Sending getSongs() request to server");
        String response = sendRequest(HTTP_METHOD_GET, SERVER_URL + SONGS_ROUTE, null);
        return createSongsList(response);
    }

    public void updateEvent(Event event) throws Exception {
        Log.i(TAG, "Sending updateEvent request to server.");

        sendRequest(HTTP_METHOD_PUT, SERVER_URL  + EVENTS_ROUTE + "/" + event.getDataBaseId(),
                event.toJson());
    }

    /*
     * Sends a HTTP request to a server defined by the urlString parameter. Returns the response
     * from the server as a String.
     */
    private String sendRequest(String method, String urlString, String content)
            throws IOException, UnexpectedResponseCodeException {

        // set connection parameters
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        conn.setDoInput(true);
        if (method.equals(HTTP_METHOD_POST) || method.equals(HTTP_METHOD_PUT)) {
            conn.setDoOutput(true);
        }

        conn.connect();

        // add content to http request
        if (content != null) {
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

        // return the response from the server as a String
        InputStream responseInputStream = conn.getInputStream();
        return inputStreamToString(responseInputStream);
    }

    /*
     * Transforms an InputStream to a String
     */
    private String inputStreamToString(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    /*
     * Takes a list of Events in JSON format and transforms it to a java List of Event objects.
     */
    private List<Event> createEventList(String eventsString) throws JSONException {
        JSONArray eventsJson = new JSONArray(eventsString);
        List<Event> events = new ArrayList<>();

        // for all the events in the JSON
        for (int i = 0; i < eventsJson.length(); i++) {
            try {

                // extract fields
                JSONObject eventJson = eventsJson.getJSONObject(i);
                String databaseId = eventJson.getString(Event.DATABASE_ID_KEY);
                String type = eventJson.getString(Event.TYPE_KEY);
                String location = eventJson.getString(Event.LOCATION_KEY);
                String date = eventJson.getString(Event.DATE_KEY);

                // extract food responsible (not a required field)
                ArrayList<String> foodResponsible = new ArrayList<>();
                JSONArray foodResponsibleJson = eventJson.getJSONArray(Event.FOOD_RESPONSIBLE_KEY);
                for (int j = 0; j < foodResponsibleJson.length(); j++) {
                    foodResponsible.add(foodResponsibleJson.getString(j));
                }

                // extract absent field
                ArrayList<String> absent = new ArrayList<>();
                JSONArray absentJson = eventJson.getJSONArray(Event.ABSENT_KEY);
                for (int j = 0; j < absentJson.length(); j++) {
                    absent.add(absentJson.getString(j));
                }

                // create the Event object and add it to the Java List
                Event event = new Event(Event.EventType.valueOf(type),
                        new Date(Long.parseLong(date)), location, databaseId, foodResponsible,
                        absent);
                events.add(event);
            } catch (JSONException e) {
                Log.w(TAG, "Were not able to parse json item since one or more of the required " +
                        "fields where missing! Discarding object. Exception: " + e);
                continue;
            }
        }
        return events;
    }

    /*
     * Takes a list of Songs in JSON format and transforms it to a java List of Song objects.
     */
    public List<Song> createSongsList(String songsString) throws JSONException {
        JSONArray songsJson = new JSONArray(songsString);
        List<Song> songs = new ArrayList<>();

        // for all the songs in the JSON
        for (int i = 0; i < songsJson.length(); i++) {
            try {

                // extract fields
                JSONObject songJson = songsJson.getJSONObject(i);
                String songName = songJson.getString(Song.SONG_NAME_KEY);
                String part = songJson.getString(Song.PART_KEY);
                String fileName = songJson.getString(Song.FILE_NAME_KEY);

                // create the Event object and add it to the Java List
                Song song = new Song(songName, part, fileName);
                songs.add(song);
            } catch (JSONException e) {
                Log.w(TAG, "Were not able to parse json item since one or more of the required " +
                        "fields where missing! Discarding object. Exception: " + e);
                continue;
            }
        }
        return songs;
    }
}
