package se.mathenri.bandapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class EventListActivity extends AppCompatActivity {

    public static final String USERNAME_PREFERENCE_KEY = "username";

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
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event eventToView = (Event) adapter.getItem(position);
                Intent startViewEventActivityIntent = eventToView.toIntent();
                startViewEventActivityIntent.setClass(
                        EventListActivity.this, ViewEventActivity.class);
                startActivity(startViewEventActivityIntent);
            }
        });

        // if the username is not set
        SharedPreferences settings = getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString(USERNAME_PREFERENCE_KEY, null);

        // if no username has been set, let user du this in new activity
        if (username == null) {
            Intent startEditUserNameActivityIntent = new Intent(
                    EventListActivity.this, EditUserNameActivity.class);
            startActivity(startEditUserNameActivityIntent);
        }

        new GetEventsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_list_menu, menu);
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

            case R.id.action_edit_username:
                Intent startEditUserNameActivity = new Intent(
                        EventListActivity.this, EditUserNameActivity.class);
                startActivity(startEditUserNameActivity);

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
