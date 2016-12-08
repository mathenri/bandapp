package se.mathenri.bandapp;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class EditUserNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_name);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        SharedPreferences settings = getDefaultSharedPreferences(getApplicationContext());
        String currentUserName = settings.getString(
                MainActivity.USERNAME_PREFERENCE_KEY, null);

        // enable "up"-button, only if there is already a username
        if (currentUserName != null) {
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }

        TextView currentUserNameTextView = (TextView) findViewById(R.id.currentUserNameTextField);
        currentUserNameTextView.setText(currentUserName);

        final EditText userNameEditText = (EditText) findViewById(R.id.editUserNameEditText);

        Button submitButton = (Button) findViewById(R.id.editUserNameSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(MainActivity.USERNAME_PREFERENCE_KEY,
                        userNameEditText.getText().toString());
                editor.commit();
                finish();
            }
        });
    }
}
