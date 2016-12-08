package se.mathenri.bandapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    public static final String USERNAME_PREFERENCE_KEY = "username";
    private static final String EVENT_LIST_TAB_NAME = "Event list";
    private static final String SHEET_MUSIC_TAB_NAME = "Sheet music";

    private EventListFragment eventListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        setupTabs();

        // if no username has been set, let user du this in new activity
        checkUsernameSet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent startAddEventActivityIntent = new Intent(
                        MainActivity.this, AddEventActivity.class);
                startActivity(startAddEventActivityIntent);
                return true;

            case R.id.action_refresh:
                eventListFragment.refreshList();
                return true;

            case R.id.action_edit_username:
                Intent startEditUserNameActivity = new Intent(
                        MainActivity.this, EditUserNameActivity.class);
                startActivity(startEditUserNameActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupTabs() {
        // set tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(EVENT_LIST_TAB_NAME));
        tabLayout.addTab(tabLayout.newTab().setText(SHEET_MUSIC_TAB_NAME));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // create fragments to tab between
        eventListFragment = new EventListFragment();
        SheetMusicFragment sheetMusicFragment = new SheetMusicFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(eventListFragment);
        fragments.add(sheetMusicFragment);

        // setup pager and adapter
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(
                getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    /*
     * Check if a user name has been set. If not, an activity is started to enable the user to do
     * so.
     */
    private void checkUsernameSet() {
        SharedPreferences settings = getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString(USERNAME_PREFERENCE_KEY, null);
        if (username == null) {
            Intent startEditUserNameActivityIntent = new Intent(
                    MainActivity.this, EditUserNameActivity.class);
            startActivity(startEditUserNameActivityIntent);
        }
    }
}
