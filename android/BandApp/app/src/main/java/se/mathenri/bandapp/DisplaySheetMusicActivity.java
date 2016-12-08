package se.mathenri.bandapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DisplaySheetMusicActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sheet_music);

        Utils.setupActionBarWithUpButton(this);

        imageView = (ImageView) findViewById(R.id.sheet_music_image_view);

        Intent intent = getIntent();
        String imageFileName = intent.getStringExtra(Song.FILE_NAME_KEY);
        new GetPartTask().execute(imageFileName);
    }

    private class GetPartTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap picture = null;
            String url = "http://10.0.2.2:8080/images/" + params[0];
            try {
                InputStream in = new URL(url).openStream();
                picture = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return picture;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
