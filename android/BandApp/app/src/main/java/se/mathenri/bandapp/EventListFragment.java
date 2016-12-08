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

import java.util.List;

public class EventListFragment extends ListFragment implements OnItemClickListener {

    private static final String TAG = EventListFragment.class.getSimpleName();
    private EventListAdapter adapter;
    private ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new EventListAdapter(getActivity().getApplicationContext());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        refreshList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Event eventToView = (Event) adapter.getItem(position);
        Intent startViewEventActivityIntent = eventToView.toIntent();
        startViewEventActivityIntent.setClass(getActivity(), ViewEventActivity.class);
        startActivity(startViewEventActivityIntent);
    }

    public void refreshList() {
        adapter.clear();
        new GetEventsTask().execute();
    }

    // queries the server for events and populates this activity's listview
    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            List<Event> events;
            try {
                events = serverCommunicator.getEvents();
            } catch (Exception e) {
                Log.w(TAG, "Failed to get event list from server! Exception: " + e);
                return null;
            }
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            // populate list with fetched events
            adapter.add(events);
        }
    }
}
