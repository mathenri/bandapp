package se.mathenri.bandapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DisplaySheetMusicActivity extends AppCompatActivity {

    private ImageView imageView;

    ServerCommunicator serverCommunicator = ServerCommunicator.getInstance();
    private static final String TAG = EventListFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sheet_music);

        Utils.setupActionBarWithUpButton(this);

        // when starting this activity it should slid in on top of the parent activity
        overridePendingTransition(R.anim.rigth_slide_in, R.anim.stay);

        imageView = (ImageView) findViewById(R.id.sheet_music_image_view);

        // get image filename sent by parent activity
        Intent intent = getIntent();
        String imageFileName = intent.getStringExtra(Song.FILE_NAME_KEY);

        // fetch image from server and display
        new GetPartTask().execute(imageFileName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // slide out child activity when pressing "up" button
                finish();
                overridePendingTransition(R.anim.stay, R.anim.rigth_slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // slide out child activity when pressing "back" button
        overridePendingTransition(R.anim.stay, R.anim.rigth_slide_out);
    }

    /*
     * Fetches an image from the server and displays it in this activity's image view
     */
    private class GetPartTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image = null;
            try {
                image = serverCommunicator.getImage(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed to get image from server! Exception: " + Log.getStackTraceString(e));
            }
            return image;
        }

        protected void onPostExecute(Bitmap image) {
            imageView.setImageBitmap(image);
        }
    }
}
