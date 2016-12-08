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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.view.View.OnClickListener;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MattiasHe on 2016-11-29.
 */

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = AddEventActivity.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private RadioButton rehearsalButton;
    private EditText locationEditText;
    private static TextView dateView;
    private static TextView timeView;
    TextView foodResponsibleTextView;
    EditText foodResponsibleEditText;

    private static Calendar calendar;
    private ArrayList<String> foodResponsibleList = new ArrayList<>();
    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_activity);
        Utils.setupActionBarWithUpButton(this);

        calendar = Calendar.getInstance();

        setViewReferences();
        setDefaultViewValues();
        setupListeners();
    }

    private void setViewReferences() {
        foodResponsibleTextView = (TextView) findViewById(R.id.foodResponsibleTextView);
        foodResponsibleEditText = (EditText) findViewById(R.id.addFoodResponsibleEditText);
        rehearsalButton = (RadioButton) findViewById(R.id.rehearsalRadioButton);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
    }

    private void setDefaultViewValues() {
        dateView = (TextView) findViewById(R.id.dateLabel);
        timeView = (TextView) findViewById(R.id.timeLabel);
        dateView.setText(DATE_FORMAT.format(calendar.getTime()));
        timeView.setText(TIME_FORMAT.format(calendar.getTime()));
    }

    private void setupListeners() {
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

        final Button addFoodResponsibleButton = (Button) findViewById(R.id.addFoodResponsibleButton);
        addFoodResponsibleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodResponsible();
            }
        });

        final EditText addFoodResponsibleEditText = (EditText) findViewById(
                R.id.addFoodResponsibleEditText);
        addFoodResponsibleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addFoodResponsible();
                }
                // return false to close keyboard when the user has pressed "Done"
                return false;
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
                new AddEventTask().execute(new Event(type, date, location, null, foodResponsibleList,
                        null));
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
            dateView.setText(DATE_FORMAT.format(calendar.getTime()));
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
            timeView.setText(TIME_FORMAT.format(calendar.getTime()));
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

    private void addFoodResponsible() {
        foodResponsibleList.add(foodResponsibleEditText.getText().toString());
        foodResponsibleTextView.setText(TextUtils.join(", ", foodResponsibleList));
        foodResponsibleEditText.setText(null);
    }
}
