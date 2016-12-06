package se.mathenri.bandapp;

import android.content.Intent;

import org.json.JSONArray;
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

    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");

    // keys used to identify event fields when encapsulated in an Intent
    public static final String TYPE_KEY = "type";
    public static final String DATE_KEY = "date";
    public static final String LOCATION_KEY = "location";
    public static final String DATABASE_ID_KEY = "_id";
    public static final String FOOD_RESPONSIBLE_KEY = "foodResponsible";
    public static final String ABSENT_KEY = "absent";

    private String dataBaseId;
    private EventType type;
    private Date date;
    private String location;
    private ArrayList<String> foodResponsible = new ArrayList<>();
    private ArrayList<String> absent = new ArrayList<>();

    public Event(EventType type, Date date, String location, String dataBaseId,
                 ArrayList<String> foodResponsible, ArrayList absent) {
        this.dataBaseId = dataBaseId;
        this.type = type;
        this.date = date;
        this.location = location;
        this.foodResponsible = foodResponsible;
        this.absent = absent;
    }

    /*
     * Creates an Event object from an Intent (passed between Activities)
     */
    public Event(Intent intent) {
        this.dataBaseId = intent.getStringExtra(Event.DATABASE_ID_KEY);
        this.type = (EventType) intent.getSerializableExtra(Event.TYPE_KEY);
        this.date = new Date(intent.getLongExtra(Event.DATE_KEY, -1));
        this.location = intent.getStringExtra(Event.LOCATION_KEY);
        this.foodResponsible = (ArrayList<String>) intent.getSerializableExtra(
                Event.FOOD_RESPONSIBLE_KEY);
        this.absent = (ArrayList<String>) intent.getSerializableExtra(Event.ABSENT_KEY);

    }

    // Encapsulates the Event in an Intent object and returns it
    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra(Event.DATABASE_ID_KEY, this.dataBaseId);
        intent.putExtra(Event.TYPE_KEY, this.type);
        intent.putExtra(Event.DATE_KEY, this.date.getTime());
        intent.putExtra(Event.LOCATION_KEY, this.location);
        intent.putStringArrayListExtra(Event.FOOD_RESPONSIBLE_KEY, foodResponsible);
        intent.putStringArrayListExtra(Event.ABSENT_KEY, absent);
        return intent;
    }

    public String toJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Event.TYPE_KEY, this.type.toString());
        jsonObject.put(Event.DATE_KEY, this.date.getTime()+"");
        jsonObject.put(Event.LOCATION_KEY, this.location);

        JSONArray foodResponsiblesJson = new JSONArray();
        for (String user : foodResponsible) {
            foodResponsiblesJson.put(user);
        }
        jsonObject.put(Event.FOOD_RESPONSIBLE_KEY, foodResponsiblesJson);

        JSONArray absentJson = new JSONArray();
        for (String user : absent) {
            absentJson.put(user);
        }
        jsonObject.put(Event.ABSENT_KEY, absentJson);

        return jsonObject.toString();
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

    public List<String> getFoodResponsible() {
        return foodResponsible;
    }

    public ArrayList<String> getAbsent() { return absent; }
}
