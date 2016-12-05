package se.mathenri.bandapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ViewEventActivity extends AppCompatActivity {

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
        Event event = new Event(intent);

        TextView typeTextView = (TextView) findViewById(R.id.view_event_type);
        typeTextView.setText(event.getType().toString());

        TextView dateTextView = (TextView) findViewById(R.id.view_event_date);
        dateTextView.setText(event.getDateString());

        TextView locationTextView = (TextView) findViewById(R.id.view_event_location);
        locationTextView.setText(event.getLocation());
    }
}
