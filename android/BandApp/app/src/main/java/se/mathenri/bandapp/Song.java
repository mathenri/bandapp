package se.mathenri.bandapp;

/**
 * Created by MattiasHe on 2016-12-08.
 */

public class Song {

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

    public String getSongName() { return songName; }
    public String getPart() { return part; }
    public String getFileName() { return fileName; }
}
