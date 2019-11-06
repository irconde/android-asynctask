package edu.ualr.asynchronousprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    AsyncTask photoAsyncTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button showBut = findViewById(R.id.showImageBut);
        showBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URL url = new URL("http://media.mydogspace.com.s3.amazonaws.com/wp-content" +
                            "/uploads/2013/08/puppy-500x350.jpg");
                    // ImageView element used to display the downloaded image
                    ImageView iv = findViewById(R.id.downloadedImage);
                    photoAsyncTask = new DownloadImageTask(MainActivity.this, iv).execute(url);
                    // TODO 01. Add a new method so we can get the status of the background task using the getStatus method
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // TODO 01. Method definition

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO 02. Cancel Pending Tasks when the activity is destroyed
    }
}
