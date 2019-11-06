package edu.ualr.asynchronousprogramming;

import android.app.ProgressDialog;
import android.content.Context;
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
// We have to specify the three type parameters that exposes the AsyncTask class
    // Params. Type of the value we pass to doInBackground. URL
    // Progress. Type of the value returned to the main thread while the background thread is running. Integer
    // Result. Type of the value returned by the AsyncTask. Bitmap
public class DownloadImageTask extends AsyncTask<URL, Integer, Bitmap> {

    // The WeakReference does not prevent the view from being garbage collected when the activity
    // where the view was created is no longer active.
    private final WeakReference<ImageView> imageViewRef;

    // TODO 01. We add a weak reference to the context
    private final WeakReference<Context> ctx;

    // TODO 03. We define a new ProgressDialog member. It's used to provide the user with information
    //  about the progress of the background
    private ProgressDialog progressDialog;

    // TODO 02. We initialize the context reference in the constructor
    public DownloadImageTask(Context ctx, ImageView imageView) {
        this.imageViewRef = new WeakReference<>(imageView);
        this.ctx = new WeakReference<>(ctx);
    }

    // TODO 04. We override the onPreExecute method to define the actions to be executed before the
    //  background thread starts
    @Override
    protected void onPreExecute() {
        if ( ctx != null && ctx.get()!= null ) {
            // TODO 05. We create a new ProgressDialog instance
            // TODO 06. We initialize the progress dialog.
            // TODO 06.01. Make the progress dialog noncancelable and indeterminate
            // TODO 07. Show the progress dialog
            progressDialog = new ProgressDialog(ctx.get());
            progressDialog.setTitle(R.string.downloading_image);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
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

    @Override
    protected Bitmap doInBackground(URL... urls) {
        URL url = urls[0];
        // The IO operation invoked will take a significant ammount
        // to complete
        return downloadBitmap(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // TODO 08. Hide the progress dialog once the background work is finished
        if ( progressDialog != null ) { progressDialog.dismiss(); }
        ImageView imageView = this.imageViewRef.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
