package se.mathenri.bandapp;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

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

    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // keys used to identify event fields when encapsulated in an Intent
    private static final String TYPE_KEY = "type";
    private static final String DATE_KEY = "date";
    private static final String LOCATION_KEY = "location";
    private static final String DATABASE_ID_KEY = "id";

    private String dataBaseId;
    private EventType type;
    private Date date;
    private String location;
    private List<String> fikaResponsible = new ArrayList<>();
    private List<String> prayerReponsible = new ArrayList<>();
    private List<String> absent = new ArrayList<>();

    public Event(EventType type, Date date, String location, String dataBaseId) {
        this.dataBaseId = dataBaseId;
        this.type = type;
        this.date = date;
        this.location = location;
    }

    /*
     * Creates an Event object from an Intent (passed between Activities)
     */
    public Event(Intent intent) {
        this.dataBaseId = intent.getStringExtra(Event.DATABASE_ID_KEY);
        this.type = (EventType) intent.getSerializableExtra(Event.TYPE_KEY);
        this.date = new Date(intent.getLongExtra(Event.DATE_KEY, -1));
        this.location = intent.getStringExtra(Event.LOCATION_KEY);
    }

    // Encapsulates the Event in an Intent object and returns it
    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra(Event.DATABASE_ID_KEY, this.dataBaseId);
        intent.putExtra(Event.TYPE_KEY, this.type);
        intent.putExtra(Event.DATE_KEY, this.date.getTime());
        intent.putExtra(Event.LOCATION_KEY, this.location);
        return intent;
    }

    public String toJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Event.TYPE_KEY, this.type.toString());
        jsonObject.put(Event.DATE_KEY, this.date.getTime()+"");
        jsonObject.put(Event.LOCATION_KEY, this.location);
        return jsonObject.toString();
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

    public String getDateString() {
        return DEFAULT_DATE_FORMAT.format(date);
    }

    public String getLocation() {
        return location;
    }

    public String getDataBaseId() {
        if (dataBaseId == null) {
            throw new IllegalStateException("The database id for this Event has not been set! " +
                    "Has it been added to the database?");
        } else {
            return dataBaseId;
        }
    }
}
