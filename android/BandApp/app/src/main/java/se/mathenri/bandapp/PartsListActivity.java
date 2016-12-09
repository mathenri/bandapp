package se.mathenri.bandapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PartsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_list);

        Utils.setupActionBarWithUpButton(this);

        // when starting this activity it should slid in on top of the parent activity
        overridePendingTransition(R.anim.rigth_slide_in, R.anim.stay);

        // get the parts to display from parent activity and add to the list
        ArrayList<Song> parts = getIntent().getParcelableArrayListExtra(
                SheetMusicFragment.SELECTED_SONGS_INTENT_KEY);

        final SongAdapter adapter = new SongAdapter(this, true);
        final ListView listView = (ListView) findViewById(R.id.parts_list_view);
        listView.setAdapter(adapter);
        adapter.addAll(parts);

        // if an item is clicked, start a new activity displaying the clicked sheet music
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Song selectedPart = (Song) adapter.getItem(position);
                Intent startDisplaySheetMusicActivityIntent = new Intent(
                        PartsListActivity.this, DisplaySheetMusicActivity.class);
                startDisplaySheetMusicActivityIntent.putExtra(Song.FILE_NAME_KEY,
                        selectedPart.getFileName());
                startActivity(startDisplaySheetMusicActivityIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // slide out child activity when pressing "up" button
                finish();
                overridePendingTransition(R.anim.stay, R.anim.rigth_slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // slide out child activity when pressing "back" button
        overridePendingTransition(R.anim.stay, R.anim.rigth_slide_out);
    }
}
