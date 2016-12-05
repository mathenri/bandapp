package se.mathenri.bandapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MattiasHe on 2016-11-29.
 */

public class AddEventActivity extends AppCompatActivity {

    private RadioButton rehearsalButton;
    private EditText locationEditText;
    private static TextView dateView;
    private static TextView timeView;

    // declared static so that they can be accessed from the static picker-classes
    private static Calendar calendar;

    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();

    private static final String TAG = AddEventActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_activity);

        // add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        // enable "up"-button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();

        // get references to views
        rehearsalButton = (RadioButton) findViewById(R.id.rehearsalRadioButton);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        dateView = (TextView) findViewById(R.id.dateLabel);
        timeView = (TextView) findViewById(R.id.timeLabel);

        // define on click listeners
        final Button datePickerButton = (Button) findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        final Button timePickerButton = (Button) findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Event.EventType type = (rehearsalButton.isChecked() ?
                        Event.EventType.REHEARSAL : Event.EventType.CONCERT);
                Date date = calendar.getTime();
                String location = locationEditText.getText().toString();

//                Intent data = new Event(type, date, location).toIntent();
//                setResult(Activity.RESULT_OK, data);
                new AddEventTask().execute(new Event(type, date, location, null));
                finish();
            }
        });
    }

    // queries the server for events and populates this activity's listview
    private class AddEventTask extends AsyncTask<Event, Void, Void> {

        @Override
        protected Void doInBackground(Event... params) {
            try {
                serverCommunicator.addEvent(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed to add event to server! Exception: " + e);
            }
            return null;
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // set the picker's default values
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateView.setText(dateFormat.format(calendar.getTime()));
        }
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // set the default time of the picker
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeView.setText(timeFormat.format(calendar.getTime()));
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
}
