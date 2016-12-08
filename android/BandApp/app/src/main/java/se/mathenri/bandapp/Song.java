package se.mathenri.bandapp;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MattiasHe on 2016-12-08.
 */

// implement Parcelable so we can send list of Song objects via intents
public class Song implements Parcelable {

    public static final String SONG_NAME_KEY = "songName";
    public static final String PART_KEY = "part";
    public static final String FILE_NAME_KEY = "fileName";

    private String songName;
    private String part;
    private String fileName;

    public Song(String songName, String part, String fileName) {
        this.songName = songName;
        this.part = part;
        this.fileName = fileName;
    }

    /*
     * Creates an Song object from an Intent (passed between Activities)
     */
    public Song(Intent intent) {
        this.songName = intent.getStringExtra(SONG_NAME_KEY);
        this.part = intent.getStringExtra(PART_KEY);
        this.fileName = intent.getStringExtra(FILE_NAME_KEY);
    }

    // Encapsulates the Song in an Intent object and returns it
    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra(SONG_NAME_KEY, this.songName);
        intent.putExtra(PART_KEY, this.part);
        intent.putExtra(FILE_NAME_KEY, this.fileName);
        return intent;
    }

    public String getSongName() { return songName; }
    public String getPart() { return part; }
    public String getFileName() { return fileName; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songName);
        dest.writeString(this.part);
        dest.writeString(this.fileName);
    }

    protected Song(Parcel in) {
        this.songName = in.readString();
        this.part = in.readString();
        this.fileName = in.readString();
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
