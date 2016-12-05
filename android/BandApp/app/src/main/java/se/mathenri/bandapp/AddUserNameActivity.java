package se.mathenri.bandapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AddUserNameActivity extends AppCompatActivity {

    public static final String USERNAME_PREFERENCE_KEY = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_name);

        // if the username has already been set, redirect to eventlist screen
        SharedPreferences settings = getDefaultSharedPreferences(getApplicationContext());
        final String username = settings.getString(USERNAME_PREFERENCE_KEY, null);
        if (username != null) {
            startEventListActivity();
        }

        final EditText userNameEditText = (EditText) findViewById(R.id.addUserNameEditText);

        Button submitButton = (Button) findViewById(R.id.addUserNameSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(USERNAME_PREFERENCE_KEY, userNameEditText.getText().toString());
                editor.commit();
                startEventListActivity();
            }
        });
    }

    private void startEventListActivity() {
        Intent startEventListActivity = new Intent(
                AddUserNameActivity.this, EventListActivity.class);
        startActivity(startEventListActivity);
    }
}
