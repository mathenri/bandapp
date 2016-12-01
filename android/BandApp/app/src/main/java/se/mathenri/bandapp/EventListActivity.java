package se.mathenri.bandapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventListActivity extends ListActivity {

    private static final int ADD_EVENT_REQUEST = 0;
    private static final String TAG = EventListActivity.class.getSimpleName();

    private EventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add footer
        getListView().setFooterDividersEnabled(true);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TextView footerView = (TextView) inflater.inflate(R.layout.footer_view, null);
        getListView().addFooterView(footerView);
        footerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startAddEventActivityIntent = new Intent(
                        EventListActivity.this, AddEventActivity.class);
                startActivityForResult(startAddEventActivityIntent, ADD_EVENT_REQUEST);
            }
        });

        adapter = new EventListAdapter(getApplicationContext());
        setListAdapter(adapter);

        // request data from server
//        Event rehearsal = new Event(Event.EventType.REHEARSAL, new Date(0), "The Basement");
//        adapter.add(rehearsal);
//        Event concert = new Event(Event.EventType.CONCERT, new Date(0), "The Church");
//        adapter.add(concert);

        // start async taks that queries server for events and populates listview
        new GetEventsTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // add newly created event
        if (requestCode == ADD_EVENT_REQUEST && resultCode == RESULT_OK) {
            adapter.add(new Event(data));
        }
    }

    // queries the server for events and populates this activity's listview
    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {
        private static final String URL = "http://10.0.2.2:8080/api/events";
        private static final int READ_TIMEOUT = 10000;
        private static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected List<Event> doInBackground(Void... params) {
            return queryServer();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            // populate list with fetched events
            adapter.add(events);
        }
    }

    private List<Event> queryServer() {

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
        URL url = new URL(GetEventsTask.URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(GetEventsTask.READ_TIMEOUT);
        conn.setConnectTimeout(GetEventsTask.CONNECTION_TIMEOUT);
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
