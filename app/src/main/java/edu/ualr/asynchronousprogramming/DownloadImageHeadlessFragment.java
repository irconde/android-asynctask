package edu.ualr.asynchronousprogramming;

/**
 * Created by irconde on 2019-11-06.
 */
// TODO 01. Create a retained headless Fragment, which wraps our AsyncTask
public class DownloadImageHeadlessFragment {

    // TODO 06. We need to define an interface that will allow the AsyncTask to communicate with the
    //  Activity through the fragment and ask it to make updates in the UI

    // TODO 07. We create a new member that will keep a reference to the Activity that receives the async task callbacks

    // TODO 11. The Fragment is now the responsible for creating and executing the AsyncTask,
    //  so we need a new member to store the reference

    // TODO 12. We have to pass the fragment the url value used by the wrapped AsyncTask. Thus, we need to define a newInstance method

    // TODO 13. Override the onCreate method to define the initialization of the several members of the fragment
        // TODO 14. Avoid disposing the fragment when the activity restarts.
        // TODO 15. Create a new instance of the AsyncTask
        // TODO 16. Get an URL with the provided String value
        // TODO 17. Execute the AsyncTask using the URL

    // TODO 08. We override the onAttach method to initialize the listener member

    // TODO 09. We override the onDetach() method to delete the reference to the Activity, stored in listener member

    // TODO 27. Cancel method definition

    // TODO 02. We include our AsyncTask class as inner class of the Fragment

    // TODO 03. The AsyncTask is not longer the responsible for providing the user the progress dialog
    //  or updating the ImageView with the received Bitmap.
    //  The activity is. That's why references to ImageView, Context and ProgressDialog are not longer needed

    // TODO 03. References to ImageView and Context are not longer needed so we can delete

    // TODO 05. We can even delete the constructor

    // TODO 10. We use the listener to send progress updates and results back to the Activity

    // TODO 18. Inside the downloadBitmap method, we publish the initial progress value

    // TODO 04. We move the loadDefaultImage to the Activity
}
