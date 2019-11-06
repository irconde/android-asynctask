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


// Why do we have to do this?
// 1. Callback methods defined by AsyncTask don't throw exceptions. We must use try/catch blocks instead
// 2. We'll want to catch the exception and directly update the user interface to alert the user.
//      2.1. We cannot update the user interface from doInbackground
//      2.2 Solution: have doInBackground return and object that contains either the result or an exception

// TODO 19. The activity implements the AsyncListener interface
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL = "http://media.mydogspace.com.s3.amazonaws.com/wp-content/uploads/2013/08/puppy-500x350.jpg";
    // TODO 24. Define a tag to identify the fragment
    // TODO 21. Replace the AsyncTask member with a Fragment member
    AsyncTask photoAsyncTask = null;
    // TODO 22. Add a ProgressDialog member.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO 23. Replace the definition of the button with the initialization of the fragment
        Button showBut = findViewById(R.id.showImageBut);
        showBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    /*
                    URL url = new URL("http://media.mydogspace.com.s3.amazonaws.com/wp-content" +
                            "/uploads/2013/08/puppy-500x350.jpg");
                     */
                    URL url = new URL("http://img.allw.mn/content/www/2009/03/notfound.jpg");
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
        // TODO 28. We can replace this cancellation code with the invocation of the new Fragment's cancel method
        if(photoAsyncTask !=null && photoAsyncTask.getStatus()!=AsyncTask.Status.FINISHED){
            photoAsyncTask.cancel(true);
        }
    }

    // TODO 04. We move the loadDefaultImage to the Activity

    // TODO 25. We define the body of the onPreExecute method. Here we have to create and initialize the progress dialog
    // TODO 26. Create a new method to prepare the progress dialog

    // TODO 26. prepareProgressDialog method definition.
        // TODO 27. Define a cancel method in Fragment class in order to cancel the AsyncTask

    // TODO 29. We define the body of the onProgressUpdate.
        // TODO 29.01. Create the progress dialog if it doesn't exist
        // TODO 29.02. Update the progress value

    // TODO 30. Once the background task has finished we must remove the fragment from the activity
    //  and dismiss the progress dialog in case it's still visible

    // TODO 31. We invoke the cleanUp method both in onPostExecute and onCancelled methods
    // TODO 32. Show the received Bitmap image

}
