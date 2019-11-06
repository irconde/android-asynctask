package edu.ualr.asynchronousprogramming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
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
// TODO 02. Modify the AsyncTask class definition. We'll have a result of type Result<Bitmap> instead of Bitmap
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

    int downloadedBytes = 0;
    int totalBytes = 0;

    public DownloadImageTask(Context ctx, ImageView imageView) {
        this.imageViewRef = new WeakReference<>(imageView);
        this.ctx = new WeakReference<>(ctx);
    }

    @Override
    protected void onPreExecute() {
        if ( ctx != null && ctx.get()!= null ) {
            progressDialog = new ProgressDialog(ctx.get());
            progressDialog.setTitle(R.string.downloading_image);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            // The boolean parameter allows us to specify whether an AsyncTask thread is in a
            // 'interruptible' state, may actually be interrupted or not.
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    DownloadImageTask.this.cancel(false);
                }
            });
            progressDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    // Retrieves the image from a URL
    // TODO 08. We have to modify the downloadBitmap method and make it propagate caught exceptions
    // TODO 08.01 Modify the method's signature
    private Bitmap downloadBitmap(URL url) {
        Bitmap bitmap =null;
        InputStream is = null;
        try {
            if (isCancelled()) {
                return null;
            }
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
            // TODO 08.02. Throw again the exception once it's caught
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
    protected void onCancelled() {
        if ( imageViewRef !=null && imageViewRef.get() != null && ctx !=null && ctx.get() != null ) {
            // TODO 06.02. Replace these two lines with the method invocation
            Bitmap bitmap = BitmapFactory.decodeResource(ctx.get().getResources(), R.drawable.default_photo);
            this.imageViewRef.get().setImageBitmap(bitmap);
        }
        progressDialog.dismiss();
    }

    // TODO 03. Modify the type of the return value of the doInBackground method
    @Override
    protected Bitmap doInBackground(URL... urls) {
        // TODO 04. Create a new instance of the Result class
        // TODO 05. We protect this block using try/catch
        URL url = urls[0];
        // The IO operation invoked will take a significant amount
        // to complete
        Bitmap bitmap = downloadBitmap(url);
        return bitmap;
    }

    // TODO 04. In the onPostExecute method we check for the presence of an Exception in the Result object
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if( progressDialog!=null ) { progressDialog.dismiss(); }
        ImageView imageView = this.imageViewRef.get();
        if (imageView != null) {
            // TODO 05. If an exception was captured we'll show an error message in the logcat and load the default image
            // TODO 06. Since we need to load a default image in several points of the code we'll define a new method: loadDefaultImage
            imageView.setImageBitmap(result);
        }
    }

    // TODO 06.01. Method definition

}
