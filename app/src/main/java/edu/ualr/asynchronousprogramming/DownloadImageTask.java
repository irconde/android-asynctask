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
    private final WeakReference<Context> ctx;
    private ProgressDialog progressDialog;

    // TODO 05. We define two additional members to keep downloaded info bytes count

    public DownloadImageTask(Context ctx, ImageView imageView) {
        this.imageViewRef = new WeakReference<>(imageView);
        this.ctx = new WeakReference<>(ctx);
    }

    // TODO 01. We have to change the settings of the progress dialog
    // TODO 01.01. Initial value of progress is 0
    // TODO 01.02. Maximum value of progress is 100
    // TODO 01.03. Set as determinate
    // TODO 01.04. Set the style of the dialog to STYLE_HORIZONTAL
    @Override
    protected void onPreExecute() {
        if ( ctx != null && ctx.get()!= null ) {
            progressDialog = new ProgressDialog(ctx.get());
            progressDialog.setTitle(R.string.downloading_image);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    // TODO 02. We override the onProgressUpdate method.
    // TODO 03. We'll update the progress bar from the main thread

    // TODO 04. We modify the downloadBitmap method to calculate the progress at each iteration
    //  of the for loop
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
                throw new Exception("Unsuccessful Result code");
            }

            // TODO 06. We initialize progress information storing in totalBytes the content length of the connection
            is = conn.getInputStream();
            BufferedInputStream bif = new BufferedInputStream(is) {

                // TODO 07. We define and initialize a variable to keep track of the progress

                public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
                    int readBytes = super.read(buffer, byteOffset, byteCount);
                    if (readBytes > 0) {
                        // TODO 09. Update actual number of bytes read from the file
                        // int percent = (int) ((((float) downloadedBytes) / ((float) totalBytes)) * 100);
                        // TODO 10. Update the percent of work done
                        // TODO 11. Publish the progress to the main thread
                    }
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
        if ( progressDialog != null ) { progressDialog.dismiss(); }
        ImageView imageView = this.imageViewRef.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
