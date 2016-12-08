package se.mathenri.bandapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MattiasHe on 2016-12-08.
 */

public class SongAdapter extends BaseAdapter {

    private Context context;
    private List<Song> songs = new ArrayList<Song>();

    // specifies if we display the song name or the part name in the list
    private boolean displayPart = false;

    public SongAdapter(Context context, boolean displayPart) {
        this.context = context;
        this.displayPart = displayPart;
    }

    public void add(Song song)
    {
        songs.add(song);
        notifyDataSetChanged();
    }

    public void addAll(List<Song> songs) {
        if (songs != null) {
            this.songs.addAll(songs);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        this.songs.clear();
    }

    public List<Song> getSongs() {return songs; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = songs.get(position);

        // create list view
        RelativeLayout songLayout = (RelativeLayout) convertView;
        if (songLayout == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            songLayout = (RelativeLayout) layoutInflater.inflate(R.layout.song_list_item, null);
        }

        // fill in data
        TextView textView = (TextView) songLayout.findViewById(R.id.songListItemSongName);
        if (displayPart) {
            textView.setText(song.getPart());
        } else {
            textView.setText(song.getSongName());
        }
        return songLayout;
    }
}
