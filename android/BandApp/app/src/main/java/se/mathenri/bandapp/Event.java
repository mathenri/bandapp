package se.mathenri.bandapp;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MattiasHe on 2016-11-29.
 */

public class Event {

    // TODO: add "section rehersal", "meeting"?
    public enum EventType {
        REHEARSAL, CONCERT
    }

    // keys used to identify event fields when encapsulated in an Intent
    private static final String TYPE_KEY = "type";
    private static final String DATE_KEY = "date";
    private static final String LOCATION_KEY = "location";

    private EventType type;
    private Date date;
    private String location;
    private List<String> fikaResponsible = new ArrayList<>();
    private List<String> prayerReponsible = new ArrayList<>();
    private List<String> absent = new ArrayList<>();

    public Event(EventType type, Date date, String location) {
        this.type = type;
        this.date = date;
        this.location = location;
    }

    /*
     * Creates an Event object from an Intent (passed between Activities)
     */
    public Event(Intent intent) {

        this.type = (EventType) intent.getSerializableExtra(Event.TYPE_KEY);
        this.date = new Date(intent.getLongExtra(Event.DATE_KEY, -1));
        this.location = intent.getStringExtra(Event.LOCATION_KEY);
    }

    // Encapsulates the Event in an Intent object and returns it
    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra(Event.TYPE_KEY, this.type);
        intent.putExtra(Event.DATE_KEY, this.date.getTime());
        intent.putExtra(Event.LOCATION_KEY, this.location);
        return intent;
    }

    public void addFikaResponsible(String person) {
        fikaResponsible.add(person);
    }

    public void addPrayerResponsible(String person) {
        prayerReponsible.add(person);
    }

    public void addAbsent(String person) {
        absent.add(person);
    }

    public EventType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }
}
