package se.mathenri.bandapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ViewEventActivity extends AppCompatActivity {

    private static final String TAG = ViewEventActivity.class.getSimpleName();

    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        // enable "up"-button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        event = new Event(intent);

        TextView typeTextView = (TextView) findViewById(R.id.view_event_type);
        typeTextView.setText(event.getType().toString());

        TextView dateTextView = (TextView) findViewById(R.id.view_event_date);
        dateTextView.setText(event.getDateString());

        TextView locationTextView = (TextView) findViewById(R.id.view_event_location);
        locationTextView.setText(event.getLocation());

        // add food responsible if there are any
        if (!event.getFoodResponsible().isEmpty()) {
            TextView foodResponsibleTextView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = 2;
            foodResponsibleTextView.setLayoutParams(params);
            foodResponsibleTextView.setText(TextUtils.join("\n", event.getFoodResponsible()));
            LinearLayout layout = (LinearLayout) findViewById(R.id.view_event_content_layout);
            layout.addView(foodResponsibleTextView);
        }

        TextView absentTextView = (TextView) findViewById(R.id.view_event_absent);
        if (event.getAbsent().isEmpty()) {
            absentTextView.setText("None");
        } else {
            absentTextView.setText(TextUtils.join("\n", event.getAbsent()));
        }

        Button reportAbsenceButton = (Button) findViewById(R.id.report_absence_button);
        reportAbsenceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getDefaultSharedPreferences(getApplicationContext());
                String userName = settings.getString(
                        EventListActivity.USERNAME_PREFERENCE_KEY, null);
                if (!event.getAbsent().contains(userName)) {
                    event.addAbsent(userName);
                    try {
                        new UpdateEventTask().execute(event);
                    } catch (Exception e) {
                        Log.e(TAG, "Unable to update event! Exception: " + e);
                    }
                    finish();
                } else {
                    Log.w(TAG, "Could not report absence! User already in absent-list");
                    // TODO: view message
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete:
                new DeleteEventTask().execute(this.event);
                finish();

            case R.id.action_edit:
                Intent startEditEventActivityIntent = new Intent(
                        ViewEventActivity.this, EditEventActivity.class);
                startActivity(startEditEventActivityIntent);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // queries the server for events and populates this activity's listview
    private class DeleteEventTask extends AsyncTask<Event, Void, Void> {

        @Override
        protected Void doInBackground(Event... params) {
            try {
                serverCommunicator.deleteEvent(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed to add event to server! Exception: " + e);
            }
            return null;
        }
    }

    // queries the server for events and populates this activity's listview
    private class UpdateEventTask extends AsyncTask<Event, Void, Void> {

        @Override
        protected Void doInBackground(Event... params) {
            try {
                serverCommunicator.updateEvent(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed to update event on server! Exception: " + e);
            }
            return null;
        }
    }

}
