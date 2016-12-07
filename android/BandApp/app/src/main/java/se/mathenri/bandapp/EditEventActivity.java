package se.mathenri.bandapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity {

    private static Calendar calendar = Calendar.getInstance();
    private Event event;
    ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();

    private static final String TAG = EditEventActivity.class.getSimpleName();

    private static TextView dateTextView;
    private static TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        // enable "up"-button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        event = new Event(intent);

        // set field default values
        final TextView typeTextView = (TextView) findViewById(R.id.edit_event_type);
        typeTextView.setText(event.getType().toString());

        dateTextView = (TextView) findViewById(R.id.edit_event_date);
        dateTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(event.getDate()));

        timeTextView = (TextView) findViewById(R.id.edit_event_time);
        timeTextView.setText(new SimpleDateFormat("HH:mm").format(event.getDate()));

        final EditText locationEditText = (EditText) findViewById(R.id.edit_event_location);
        locationEditText.setText(event.getLocation());

        final TextView absentTextView = (TextView) findViewById(R.id.edit_event_absent);
        absentTextView.setText(TextUtils.join("\n", event.getAbsent()));

        final EditText addAbsentEditText = (EditText) findViewById(R.id.edit_event_add_absent);
        final EditText removeAbsentEditText = (EditText) findViewById(R.id.edit_event_remove_absent);
        final EditText addFoodResponsibleEditText = (EditText) findViewById(
                R.id.edit_event_add_food_responsible);
        final EditText removeFoodResponsibleText = (EditText) findViewById(
                R.id.edit_event_remove_food_responsible);

        final TextView foodResponsibleTextView = (TextView) findViewById(R.id.edit_event_food_responsible_list);
        foodResponsibleTextView.setText(TextUtils.join("\n", event.getFoodResponsible()));

        // set listeners
        Button changeTypeButton = (Button) findViewById(R.id.edit_event_type_button);
        changeTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event.EventType newType = (event.getType() == Event.EventType.CONCERT ?
                        Event.EventType.REHEARSAL : Event.EventType.CONCERT);
                event.setType(newType);
                typeTextView.setText(newType.toString());
            }
        });

        Button datePickButton = (Button) findViewById(R.id.edit_event_date_button);
        datePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                event.setDate(calendar.getTime());
            }
        });

        Button timePickButton = (Button) findViewById(R.id.edit_event_time_button);
        timePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                event.setDate(calendar.getTime());
            }
        });

        Button addAbsentButton = (Button) findViewById(R.id.edit_event_add_absent_button);
        addAbsentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.addAbsent(addAbsentEditText.getText().toString());
                absentTextView.setText(TextUtils.join("\n", event.getAbsent()));
            }
        });

        Button removeAbsentButton = (Button) findViewById(R.id.edit_event_remove_absent_button);
        removeAbsentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.removeAbsent(removeAbsentEditText.getText().toString());
                absentTextView.setText(TextUtils.join("\n", event.getAbsent()));
            }
        });

        Button addFoodResponsibleButton = (Button) findViewById(
                R.id.edit_event_add_food_responsible_button);
        addFoodResponsibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.addFoodResponsible(addFoodResponsibleEditText.getText().toString());
                foodResponsibleTextView.setText(TextUtils.join("\n", event.getFoodResponsible()));
            }
        });

        Button removeFoodResponsibleButton = (Button) findViewById(
                R.id.edit_event_remove_food_responsible_button);
        removeFoodResponsibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.removeFoodResponsible(removeFoodResponsibleText.getText().toString());
                foodResponsibleTextView.setText(TextUtils.join("\n", event.getFoodResponsible()));
            }
        });

        Button submitButton = (Button) findViewById(R.id.edit_event_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setLocation(locationEditText.getText().toString());
                new UpdateEventTask().execute(event);
                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.edit_event_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class UpdateEventTask extends AsyncTask<Event, Void, Void> {

        @Override
        protected Void doInBackground(Event... params) {
            try {
                serverCommunicator.updateEvent(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed to add event to server! Exception: " + Log.getStackTraceString(e));
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
            dateTextView.setText(dateFormat.format(calendar.getTime()));
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
            timeTextView.setText(timeFormat.format(calendar.getTime()));
        }
    }
}
