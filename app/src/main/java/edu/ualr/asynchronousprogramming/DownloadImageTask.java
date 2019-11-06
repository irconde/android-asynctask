package edu.ualr.asynchronousprogramming;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by irconde on 2019-11-05.
 */
// TODO 01. Define a new class that extends the AsyncTask class
// We have to specify the three type parameters that exposes the AsyncTask class
    // Params. Type of the value we pass to doInBackground. URL
    // Progress. Type of the value returned to the main thread while the background thread is running. Integer
    // Result. Type of the value returned by the AsyncTask. Bitmap
public class DownloadImageTask extends AsyncTask<URL, Integer, Bitmap> {

    // TODO 02. Weak reference to the UI View to update
    // The WeakReference does not prevent the view from being garbage collected when the activity
    // where the view was created is no longer active.
    private final WeakReference<ImageView> imageViewRef;

    // TODO 03. Constructor. Initialize the weak reference to the UI View
    public DownloadImageTask(ImageView imageView) {
        this.imageViewRef = new WeakReference<>(imageView);
    }

    // Retrieves the image from a URL
    private Bitmap downloadBitmap(URL url) {
        Bitmap bitmap =null;
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK){
                throw new Exception("Unsucesfull Result code");
            }
            is = conn.getInputStream();
            BufferedInputStream bif = new BufferedInputStream(is) {
                public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
                    int readBytes = super.read(buffer, byteOffset, byteCount);
                    return readBytes;
                }
            };
            Bitmap downloaded = BitmapFactory.decodeStream(bif);
            bitmap = downloaded;
        } catch (Exception e) {
            e.printStackTrace();
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

    // TODO 04. Override doInBackground method to define the operations we want to
    //  execute in a background thread. Download an image from the provided URL
    @Override
    protected Bitmap doInBackground(URL... urls) {
        URL url = urls[0];
        // The IO operation invoked will take a significant ammount
        // to complete
        return downloadBitmap(url);
    }

    // TODO 05. Override onPostExecute method to return the result of the operation executed by the
    //  background thread to the main thread
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = this.imageViewRef.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
