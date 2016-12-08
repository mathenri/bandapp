package se.mathenri.bandapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SheetMusicFragment extends ListFragment implements OnItemClickListener {

    private static final String TAG = SheetMusicFragment.class.getSimpleName();
    public static final String SELECTED_SONGS_INTENT_KEY = "songs";

    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();
    private SongAdapter adapter;
    private List<Song> allSongs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sheet_music, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // setup adapter
        adapter = new SongAdapter(getActivity().getApplicationContext(), false);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

        // get the songs from the server
        new GetSongsTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // start a new activity displaying the available parts of the song that was clicked
        Song selectedSong = (Song) adapter.getItem(position);
        ArrayList<Song> parts = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.getSongName().equals(selectedSong.getSongName()))
            parts.add(song);
        }

        Intent startPartsListActivityIntent = new Intent(getActivity(), PartsListActivity.class);
        startPartsListActivityIntent.putParcelableArrayListExtra(SELECTED_SONGS_INTENT_KEY, parts);
        startActivity(startPartsListActivityIntent);
    }

    // queries the server for songs and populates this fragment's listview
    private class GetSongsTask extends AsyncTask<Void, Void, List<Song>> {

        @Override
        protected List<Song> doInBackground(Void... params) {
            List<Song> songs;
            try {
                songs = serverCommunicator.getSongs();
            } catch (Exception e) {
                Log.w(TAG, "Failed to get song list from server! Exception: " + e);
                return null;
            }
            return songs;
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            allSongs = songs;

            // get songs with unique song names and populate list
            List<Song> songsWithUniqueSongNames = new ArrayList<>();
            List<String> addedNames = new ArrayList<>();
            for (Song song : allSongs) {
                if (!addedNames.contains(song.getSongName())) {
                    addedNames.add(song.getSongName());
                    songsWithUniqueSongNames.add(song);
                }
            }
            adapter.addAll(songsWithUniqueSongNames);
        }
    }
}
