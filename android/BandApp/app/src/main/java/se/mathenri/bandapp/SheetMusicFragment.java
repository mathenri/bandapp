package se.mathenri.bandapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SheetMusicFragment extends ListFragment implements OnItemClickListener {

    private static final String TAG = SheetMusicFragment.class.getSimpleName();

    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();
    private ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sheet_music, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // setup adapter
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_2,
                android.R.id.text1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

        // get the songs from the server
        new GetSongsTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Event eventToView = (Event) adapter.getItem(position);
//            Intent startViewEventActivityIntent = eventToView.toIntent();
//            startViewEventActivityIntent.setClass(getActivity(), ViewEventActivity.class);
//            startActivity(startViewEventActivityIntent);
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
            // get unique song names and populate list
            Set<String> songNames = new HashSet<>();
            for (Song song : songs) {
                songNames.add(song.getSongName());
            }
            adapter.addAll(songNames);
        }


    }
}
