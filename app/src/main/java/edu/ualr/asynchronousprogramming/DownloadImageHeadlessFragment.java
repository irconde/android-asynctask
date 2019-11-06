package edu.ualr.asynchronousprogramming;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by irconde on 2019-11-06.
 */
// TODO 01. Create a retained headless Fragment, which wraps our AsyncTask
public class DownloadImageHeadlessFragment extends Fragment {

    // TODO 06. We need to define an interface that will allow the AsyncTask to communicate with the
    //  Activity through the fragment and ask it to make updates in the UI

    public interface AsyncListener {
        void onPreExecute();
        void onProgressUpdate(Integer... progress);
        void onPostExecute(Result<Bitmap> result);
        void onCancelled(Result<Bitmap> result);
    }

    // TODO 07. We create a new member that will keep a reference to the Activity that receives the async task callbacks
    private AsyncListener listener;

    // TODO 11. The Fragment is now the responsible for creating and executing the AsyncTask,
    //  so we need a new member to store the reference
    private DownloadImageTask task;

    // TODO 12. We have to pass the fragment the url value used by the wrapped AsyncTask. Thus, we need to define a newInstance method
    public static DownloadImageHeadlessFragment newInstance(String url) {
        DownloadImageHeadlessFragment myFragment = new DownloadImageHeadlessFragment();
        Bundle args = new Bundle();
        args.putString("url",url);
        myFragment.setArguments(args);
        return myFragment;
    }

    // TODO 13. Override the onCreate method to define the initialization of the several members of the fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO 14. Avoid disposing the fragment when the activity restarts.
        setRetainInstance(true);
        // TODO 15. Create a new instance of the AsyncTask
        task = new DownloadImageTask();
        // TODO 16. Get an URL with the provided String value
        URL url = null;
        try {
            String urlStr = getArguments().getString("url");
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // TODO 17. Execute the AsyncTask using the URL
        task.execute(url);
    }

    // TODO 08. We override the onAttach method to initialize the listener member
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AsyncListener) {
            this.listener = (AsyncListener) context;
        }
    }

    // TODO 09. We override the onDetach() method to delete the reference to the Activity, stored in listener member
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // TODO 27. Cancel method definition
    public void cancel() {
        if(task!=null){
            task.cancel(false);
        }
    }

    // TODO 02. We include our AsyncTask class as inner class of the Fragment
    private class DownloadImageTask extends AsyncTask<URL, Integer, Result<Bitmap>> {

        // TODO 03. The AsyncTask is not longer the responsible for providing the user the progress dialog
        //  or updating the ImageView with the received Bitmap.
        //  The activity is. That's why references to ImageView, Context and ProgressDialog are not longer needed
        // TODO 03. References to ImageView and Context are not longer needed so we can delete

        int downloadedBytes = 0;
        int totalBytes = 0;

        // TODO 05. We can even delete the constructor

        // TODO 10. We use the listener to send progress updates and results back to the Activity
        @Override
        protected void onPreExecute() {
            if (listener != null) {
                listener.onPreExecute();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (listener != null) {
                listener.onProgressUpdate(values);
            }
        }

        // Retrieves the image from a URL
        private Bitmap downloadBitmap(URL url) throws Exception{
            Bitmap bitmap =null;
            InputStream is = null;
            try {
                if (isCancelled()) {
                    return null;
                }
                // TODO 18. We publish the initial progress value
                publishProgress(0);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK){
                    throw new Exception("Unsuccessful Result code");
                }

                totalBytes = conn.getContentLength();
                downloadedBytes = 0;

                is = conn.getInputStream();
                BufferedInputStream bif = new BufferedInputStream(is) {

                    int progress = 0;

                    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
                        int readBytes = super.read(buffer, byteOffset, byteCount);

                        if ( isCancelled() ){
                            // Returning -1 means that there is no more data because the
                            // end of the stream has been reached.
                            return -1;
                        }
                        if (readBytes > 0) {
                            downloadedBytes += readBytes;
                            // int percent = (int) ((((float) downloadedBytes) / ((float) totalBytes)) * 100);
                            int percent = (int) ((downloadedBytes * 100f) / totalBytes);
                            if (percent > progress) {
                                publishProgress(percent);
                                progress = percent;
                            }
                        }
                        return readBytes;
                    }
                };
                Bitmap downloaded = BitmapFactory.decodeStream(bif);
                if ( !isCancelled() ){
                    bitmap = downloaded;
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onCancelled(Result<Bitmap> result) {
            if (listener != null) {
                listener.onCancelled(result);
            }
        }

        @Override
        protected Result<Bitmap> doInBackground(URL... urls) {
            Result<Bitmap> result = new Result<>();
            try {
                URL url = urls[0];
                Bitmap bitmap = downloadBitmap(url);
                result.result = bitmap;
            } catch (Exception e) {
                result.error = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result<Bitmap> result) {
            if (listener != null) {
                listener.onPostExecute(result);
            }
        }

        // TODO 04. We move the loadDefaultImage to the Activity

    }
}
