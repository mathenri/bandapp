package se.mathenri.bandapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by MattiasHe on 2016-12-07.
 */

public class Utils {

    /*
     * Sets up an action bar with an "up button" on an activity
     */
    public static void setupActionBarWithUpButton(AppCompatActivity activity) {
        // add toolbar
        Toolbar myToolbar = (Toolbar) activity.findViewById(R.id.toolbar_main);
        activity.setSupportActionBar(myToolbar);

        // enable "up"-button
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
