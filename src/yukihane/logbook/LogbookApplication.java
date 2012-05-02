package yukihane.logbook;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class LogbookApplication extends Application {
    public static String TAG = "LOGBOOK";
    public static Facebook mFacebook;
    public static AsyncFacebookRunner mAsyncRunner;
    public static String userUID = null;
    public static String objectID = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged " + getClass().getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate " + getClass().getSimpleName());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory " + getClass().getSimpleName());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate " + getClass().getSimpleName());
    }

}
