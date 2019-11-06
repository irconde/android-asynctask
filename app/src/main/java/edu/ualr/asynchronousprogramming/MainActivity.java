package edu.ualr.asynchronousprogramming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

// Why do we have to do this?
// 1. Callback methods defined by AsyncTask don't throw exceptions. We must use try/catch blocks instead
// 2. We'll want to catch the exception and directly update the user interface to alert the user.
//      2.1. We cannot update the user interface from doInbackground
//      2.2 Solution: have doInBackground return and object that contains either the result or an exception

// TODO 19. The activity implements the AsyncListener interface
public class MainActivity extends AppCompatActivity implements DownloadImageHeadlessFragment.AsyncListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL = "http://media.mydogspace.com.s3.amazonaws.com/wp-content/uploads/2013/08/puppy-500x350.jpg";
    // TODO 24. Define a tag to identify the fragment
    private static final String DOWNLOAD_PHOTO_FRAG = "download_photo_as_fragment";
    // TODO 21. Replace the AsyncTask member with a Fragment member
    private DownloadImageHeadlessFragment fragment;
    // TODO 22. Add a ProgressDialog member.
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO 23. Replace the definition of the button with the initialization of the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (DownloadImageHeadlessFragment) fragmentManager.findFragmentByTag(DOWNLOAD_PHOTO_FRAG);
        // We'll use the same URL: "http://media.mydogspace.com.s3.amazonaws.com/wp-content/uploads/2013/08/puppy-500x350.jpg"
        if (fragment == null) {
            fragment = DownloadImageHeadlessFragment.newInstance(URL);
            fragmentManager.beginTransaction().add(fragment, DOWNLOAD_PHOTO_FRAG).commit();
        }

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
        if (fragment != null) {
            fragment.cancel();
        }
    }

    // TODO 04. We move the loadDefaultImage to the Activity
    private void loadDefaultImage(ImageView imageView) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
        imageView.setImageBitmap(bitmap);
    }

    // TODO 25. We define the body of the onPreExecute method. Here we have to create and initialize the progress dialog
    @Override
    public void onPreExecute() {
        if (progressDialog == null)
            // TODO 26. Create a new method to prepare the progress dialog
            prepareProgressDialog();
    }

    private void prepareProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.downloading_image);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO 27. Define a cancel method in Fragment class in order to cancel the AsyncTask
                fragment.cancel();
            }
        });
        progressDialog.show();
    }

    // TODO 29. We define the body of the onProgressUpdate.
    @Override
    public void onProgressUpdate(Integer... progress) {
        // TODO 29.01. Create the progress dialog if it doesn't exist
        // TODO 29.02. Update the progress value
        if (progressDialog == null)
            prepareProgressDialog();
        progressDialog.setProgress(progress[0]);
    }

    // TODO 30. Once the background task has finished we must remove the fragment from the activity
    //  and dismiss the progress dialog in case it's still visible
    private void cleanUp() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentByTag(DOWNLOAD_PHOTO_FRAG);
        fm.beginTransaction().remove(frag).commit();
    }

    // TODO 31. We invoke the cleanUp method both in onPostExecute and onCancelled methods
    @Override
    public void onPostExecute(Result<Bitmap> result) {
        // TODO 32. Show the received Bitmap image
        if (result.result != null) {
            ImageView iv = findViewById(R.id.downloadedImage);
            iv.setImageBitmap(result.result);
        }
        cleanUp();
    }

    @Override
    public void onCancelled(Result<Bitmap> result) {
        cleanUp();
    }
}
