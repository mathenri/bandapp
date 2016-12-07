package se.mathenri.bandapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by MattiasHe on 2016-11-29.
 */

public class EventListAdapter extends BaseAdapter {

    private Context context;
    private List<Event> events = new ArrayList<Event>();

    private static int FOOD_RESPONSIBLE_TEXT_VIEW_ID = View.generateViewId();

    public EventListAdapter(Context context) {
        this.context = context;
    }

    public void add(Event event)
    {
        events.add(event);
        Collections.sort(this.events, new EventDateComparator());
        notifyDataSetChanged();
    }

    public void add(List<Event> events) {
        if (events != null) {
            this.events.addAll(events);
            Collections.sort(this.events, new EventDateComparator());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        this.events.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = events.get(position);

        // create list view
        RelativeLayout eventLayout = (RelativeLayout) convertView;
        if (eventLayout == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            eventLayout = (RelativeLayout) layoutInflater.inflate(R.layout.event_list_item, null);
        } else {
            // if we reuse list item, there is a possibility there was a food responsible text view
            // added to it with text in it. we need to remove this or we might add a new
            // food responsible text view over it later.
            TextView foodResponsibleTextView = (TextView) eventLayout.findViewById(
                    FOOD_RESPONSIBLE_TEXT_VIEW_ID);
            if (foodResponsibleTextView != null) {
                eventLayout.removeView(foodResponsibleTextView);
            }
        }

        // fill in data
        TextView typeView = (TextView) eventLayout.findViewById(R.id.eventListItemType);
        typeView.setText(event.getType().toString());
        if (event.getType() == Event.EventType.CONCERT) {
            typeView.setTextColor(ContextCompat.getColor(context, R.color.colorConcert));
        } else {
            typeView.setTextColor(ContextCompat.getColor(context, R.color.colorRehearsal));
        }

        TextView dateView = (TextView) eventLayout.findViewById(R.id.eventListItemDate);
        dateView.setText(event.getDateString());

        TextView locationView = (TextView) eventLayout.findViewById(R.id.eventListItemLocation);
        locationView.setText(event.getLocation());

        TextView absentTextView = (TextView) eventLayout.findViewById(R.id.eventListItemAbsent);
        absentTextView.setText("Absent: " + event.getAbsent().size()+"");

        if (!event.getFoodResponsible().isEmpty()) {
            TextView foodResponsibleTextView = new TextView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.eventListItemAbsent);
            params.bottomMargin = 3;
            foodResponsibleTextView.setText(
                    "Food: " + TextUtils.join(", ", event.getFoodResponsible()));
            foodResponsibleTextView.setTextSize(12);
            foodResponsibleTextView.setTypeface(null, Typeface.ITALIC);
            foodResponsibleTextView.setId(FOOD_RESPONSIBLE_TEXT_VIEW_ID);
            RelativeLayout layout = (RelativeLayout) eventLayout.findViewById(R.id.eventListItem);
            layout.addView(foodResponsibleTextView, params);
        }

        return eventLayout;
    }

    private class EventDateComparator implements Comparator<Event> {
        @Override
        public int compare(Event e1, Event e2) {
            return e1.getDate().compareTo(e2.getDate());
        }
    }
}
