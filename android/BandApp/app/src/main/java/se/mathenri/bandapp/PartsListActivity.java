package se.mathenri.bandapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        ArrayList<Song> parts = getIntent().getParcelableArrayListExtra(
                SheetMusicFragment.SELECTED_SONGS_INTENT_KEY);
        final SongAdapter adapter = new SongAdapter(this, true);

        final ListView listView = (ListView) findViewById(R.id.parts_list_view);
        listView.setAdapter(adapter);
        adapter.addAll(parts);
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
}