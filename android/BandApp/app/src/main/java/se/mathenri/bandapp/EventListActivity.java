package se.mathenri.bandapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private static final String TAG = EventListActivity.class.getSimpleName();

    private EventListAdapter adapter;

    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        // set list adapter
        ListView eventListView = (ListView) findViewById(R.id.event_list_view);
        adapter = new EventListAdapter(getApplicationContext());
        eventListView.setAdapter(adapter);

        new GetEventsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent startAddEventActivityIntent = new Intent(
                        EventListActivity.this, AddEventActivity.class);
                startActivity(startAddEventActivityIntent);
                return true;

            case R.id.action_refresh:
                adapter.clear();
                new GetEventsTask().execute();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // queries the server for events and populates this activity's listview
    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            List<Event> events;
            try {
                events = serverCommunicator.getEvents();
            } catch (Exception e) {
                Log.w(TAG, "Failed to get event list from server! Exception: " + e);
                return null;
            }
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            // populate list with fetched events
            adapter.add(events);
        }
    }
}
