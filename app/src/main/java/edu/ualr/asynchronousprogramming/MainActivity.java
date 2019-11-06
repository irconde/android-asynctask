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
                    logAsyncTaskStatus(photoAsyncTask);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void logAsyncTaskStatus(AsyncTask task) {
        switch (task.getStatus()) {
            case PENDING:
                Log.d(TAG, "Task has not started yet. We can invoke execute");
                break;
            case RUNNING:
                Log.d(TAG, "Task currently running in background");
                break;
            case FINISHED:
                if (task.isCancelled()) {
                    Log.d(TAG, "Task is done. OnCancelled was called");
                } else {
                    Log.d(TAG, "Task is done. OnPostExecute was called");
                }
                break;
                default: Log.d(TAG , "Unknown state");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(photoAsyncTask !=null && photoAsyncTask.getStatus()!=AsyncTask.Status.FINISHED){
            photoAsyncTask.cancel(true);
        }
    }
}
