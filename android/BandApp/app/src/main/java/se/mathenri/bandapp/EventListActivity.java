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

    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();

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

        @Override
        protected List<Event> doInBackground(Void... params) {
            return serverCommunicator.getEvents();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            // populate list with fetched events
            adapter.add(events);
        }
    }
}
